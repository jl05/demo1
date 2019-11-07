package com.joycastle.my_facebook;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;
import com.joycastle.gamepluginbase.LifeCycleDelegate;
import com.joycastle.gamepluginbase.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by geekgy on 16/4/24.
 */
public class FacebookHelper implements LifeCycleDelegate {
    private enum PermissionType {
        READ,
        PUBLISH,
    }

    private interface GrantPermissionListener {
        void onFinish(boolean result);
    }

    private static final String TAG = "FacebookHelper";
    private static FacebookHelper instance = new FacebookHelper();

    private InvokeJavaMethodDelegate loginListener;
    private InvokeJavaMethodDelegate appLinkListener;
    private GrantPermissionListener grantPermissionListener;
    private CallbackManager callbackManager;

    public static FacebookHelper getInstance() {
        return instance;
    }
    private FacebookHelper() {}

    public void openFacebookPage(String installUrl, String url) {
        //TODO
    }

    public void setLoginFunc(InvokeJavaMethodDelegate delegate) {
        this.loginListener = delegate;
    }

    public void setAppLinkFunc(InvokeJavaMethodDelegate delegate) {
        this.appLinkListener = delegate;
    }

    public boolean isLogin(){
        AccessToken token = AccessToken.getCurrentAccessToken();
        return !(token == null || token.isExpired());
    }

    public void login() {
        if (this.isLogin()) {
            return;
        }
        List<String> permissions = Arrays.asList("public_profile", "email");
        grantPermissions(permissions, PermissionType.READ, new GrantPermissionListener() {
            @Override
            public void onFinish(boolean result) {
                Log.e(TAG, "login result: " + result);
            }
        });
    }

    public void logout() {
        if (!this.isLogin())
            return;
        LoginManager.getInstance().logOut();
    }

    public String getUserID() {
        if (!this.isLogin()) {
            return null;
        }
        return Profile.getCurrentProfile().getId();
    }

    public String getAccessToken() {
        if (!this.isLogin()) {
            return null;
        }
        return AccessToken.getCurrentAccessToken().getToken();
    }

    public void getUserProfile(final String fid, final int picSize, final InvokeJavaMethodDelegate delegate){
        List<String> permissions = Arrays.asList("public_profile", "email");
        grantPermissions(permissions, PermissionType.READ, new GrantPermissionListener() {
            @Override
            public void onFinish(boolean result) {
                if (!result) {
                    delegate.onFinish(new ArrayList());
                } else {
                    String graphPath = String.format(Locale.getDefault(), "/%s?fields=id,name,gender,picture.height(%d).width(%d),email", fid, picSize, picSize);
                    GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), graphPath, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            if (response.getError() != null) {
                                delegate.onFinish(new ArrayList());
                            } else {
                                try {
                                    ArrayList<Object> arrayList = new ArrayList<>();
                                    arrayList.add(jsonObject2HashMap(response.getJSONObject()));
                                    delegate.onFinish(arrayList);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    delegate.onFinish(new ArrayList());
                                }
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    public void getInvitableFriends(ArrayList<String> inviteTokens, int picSize, InvokeJavaMethodDelegate delegate) {
        //TODO
    }

    public void getFriends(int picSzie ,InvokeJavaMethodDelegate delegate) {
        //TODO
    }

    public void confirmRequest(final ArrayList<String> fidOrTokens, final String title, final String msg, final InvokeJavaMethodDelegate delegate){
        grantPermissions(Arrays.asList("public_profile"), PermissionType.READ, new GrantPermissionListener() {
            @Override
            public void onFinish(boolean result) {
                if (!result) {
                    delegate.onFinish(new ArrayList());
                } else {
                    GameRequestDialog gameRequestDialog = new GameRequestDialog(SystemUtil.getInstance().getActivity());
                    gameRequestDialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
                        @Override
                        public void onSuccess(GameRequestDialog.Result result) {
                            ArrayList arrayList = new ArrayList();
                            List<String> recipients = result.getRequestRecipients();
                            if (recipients.size() > 0) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("request", result.getRequestId());
                                for (int i=0; i<recipients.size(); i++) {
                                    hashMap.put("to["+i+"]", recipients.get(i));
                                }
                                arrayList.add(hashMap);
                            }
                            delegate.onFinish(arrayList);
                        }

                        @Override
                        public void onCancel() {
                            delegate.onFinish(new ArrayList());
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.e(TAG, error.toString());
                            delegate.onFinish(new ArrayList());
                        }
                    });
                    GameRequestContent content = new GameRequestContent.Builder()
                            .setRecipients(fidOrTokens)
                            .setTitle(title)
                            .setMessage(msg)
                            .build();
                    gameRequestDialog.show(content);
                }
            }
        });
    }

    public void queryRequest(InvokeJavaMethodDelegate delegate) {
        //TODO
    }
    public void acceptRequest(String requestId ,InvokeJavaMethodDelegate delegate) {
        //TODO
    }
    /**
     *
     * @param shareType Links/Photos/Videos/Multimedia/OpenGraph
     * @param customInterface 使用Api
     * @param map 参数
     * @param message 默认消息，只在Api方式下有效
     * @param listener 回调
     */
    //std::string title, std::string i, std::string caption, std::string imageUrl, std::string contentUrl, std::function<void(bool)>& func
    public void share(String shareType, boolean customInterface, Map<String, String> map, String message, InvokeJavaMethodDelegate listener) {
        ShareContent content = null;
        if ("Links".equals(shareType)) {
            ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
            builder.setContentUrl(map.get("ContentUrl") == null ? null : Uri.parse(map.get("ContentUrl")));
            builder.setContentTitle(map.get("ContentTitle"));
            builder.setImageUrl(map.get("ImageUrl") == null ? null : Uri.parse(map.get("ImageUrl")));
            builder.setContentDescription(map.get("ContentDescription"));
            content = builder.build();
        } else if ("Photos".equals(shareType)) {

            Bitmap bitmap = BitmapFactory.decodeFile("");
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
        } else if ("Videos".equals(shareType)) {
            Uri uri = null;
            ShareVideo video = new ShareVideo.Builder()
                    .setLocalUrl(uri)
                    .build();
            content = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .setContentDescription("")
                    .setContentTitle("title")
                    .setPreviewPhoto(null)
                    .build();
        } else if ("Multimedia".equals(shareType)) {
            SharePhoto photo1 = new SharePhoto.Builder()
                    .setBitmap(null)
                    .build();
        } else if ("OpenGraph".equals(shareType)) {
            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "asdadwqdfwe.Jackpot")
                    .putString("og:title", "A Game of Thrones")
                    .build();
            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("asdadwqdfwe.hit")
                    .putObject("jackpot", object)
                    .build();
            content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("jackpot")
                    .setAction(action)
                    .build();
        }
        if (content == null)
            return;
        FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>(){

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i(TAG, "share success");
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "share cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "share failed: "+error.toString());
            }
        };
        if (customInterface) {
            ShareApi shareApi = new ShareApi(content);
            shareApi.setMessage(message);
            shareApi.share(callback);
        } else {
            ShareDialog shareDialog = new ShareDialog(SystemUtil.getInstance().getActivity());
            shareDialog.registerCallback(callbackManager, callback);
            shareDialog.show(content);
        }
    }

    public void setLevel(int level) {
        //TODO
    }

    public void getLevel(String fid, InvokeJavaMethodDelegate delegate) {
        //TODO
    }

    public void inviteFriend(String appLinkURL,String prviewImageURL ,InvokeJavaMethodDelegate delegate) {
        //TODO
    }

    /**
     * 分配权限
     * @param permissions 所需要申请的权限
     * @param permissionType read/publish
     * @param listener 回调
     */
    private void grantPermissions(List<String> permissions, PermissionType permissionType, GrantPermissionListener listener) {
        Set<String> grantedPermissions = new HashSet<>();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            grantedPermissions = accessToken.getPermissions();
        }
        ArrayList<String> needGrantPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!grantedPermissions.contains(permission)) {
                needGrantPermissions.add(permission);
            }
        }
        if (needGrantPermissions.size() <= 0){
            listener.onFinish(true);
            return;
        }
        grantPermissionListener = listener;
        if (permissionType == PermissionType.READ) {
            LoginManager.getInstance().logInWithReadPermissions(SystemUtil.getInstance().getActivity(), needGrantPermissions);
        } else if (permissionType == PermissionType.PUBLISH) {
            LoginManager.getInstance().logInWithPublishPermissions(SystemUtil.getInstance().getActivity(), needGrantPermissions);
        }
    }

    private static ArrayList<Object> jsonArray2ArrayList(JSONArray valArr) throws JSONException {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int i = 0; i < valArr.length(); i++) {
            if (valArr.optJSONArray(i) != null) {
                arrayList.add(jsonArray2ArrayList(valArr.getJSONArray(i)));
            } else if (valArr.optJSONObject(i) != null) {
                arrayList.add(jsonObject2HashMap(valArr.getJSONObject(i)));
            } else {
                arrayList.add(valArr.get(i));
            }
        }
        return arrayList;
    }

    private static HashMap<String, Object> jsonObject2HashMap(JSONObject valObj) throws JSONException {
        HashMap<String, Object> hashMap = new HashMap<>();
        Iterator<String> keys = valObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (valObj.optJSONArray(key) != null) {
                hashMap.put(key, jsonArray2ArrayList(valObj.getJSONArray(key)));
            } else if (valObj.optJSONObject(key) != null) {
                hashMap.put(key, jsonObject2HashMap(valObj.getJSONObject(key)));
            } else {
                hashMap.put(key, valObj.get(key));
            }
        }
        return hashMap;
    }

    @Override
    public void init(Application application) {

    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "facebook login success");
                String userId = AccessToken.getCurrentAccessToken().getUserId();
                String accessToken = AccessToken.getCurrentAccessToken().getToken();
                Log.i(TAG, "userId = "+userId+", accessToken = "+accessToken);
                if (null != FacebookHelper.this.loginListener) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(userId);
                    arrayList.add(accessToken);
                    FacebookHelper.this.loginListener.onFinish(arrayList);
                }
                if(grantPermissionListener!=null)
                    grantPermissionListener.onFinish(true);
            }

            @Override
            public void onCancel() {
                Log.i(TAG,"login cancel!");
                if(grantPermissionListener!=null)
                    grantPermissionListener.onFinish(false);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"login error!");
                Log.e(TAG, error.toString());
                if(grantPermissionListener!=null)
                    grantPermissionListener.onFinish(false);
            }
        });
    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
