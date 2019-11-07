package org.cocos2dx.lua;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.joycastle.analytic.gameanalytics.GameAnalyticsHelper;
import com.joycastle.app.R;
import com.joycastle.gameplugin.AdvertiseHelper;
import com.joycastle.gameplugin.AnalyticHelper;
import com.joycastle.gameplugin.GamePlugin;
import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;
import com.joycastle.gamepluginbase.SystemUtil;
import com.joycastle.my_facebook.FacebookHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppActivity extends Activity implements AdapterView.OnItemClickListener {
    static {
        System.loadLibrary("cocos2dlua");
    }

    private static final String TAG = "MainActivity";

    private ArrayList<HashMap<String, OnClickListener>> arrayList = null;

    interface OnClickListener {
        void onClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SystemUtil.getInstance().hideNavigation(this);

        GamePlugin.getInstance().onCreate(this, savedInstanceState);

        GamePlugin.getInstance().setNotifyHandler(new InvokeJavaMethodDelegate() {
            @Override
            public void onFinish(ArrayList<Object> resArrayList) {

            }
        });
        SystemUtil.getInstance().getDebugMode();
        arrayList = new ArrayList<>();

        ///////////////////////////////Analytic///////////////////////////////
        addToArrayList("--------AnalyticHelper", null);
        addToArrayList("setAccoutInfo", new OnClickListener() {
            @Override
            public void onClick() {
                HashMap map = new HashMap();
                map.put("userId", "00001");
                map.put("gender", "male");
                map.put("age", 20);
                AnalyticHelper.getInstance().setAccoutInfo(map);
//                JSONObject reqData = new JSONObject();
//                JSONArray json =new JSONArray();
//                JSONObject jso = new JSONObject();
//                jso.put("test","hello");
//                json.put(jso);
//                reqData.put("json", json);
//                System.out.print("request json : "+reqData.toString());
//                NativeUtil.invokeJavaMethod("com.joycastle.gamepluginbase.SystemUtil","showAlertDialog",reqData.toString(),0);
            }
        });
        addToArrayList("onEvent", new OnClickListener() {
            @Override
            public void onClick() {
                AnalyticHelper.getInstance().onEvent("dead");
            }
        });
        addToArrayList("onEventWithLabel", new OnClickListener() {
            @Override
            public void onClick() {
                AnalyticHelper.getInstance().onEvent("dead", "10");
            }
        });
        addToArrayList("onEventWithData", new OnClickListener() {
            @Override
            public void onClick() {
                HashMap map = new HashMap<>();
                map.put("level", "10");
                map.put("score", 7.5);
                map.put("coin", 100);
                AnalyticHelper.getInstance().onEvent("dead", map);
            }
        });

        ///////////////////////////////Facebook///////////////////////////////
        addToArrayList("--------FaceBook", null);
        FacebookHelper.getInstance().setLoginFunc(new InvokeJavaMethodDelegate() {
            @Override
            public void onFinish(ArrayList<Object> resArrayList) {
                Log.e(TAG, resArrayList.toString());
            }
        });
        FacebookHelper.getInstance().setAppLinkFunc(new InvokeJavaMethodDelegate() {
            @Override
            public void onFinish(ArrayList<Object> resArrayList) {
                Log.e(TAG, resArrayList.toString());
            }
        });
        addToArrayList("openFacebookPage", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().openFacebookPage("", "");
            }
        });
        addToArrayList("isLogin", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = FacebookHelper.getInstance().isLogin();
                showAlert(String.valueOf(result));
            }
        });
        addToArrayList("login", new OnClickListener() {

            @Override
            public void onClick() {
                FacebookHelper.getInstance().login();
//                JSONObject reqData = new JSONObject();
//                JSONArray json =new JSONArray();
//                JSONObject jso = new JSONObject();
//                try {
//                    json.put(jso);
//                    reqData.put("json", json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                System.out.print("request json : "+reqData.toString());
//                NativeUtil.invokeJavaMethod("com.joycastle.my_facebook.FacebookHelper","login",reqData.toString(),-1);
            }
        });
        addToArrayList("logout", new OnClickListener() {

            @Override
            public void onClick() {
                FacebookHelper.getInstance().logout();
//                NativeUtil.invokeJavaMethod("com.joycastle.my_facebook.FacebookHelper","logout","{}",-1);
            }
        });
        addToArrayList("getUserID", new OnClickListener() {

            @Override
            public void onClick() {
                String uid = FacebookHelper.getInstance().getUserID();
                showAlert(uid);
//                NativeUtil.invokeJavaMethod("com.joycastle.my_facebook.FacebookHelper","getUserId","{}",-1);
            }
        });
        addToArrayList("getAccessToken", new OnClickListener() {
            @Override
            public void onClick() {
                String accessToken = FacebookHelper.getInstance().getAccessToken();
                showAlert(accessToken);

            }
        });
        addToArrayList("getUserProfile", new OnClickListener() {

            @Override
            public void onClick() {
                FacebookHelper.getInstance().getUserProfile("me",320, new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
//                NativeUtil.invokeJavaMethod("com.joycastle.my_facebook.FacebookHelper","getUserProfile","{}",1);
            }
        });
        addToArrayList("getInvitableFriends", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().getInvitableFriends(new ArrayList<String>(), 320, new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("getFriends", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().getFriends(320, new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("confirmRequest", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().confirmRequest(new ArrayList<String>(), "title", "message", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("queryRequest", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().queryRequest(new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("acceptRequest", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().acceptRequest("requestId", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("share", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().share("", false, null, "", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("setLevel", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().setLevel(10);
            }
        });
        addToArrayList("getLevel", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().getLevel("fid", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("inviteFriend", new OnClickListener() {
            @Override
            public void onClick() {
                FacebookHelper.getInstance().inviteFriend("", "", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });

        ///////////////////////////////Advertise///////////////////////////////
        addToArrayList("--------AdvertiseHelper", null);
        addToArrayList("showBannerAd", new OnClickListener() {
            @Override
            public void onClick() {
                int height = AdvertiseHelper.getInstance().showBannerAd(true, true);
                Log.e(TAG, height+"");
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","showBannerAd","{}",-1);
            }
        });
        addToArrayList("hideBannerAd", new OnClickListener() {
            @Override
            public void onClick() {
                AdvertiseHelper.getInstance().hideBannerAd();
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","hideBannerAd","{}",-1);
            }
        });
        addToArrayList("isInterstitialAdReady", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = AdvertiseHelper.getInstance().isInterstitialAdReady();
                showAlert(String.valueOf(result));
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","isInterstitialAdReady","{}",-1);
            }
        });
        addToArrayList("showInterstitialAd", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = AdvertiseHelper.getInstance().showInterstitialAd(new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        Log.e(TAG, "showInterstitialAd Result: "+resArrayList);
                        showAlert(resArrayList.toString());
                    }
                });
                Log.e(TAG, "showInterstitialAd: "+result);
//                JSONObject reqData = new JSONObject();
//                JSONArray json = new JSONArray();
//                try {
//                    reqData.put("json", json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","showInterstitialAd",reqData.toString(),0);
            }
        });
        addToArrayList("isVideoAdReady", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = AdvertiseHelper.getInstance().isVideoAdReady();
                showAlert(String.valueOf(result));

//                JSONObject reqData = new JSONObject();
//                JSONArray json =new JSONArray();
//                try {
//                    reqData.put("json", json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","isVideoAdReady",reqData.toString(),-1);
            }
        });
        addToArrayList("showVideoAd", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = AdvertiseHelper.getInstance().showVideoAd(new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
                Log.e(TAG, "showVideoAd: "+result);

//                JSONObject reqData = new JSONObject();
//                JSONArray json =new JSONArray();
//                try {
//                    reqData.put("json", json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                NativeUtil.invokeJavaMethod("com.joycastle.gameplugin.AdvertiseHelper","showVideoAd",reqData.toString(),0);
            }
        });

        ///////////////////////////////GamePlugin///////////////////////////////
        addToArrayList("--------GamePlugin", null);
        addToArrayList("canDoIap", new OnClickListener() {
            @Override
            public void onClick() {
                boolean result = GamePlugin.getInstance().canDoIap();
                showAlert(String.valueOf(result));
            }
        });
        addToArrayList("getSuspensiveIap", new OnClickListener() {
            @Override
            public void onClick() {
                HashMap hashMap = GamePlugin.getInstance().getSuspensiveIap();
                showAlert(hashMap.toString());
            }
        });
        addToArrayList("setSuspensiveIap", new OnClickListener() {
            @Override
            public void onClick() {
                HashMap hashMap = new HashMap();
                GamePlugin.getInstance().setSuspensiveIap(hashMap);
            }
        });
        addToArrayList("doIap", new OnClickListener() {
            @Override
            public void onClick() {
                GamePlugin.getInstance().doIap("blackjack.chip4", "", new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        showAlert(resArrayList.toString());
                    }
                });
            }
        });
        addToArrayList("rateGame", new OnClickListener() {
            @Override
            public void onClick() {
                GamePlugin.getInstance().rateGame();
            }
        });

        ///////////////////////////////SystemUtil///////////////////////////////
        addToArrayList("--------SystemUtil", null);
        addToArrayList("getDebugMode", new OnClickListener() {
            @Override
            public void onClick() {
                int debugMode = SystemUtil.getInstance().getDebugMode();
                showAlert(String.valueOf(debugMode));
            }
        });
        addToArrayList("getPlatCfgValue", new OnClickListener() {
            @Override
            public void onClick() {
                String value = SystemUtil.getInstance().getPlatCfgValue("flurry_key");
                showAlert(value);
            }
        });
        addToArrayList("getAppBundleId", new OnClickListener() {
            @Override
            public void onClick() {
                String packageName = SystemUtil.getInstance().getAppBundleId();
                showAlert(packageName);
            }
        });
        addToArrayList("getAppName", new OnClickListener() {
            @Override
            public void onClick() {
                String appName = SystemUtil.getInstance().getAppName();
                showAlert(appName);
            }
        });
        addToArrayList("getAppVersion", new OnClickListener() {
            @Override
            public void onClick() {
                String versionName = SystemUtil.getInstance().getAppVersion();
                showAlert(versionName);
            }
        });
        addToArrayList("getAppBuild", new OnClickListener() {
            @Override
            public void onClick() {
                int versionCode = SystemUtil.getInstance().getAppBuild();
                showAlert(String.valueOf(versionCode));
            }
        });
        addToArrayList("getDeviceName", new OnClickListener() {
            @Override
            public void onClick() {
                String deviceName = SystemUtil.getInstance().getDeviceName();
                showAlert(deviceName);
            }
        });
        addToArrayList("getDeviceModel", new OnClickListener() {
            @Override
            public void onClick() {
                String deviceModel = SystemUtil.getInstance().getDeviceModel();
                showAlert(deviceModel);
            }
        });
        addToArrayList("getDeviceType", new OnClickListener() {
            @Override
            public void onClick() {
                String deviceType = SystemUtil.getInstance().getDeviceType();
                showAlert(deviceType);
            }
        });
        addToArrayList("getSystemName", new OnClickListener() {
            @Override
            public void onClick() {
                String systemName = SystemUtil.getInstance().getSystemName();
                showAlert(systemName);
            }
        });
        addToArrayList("getSystemVersion", new OnClickListener() {
            @Override
            public void onClick() {
                String systemVersion = SystemUtil.getInstance().getSystemVersion();
                showAlert(systemVersion);
            }
        });
        addToArrayList("getUUID", new OnClickListener() {
            @Override
            public void onClick() {
                String uuid = SystemUtil.getInstance().getUUID();
                showAlert(uuid);
            }
        });
        addToArrayList("getCountryCode", new OnClickListener() {
            @Override
            public void onClick() {
                String countryCode = SystemUtil.getInstance().getCountryCode();
                showAlert(countryCode);
            }
        });
        addToArrayList("getLanguageCode", new OnClickListener() {
            @Override
            public void onClick() {
                String languageCode = SystemUtil.getInstance().getLanguageCode();
                showAlert(languageCode);
            }
        });
        addToArrayList("getCpuTime", new OnClickListener() {
            @Override
            public void onClick() {
                long cpuTime = SystemUtil.getInstance().getCpuTime();
                showAlert(String.valueOf(cpuTime));
            }
        });
        addToArrayList("showLoading & hideLoading", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().showLoading("Loading...");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SystemUtil.getInstance().hideLoading();
                    }
                }, 5000);
            }
        });
        addToArrayList("vibrate", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().vibrate();
            }
        });
        addToArrayList("setNotificationState: true", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().setNotificationState(true);
            }
        });
        addToArrayList("setNotificationState: false", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().setNotificationState(false);
            }
        });
        addToArrayList("postNotification", new OnClickListener() {
            @Override
            public void onClick() {
                HashMap reqData = new HashMap();
                reqData.put("message","♥ ♠ Your FREE BONUS is ready NOW! ♣ ♦");
                reqData.put("delay", 10.0);
                SystemUtil.getInstance().postNotification(reqData);

            }
        });
        addToArrayList("copyToClipboard", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().copyToPasteboard("hello world");
            }
        });
        addToArrayList("setBadgeNum", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().setBadgeNum(0);
            }
        });
        addToArrayList("keychainSet", new OnClickListener() {
            @Override
            public void onClick() {
                SystemUtil.getInstance().keychainSet("hahaha", "hehehe");
            }
        });
        addToArrayList("keychainGet", new OnClickListener() {
            @Override
            public void onClick() {
                String value = SystemUtil.getInstance().keychainGet("hahaha");
                showAlert(value);
            }
        });
        addToArrayList("isRoot", new OnClickListener() {
            @Override
            public void onClick() {
                boolean isRoot = SystemUtil.getInstance().isJailbroken();
                showAlert(String.valueOf(isRoot));
            }
        });

        List<String> data = new ArrayList<>();
        for (HashMap<String, OnClickListener> item : arrayList) {
            data.add(item.entrySet().iterator().next().getKey());
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, data));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HashMap<String, OnClickListener> hashMap = arrayList.get(i);
        final OnClickListener listener = hashMap.entrySet().iterator().next().getValue();
        if (listener == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                listener.onClick();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GamePlugin.getInstance().onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GamePlugin.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GamePlugin.getInstance().onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GamePlugin.getInstance().onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GamePlugin.getInstance().onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GamePlugin.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }

    public void addToArrayList(String name, OnClickListener listener) {
        HashMap<String, OnClickListener> hashMap = new HashMap<>();
        hashMap.put(name, listener);
        arrayList.add(hashMap);
    }

    public void showAlert(String message) {
        message = message==null ? "null" : message;
        final String finalMessage = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(AppActivity.this)
                        .setMessage(finalMessage)
                        .create()
                        .show();
                Log.e(TAG, finalMessage);
            }
        });
    }
}
