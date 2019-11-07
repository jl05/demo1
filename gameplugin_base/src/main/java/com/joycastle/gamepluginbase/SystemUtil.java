package com.joycastle.gamepluginbase;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by geekgy on 16/4/23.
 */


public class SystemUtil {

    static final String SYS_NOTIFY_ACTION = "sys.notify";

    //最大alarm数量
    static final int MAX_ALARM_REQUEST_CODE = 200;
    static final String NOTIFICATION_CHANNEL_ID = "defaults_chanenel_id";
    static final String NOTIFICATION_CHANNEL_NAME = "defaults_chanenel_name";


    private static final String TAG = "SystemUtil";
    private static final String PREFS_FILE = "device_id";
    private static final String PREFS_DEVICE_ID = "device_id";

    private static SystemUtil mInstance = new SystemUtil();

    private Application mApplication;
    private Activity mActivity;
    private KProgressHUD mProgressHUD;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private String mUUID;

    private int mAlarmRequestCode = 0;
    private int mNotificationId = 0;
    private String mNotificationExtra;

    private InvokeJavaMethodDelegate mNotifyDelegate;

    private InvokeJavaMethodDelegate mIapVerifyHandler  = null;
    private InvokeJavaMethodDelegate mUnconsumeHandler  = null;
    private InvokeJavaMethodDelegate mConsumeHanlder    = null;

    private final static int[][] AnalyticDataDiscrete = {
            {0, 10, 10},
            {11, 20, 20},
            {21, 30, 30},
            {31, 40, 40},
            {41, 60, 60},
            {61, 80, 80},
            {81, 100, 100},
            {101, 150, 150},
            {151, 200, 200},
            {201, 250, 250},
            {251, 300, 300},
            {301, 350, 350},
            {351, 400, 400},
            {401, 500, 500},
            {501, 600, 600},
            {601, 700, 700},
            {701, 800, 800},
            {801, 900, 900},
            {901, 1000, 1000},
            {1001, 1200, 1200},
            {1201, 1400, 1400},
            {1401, 1600, 1600},
            {1601, 1800, 1800},
            {1801, 2000, 2000},
            {2001, 2500, 2500},
            {2501, 3000, 3000},
            {3001, 3500, 3500},
            {3501, 4000, 4000},
            {4001, 4500, 4500},
            {4501, 5000, 5000},
            {5001, 6000, 6000},
            {6001, 7000, 7000},
            {7001, 8000, 8000},
            {8001, 9000, 9000},
            {9001, 10000, 10000}
    };

    public static SystemUtil getInstance() { return mInstance; }

    public void setApplication(Application application) {
        this.mApplication = application;
    }

    public Application getApplication() {
        return this.mApplication;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    public Activity getActivity() {
        return this.mActivity;
    }

    public void onCreate() {
//        this.changeSPLocation();
        this.systemOnCreate();
    }

    public void onResume(Activity activity) {
        this.systemOnResume(activity);
    }

    /**
     * 设置系统通知点击的回调函数
     * @param delegate
     */
    public void setNotifyLaunchAppHandler(InvokeJavaMethodDelegate delegate){
        this.mNotifyDelegate = delegate;
    }

    /**
     * 执行点击系统通知的回调
     */
    public void exeNotifyHandler(HashMap extra) {
        if (this.mNotifyDelegate != null) {
            ArrayList extraArr = new ArrayList();
            extraArr.add(extra);
            this.mNotifyDelegate.onFinish(extraArr);
        }
    }

    /**
     * 设置内购验证的函数handler
     * @param delegate
     */
    public void setIapVerifyHandler(InvokeJavaMethodDelegate delegate) {
        this.mIapVerifyHandler = delegate;
    }

    /**
     * 执行内购验证
     * @param extra
     */
    public void exeIapVerifyHandler(HashMap extra) {
        if (this.mIapVerifyHandler != null) {
            ArrayList extraArr = new ArrayList();
            extraArr.add(extra);
            this.mIapVerifyHandler.onFinish(extraArr);
        }
    }

    /**
     * 设置未消费订单处理的handler
     * @param delegate
     */
    public void setUnconsumedHandler(InvokeJavaMethodDelegate delegate) {
        this.mUnconsumeHandler = delegate;
    }

    /**
     * 检查未消费订单handler是否可用
     * @return
     */
    public boolean checkUnconsumedHandler() {
        return this.mUnconsumeHandler != null;
    }

    /**
     * 执行未消费订单handler
     * @param extra
     */
    public void exeUnconsumedHandler(HashMap extra) {
        if (this.mUnconsumeHandler != null) {
            ArrayList extraArr = new ArrayList();
            extraArr.add(extra);
            this.mUnconsumeHandler.onFinish(extraArr);
        }
    }

    /**
     * 设置订单消费的handler
     * @param delegate
     */
    public void setConsumeHandler(InvokeJavaMethodDelegate delegate) {
        this.mConsumeHanlder = delegate;
    }

    /**
     * 执行订单验证的结果
     * @param isSuccess
     * @param environment
     */
    public void executeVerifyResult(boolean isSuccess, String environment) {
        final boolean _isSuccess    =  isSuccess;
        final String _environment   =  environment;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                HashMap extra = new HashMap();
                extra.put("isSuccess", _isSuccess);
                extra.put("environment", _environment);
                ArrayList extraArr = new ArrayList();
                extraArr.add(extra);
                SystemUtil.getInstance().mConsumeHanlder.onFinish(extraArr);
            }}
            );
    }

    /**
     * 获取运行模式
     * @return
     */
    public int getDebugMode() {
//        String isDebug = BuildConfig.DEBUG ? "0" : "1";
        //TODO: 1 DEBUG, 2 RELEASE, 3 SUBMISSION
        Integer gameMode = Integer.parseInt(this.mApplication.getString(R.string.game_mode));
        return gameMode;
    }

    /**
     * 获取manifest中meta-data的值
     * @param key
     * @return
     */
    public String getPlatCfgValue(String key) {
        String value = "";
        try {
            PackageManager packageManager = mApplication.getPackageManager();
            String packageName = mApplication.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            Bundle metaData = packageInfo.applicationInfo.metaData;
            Object obj = metaData.get(key);
            if (obj != null) {
                value = obj.toString();
            } else {
                Log.e(TAG, "meta-data: " + key + " didn't declare");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取包名
     * @return
     */
    public String getAppBundleId() {
        return mApplication.getPackageName();
    }

    /**
     * 获取App名称
     * @return
     */
    public String getAppName() {
        PackageManager pm = mApplication.getPackageManager();
        return mApplication.getApplicationInfo().loadLabel(pm).toString();
    }

    /**
     * 获取VersionName
     * @return
     */
    public String getAppVersion() {
        String versionName = "default";
        try {
            PackageInfo packageInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取VersionCode
     * @return
     */
    public int getAppBuild() {
        int versionCode = -1;
        try {
            PackageInfo packageInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 设备厂商
     * @return
     */
    public String getDeviceName() {
        return android.os.Build.BRAND;
    }

    /**
     * 手机设备型号
     * @return
     */
    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public String getDeviceType() {
        return Build.ID;
    }

    public String getSystemName() {
        return "Android OS";
    }

    /**
     * 系统版本号
     * @return
     */
    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getIDFV() {
        //TODO
        return "";
    }

    public String getIDFA() {
        //TODO
        return "";
    }

    /**
     * 获取android顶部刘海区域的高度
     * @return
     */
    public int getTopNotchHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            View decorView = this.mActivity.getWindow().getDecorView();
            DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null){
                List<Rect> rects = displayCutout.getBoundingRects();
                Rect rect   = rects.get(0);
                int top     = rect.top;
                int bottom  = rect.bottom;
                int right   = rect.right;
                int left    = rect.left;
                return bottom;
            }
            else{
                return 0;
            }
        }
        return 0;
    }

    /**
     * dp 转 pixel
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public String getUUID() {
        if (mUUID == null) {
            synchronized (SystemUtil.class) {
                if (mUUID == null) {
                    Context context = this.mApplication.getApplicationContext();
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        mUUID = id;
                    } else {
                        mUUID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, mUUID).commit();
                    }
                }
            }
        }
        return mUUID;
    }

    public String getCountryCode() {
        return mApplication.getResources().getConfiguration().locale.getCountry();
    }

    public String getLanguageCode() {
        return mApplication.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 获取启动时间
     * @return
     */
    public long getCpuTime() {
        return SystemClock.elapsedRealtime()/1000;
    }

    public String getNetworkState() {
        String ret = "NotReachable";
        ConnectivityManager manager = (ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    ret = "ReachableViaWiFi";
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    ret = "ReachableViaWWAN";
                }
            }
        }
        return ret;
    }

    /**
     * 隐藏虚拟键
     */
    public static void hideNavigation(Activity context) {

        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)) {
//            Logger.get().d("myth hideNavigation  " + context.getClass().getSimpleName());
            context.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    public void showAlertDialog(final String title, final String message, final String btnTitle1, final String btnTitle2, final InvokeJavaMethodDelegate delegate) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(mActivity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(btnTitle1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<Object> arrayList = new ArrayList<>();
                                arrayList.add(true);
                                delegate.onFinish(arrayList);
                            }
                        })
                        .setNegativeButton(btnTitle2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<Object> arrayList = new ArrayList<>();
                                arrayList.add(false);
                                delegate.onFinish(arrayList);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    public void showProgressDialog(String message, int percent) {
        assert(false);
    }

    public void hideProgressDialog() {
        assert(false);
    }

    public void showLoading(final String message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressHUD != null) {
                    return;
                }
                mProgressHUD = KProgressHUD.create(mActivity)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(message)
                        .setCancellable(false)
                        .show();
            }
        });

    }

    public void hideLoading() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressHUD == null) {
                    return;
                }
                mProgressHUD.dismiss();
                mProgressHUD = null;
            }
        });
    }

    public void showMessage(String message) {
        assert(false);
    }

    public void vibrate() {
        Vibrator mVibrator = (Vibrator) mApplication.getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(new long[]{1000, 3000}, -1);
    }

    public void saveImage(String imgPath, String album) {
        assert(false);
    }

    public void sendEmail(String subject, ArrayList<String> toRecipients, String emailBody) {
        assert(false);
        String[] reciver = new String[] { "testgameplugin@gmail.com" };
        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
        myIntent.setType("plain/text");
//        myIntent.setType("message/rfc822");
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
        mActivity.startActivity(Intent.createChooser(myIntent, "mail test"));
    }

    public void setNotificationState(boolean enabled) {
        if (!enabled) {
            NotificationManager notificationManager =
                    (NotificationManager) this.mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    /**
     * 注册系统通知
     * @param notifications
     */
    public void postNotification(HashMap<String, Object> notifications) {
//        Log.e(TAG, "postNotification: " + notifications.toString());
        int notiTime = 0;
        String content      = (String)notifications.get("message");
        Object delayObject  = notifications.get("delay");
        String from         = (String)notifications.get("from");
//        Integer badge       = (Integer)notifications.get("badge");
        if (delayObject instanceof Double) {
            notiTime = ((Double)delayObject).intValue();
        } else {
            notiTime = (int) delayObject;
        }

        this.postNotificationEx("",content, notiTime, from);
    }

    public void setBadgeNum(int num) {
        if (num <= 0) {
            num = 0;
        } else {
            num = Math.max(0, Math.min(num, 99));
        }
        ShortcutBadger.applyCount(this.mActivity, num);
    }


    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     *         "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }

    public void share() {
        assert(false);
    }

    /**
     * 修改 SharedPreferences 文件的路径
     */
    private void changeSPLocation() {
        try {
            Field field;
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(this);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(android.os.Environment.getExternalStorageDirectory().getPath());
            // 修改mPreferencesDir变量的值
            field.set(obj, file);

        }catch (NoSuchFieldException ne){
            ne.printStackTrace();
        }catch (IllegalAccessException ae){
            ae.printStackTrace();;
        }
    }

    public void keychainSet(String key, String value) {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("blackData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String keychainGet(String key) {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("blackData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key,"");
        return value;
    }

    /**
     * 判断手机是否ROOT
     */
    public boolean isJailbroken() {
        boolean root = false;
        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    public void copyToPasteboard(String str) {
        Context context = this.mApplication.getApplicationContext();
        ClipboardManager clip = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//        clip.getText(); // 粘贴
        clip.setText(str); // 复制
    }

    public void requestUrl(String requestType, String url, HashMap<String, Object> map, final InvokeJavaMethodDelegate delegate) {
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (requestType.equalsIgnoreCase("post")) {
                JSONObject jsonObject = new JSONObject();
                Iterator<String> it = map.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    jsonObject.put(key, map.get(key));
                }
                String jsonStr = jsonObject.toString();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
                builder.post(body);
            }
            Request request = builder.build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(false);
                    delegate.onFinish(arrayList);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(true);
                    arrayList.add(response.body().string());
                    delegate.onFinish(arrayList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册系统通知
     * @param title
     * @param msg
     * @param seconds
     * @param extra
     */
    public void postNotificationEx(String title, String msg, int seconds, String extra) {
        if (this.mAlarmRequestCode >= MAX_ALARM_REQUEST_CODE) {
            Log.w(TAG, "alarm is too more");
            return;
        }

        long futureInMillis = System.currentTimeMillis() + seconds * 1000;

        Intent publisherIntent = new Intent(this.mActivity, NotificationPublisher.class);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, this.mNotificationId);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_TITLE, title);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_MSG, msg);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_WHEN, futureInMillis);
        publisherIntent.putExtra(NotificationPublisher.NOTIFICATION_EXTRA, extra);
        PendingIntent publisherPendingIntent = PendingIntent
                .getBroadcast(this.mActivity, this.mAlarmRequestCode, publisherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.mActivity.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, publisherPendingIntent);
        }

        this.mAlarmRequestCode++;
        this.mNotificationId++;
    }

    private void systemOnCreate() {
        // 创建 notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager notificationManager = this.mActivity.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        // 初始化 notification id
        mNotificationId = Math.round(System.currentTimeMillis()/1000);

        // 记录是否从通知打开
        Intent intent = this.mActivity.getIntent();
        this.mNotificationExtra = intent.getStringExtra(NotificationPublisher.NOTIFICATION_EXTRA);
    }

    /**
     * 取消指定通知
     * @param notifyId
     */
    public void cancelNotificationById(String notifyId) {
        AlarmManager alarmManager = (AlarmManager)this.mActivity.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < MAX_ALARM_REQUEST_CODE; i++) {
            Intent publisherIntent = new Intent(this.mActivity, NotificationPublisher.class);
            PendingIntent publisherPendingIntent = PendingIntent
                    .getBroadcast(this.mActivity, i, publisherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            String nId =  publisherIntent.getStringExtra(NotificationPublisher.NOTIFICATION_EXTRA);

            if (notifyId.equals(nId)) {
                if (alarmManager != null) {
                    alarmManager.cancel(publisherPendingIntent);
                }
            }
        }
    }

    private void systemOnResume(Activity activity) {
        // 取消已注册的 notification publisher
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < MAX_ALARM_REQUEST_CODE; i++) {
            Intent publisherIntent = new Intent(activity, NotificationPublisher.class);
            PendingIntent publisherPendingIntent = PendingIntent
                    .getBroadcast(activity, i, publisherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager != null) {
                alarmManager.cancel(publisherPendingIntent);
            }
        }
        // 清除通知
        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        this.mAlarmRequestCode = 0;
    }

    private static JSONArray arrayList2JsonArray(ArrayList arrayList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object val : arrayList) {
            if (val instanceof ArrayList) {
                jsonArray.put(arrayList2JsonArray((ArrayList) val));
            } else if (val instanceof HashMap) {
                jsonArray.put(hashMap2JsonObject((HashMap) val));
            } else {
                jsonArray.put(val);
            }
        }
        return jsonArray;
    }

    public static JSONObject hashMap2JsonObject(HashMap hashMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Iterator it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object val = hashMap.get(key);
            if (val instanceof ArrayList) {
                jsonObject.put(key, arrayList2JsonArray((ArrayList) val));
            } else if (val instanceof HashMap) {
                jsonObject.put(key, hashMap2JsonObject((HashMap) val));
            } else {
                jsonObject.put(key, val);
            }
        }
        return jsonObject;
    }


    public int calculateDiscreteNum(int acNum) {
        int markNum = -1;
        for (int i = 0; i < AnalyticDataDiscrete.length; i++) {
            if ( acNum >= AnalyticDataDiscrete[i][0] && acNum <= AnalyticDataDiscrete[i][1])
            {
                markNum = AnalyticDataDiscrete[i][2];
                Log.i(TAG, "calculateDiscreteNum: "+acNum+" - "+markNum);
                return markNum;
            }
        }
        return markNum;
    }

}


















