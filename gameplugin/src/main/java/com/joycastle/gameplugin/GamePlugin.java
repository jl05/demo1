package com.joycastle.gameplugin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;
import com.joycastle.gamepluginbase.LifeCycleDelegate;
import com.joycastle.gamepluginbase.SystemUtil;
import com.joycastle.iab.googleplay.GoogleIabHelper;
import com.joycastle.my_facebook.FacebookHelper;
import com.joycastle.analytic.kochava.KCAnalyticHelper;
import com.joycastle.analytic.gameanalytics.GameAnalyticsHelper;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by geekgy on 16/5/11.
 */
public class GamePlugin implements LifeCycleDelegate {
    private static final String TAG = "GamePlugin";
    private static GamePlugin instance = new GamePlugin();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private InvokeJavaMethodDelegate mNotifyDelegate;

    public static GamePlugin getInstance() { return instance; }

    private static long acInitTime  = 0;
    private static long fbInitTime  = 0;
    private static long iabInitTime = 0;
    private static long kcInitTime  = 0;
    private static long gaInitTime  = 0;



    private GamePlugin() {}

    @Override
    public void init(Application application) {
        final  Application mApplication = application;

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdvertiseHelper.getInstance().init(mApplication);
            }
        },6000);


        BackgroundThread.prepareThread();
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                SystemUtil.getInstance().setApplication(mApplication);

                long st1 = System.currentTimeMillis();
                AnalyticHelper.getInstance().init(mApplication);
                long st2 = System.currentTimeMillis();
                acInitTime = SystemUtil.getInstance().calculateDiscreteNum((int)(st2 - st1)) ;
                FacebookHelper.getInstance().init(mApplication);
                long st3 = System.currentTimeMillis();
                fbInitTime = SystemUtil.getInstance().calculateDiscreteNum((int)(st3 - st2));
                GoogleIabHelper.getInstance().init(mApplication);
                long st4 = System.currentTimeMillis();
                iabInitTime = SystemUtil.getInstance().calculateDiscreteNum((int)(st4 - st3));
                KCAnalyticHelper.getInstance().init(mApplication);
                long st5 = System.currentTimeMillis();
                kcInitTime = SystemUtil.getInstance().calculateDiscreteNum((int)(st5 - st4));
                GameAnalyticsHelper.getInstance().init(mApplication);
                long st6 = System.currentTimeMillis();
                gaInitTime = SystemUtil.getInstance().calculateDiscreteNum((int)(st6 - st5));
            }
        });
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {

        final Activity mActivity    = activity;
        final Bundle mState         = savedInstanceState;

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdvertiseHelper.getInstance().onCreate(mActivity, mState);
            }
        },6000);

        BackgroundThread.prepareThread();
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                SystemUtil.getInstance().setActivity(mActivity);
                SystemUtil.getInstance().onCreate();

                long st1 = System.currentTimeMillis();
                AnalyticHelper.getInstance().onCreate(mActivity, mState);
                final long st2 = System.currentTimeMillis();

                HashMap<String, Object> ed = new HashMap<>();
                ed.put("AnalyticHelperInitTime", acInitTime);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed);
                Log.i(TAG, "AnalyticHelperInitTime: "+acInitTime);

                HashMap<String, Object> ed2 = new HashMap<>();
                ed2.put("FacebookHelperInitTime", fbInitTime);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed2);
                Log.i(TAG, "FacebookHelperInitTime: "+fbInitTime);

                HashMap<String, Object> ed3 = new HashMap<>();
                ed3.put("GoogleIabHelperInitTime", iabInitTime);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed3);
                Log.i(TAG, "GoogleIabHelperInitTime: "+iabInitTime);

                HashMap<String, Object> ed4 = new HashMap<>();
                ed4.put("KCAnalyticHelperInitTime", kcInitTime);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed4);
                Log.i(TAG, "KCAnalyticHelperInitTime: "+kcInitTime);

                HashMap<String, Object> ed5 = new HashMap<>();
                ed5.put("GameAnalyticsHelperInitTime", gaInitTime);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed5);
                Log.i(TAG, "GameAnalyticsHelperInitTime: "+gaInitTime);

                HashMap<String, Object> ed6 = new HashMap<>();
                int d = SystemUtil.getInstance().calculateDiscreteNum((int)(st2-st1));
                ed6.put("AnalyticHelperOnCreateTime", d);
                AnalyticHelper.getInstance().onEvent("GameStartTime", ed6);
                Log.i(TAG, "AnalyticHelperOnCreateTime: "+d);

                FacebookHelper.getInstance().onCreate(mActivity, mState);
                long st3 = System.currentTimeMillis();
                GoogleIabHelper.getInstance().onCreate(mActivity, mState);
                long st4 = System.currentTimeMillis();
                KCAnalyticHelper.getInstance().onCreate(mActivity, mState);
                long st5 = System.currentTimeMillis();
                GameAnalyticsHelper.getInstance().onCreate(mActivity, mState);
                long st6 = System.currentTimeMillis();

                HashMap<String, Object> eventData = new HashMap<>();
                int d1 = SystemUtil.getInstance().calculateDiscreteNum((int)(st3-st2));
                eventData.put("FacebookHelperOnCreateTime", d1);
                AnalyticHelper.getInstance().onEvent("GameStartTime", eventData);
                Log.i(TAG, "FacebookHelperOnCreateTime: "+d1);

                HashMap<String, Object> eventData1 = new HashMap<>();
                int d2 = SystemUtil.getInstance().calculateDiscreteNum((int)(st4-st3));
                eventData1.put("GoogleIabHelperOnCreateTime", d2);
                AnalyticHelper.getInstance().onEvent("GameStartTime", eventData1);
                Log.i(TAG, "GoogleIabHelperOnCreateTime: "+d2);

                HashMap<String, Object> eventData2 = new HashMap<>();
                int d3 = SystemUtil.getInstance().calculateDiscreteNum((int)(st5-st4));
                eventData2.put("KCAnalyticHelperOnCreateTime", d3);
                AnalyticHelper.getInstance().onEvent("GameStartTime", eventData2);
                Log.i(TAG, "KCAnalyticHelperOnCreateTime: "+d3);

                HashMap<String, Object> eventData3 = new HashMap<>();
                int d4 = SystemUtil.getInstance().calculateDiscreteNum((int)(st6-st5));
                eventData3.put("GameAnalyticsHelperOnCreateTime", d4);
                AnalyticHelper.getInstance().onEvent("GameStartTime", eventData3);
                Log.i(TAG, "GameAnalyticsHelperOnCreateTime: "+d4);
            }
        });
    }

    @Override
    public void onStart(Activity activity) {
        AnalyticHelper.getInstance().onStart(activity);
        AdvertiseHelper.getInstance().onStart(activity);
        FacebookHelper.getInstance().onStart(activity);
        GoogleIabHelper.getInstance().onStart(activity);
        KCAnalyticHelper.getInstance().onStart(activity);
        GameAnalyticsHelper.getInstance().onStart(activity);
    }

    @Override
    public void onResume(Activity activity) {
        AnalyticHelper.getInstance().onResume(activity);
        AdvertiseHelper.getInstance().onResume(activity);
        FacebookHelper.getInstance().onResume(activity);
        GoogleIabHelper.getInstance().onResume(activity);
        KCAnalyticHelper.getInstance().onResume(activity);
        GameAnalyticsHelper.getInstance().onResume(activity);
        SystemUtil.getInstance().onResume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        AnalyticHelper.getInstance().onPause(activity);
        AdvertiseHelper.getInstance().onPause(activity);
        FacebookHelper.getInstance().onPause(activity);
        GoogleIabHelper.getInstance().onPause(activity);
        KCAnalyticHelper.getInstance().onResume(activity);
        GameAnalyticsHelper.getInstance().onResume(activity);
    }

    @Override
    public void onStop(Activity activity) {
        AnalyticHelper.getInstance().onStop(activity);
        AdvertiseHelper.getInstance().onStop(activity);
        FacebookHelper.getInstance().onStop(activity);
        GoogleIabHelper.getInstance().onStop(activity);
        KCAnalyticHelper.getInstance().onStop(activity);
        GameAnalyticsHelper.getInstance().onStop(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        AnalyticHelper.getInstance().onDestroy(activity);
        AdvertiseHelper.getInstance().onDestroy(activity);
        FacebookHelper.getInstance().onDestroy(activity);
        GoogleIabHelper.getInstance().onDestroy(activity);
        KCAnalyticHelper.getInstance().onDestroy(activity);
        GameAnalyticsHelper.getInstance().onDestroy(activity);
        BackgroundThread.destroyThread();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        AnalyticHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        AdvertiseHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        FacebookHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        GoogleIabHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        KCAnalyticHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
        GameAnalyticsHelper.getInstance().onActivityResult(activity, requestCode, resultCode, data);
    }

    public void setNotifyHandler(InvokeJavaMethodDelegate delegate) {
        mNotifyDelegate = delegate;
        SystemUtil.getInstance().setNotifyLaunchAppHandler(delegate);
    }

    public void setIapVerifyUrlAndSign(String url, String sign) {
        GoogleIabHelper.getInstance().setIapVerifyUrlAndSign(url, sign);
    }

    public boolean canDoIap() {
        return GoogleIabHelper.getInstance().canDoIap();
    }

    public HashMap getSuspensiveIap() {
        return GoogleIabHelper.getInstance().getSuspensiveIap();
    }

    public void setSuspensiveIap(HashMap iapInfo) {
        GoogleIabHelper.getInstance().setSuspensiveIap(iapInfo);
    }

    public void doIap(final String iapId, final String userId, final InvokeJavaMethodDelegate delegate) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                GoogleIabHelper.getInstance().doIap(iapId, userId, delegate);
            };
        });
    }

    /**
     * 设置内购验证的回调
     * @param delegate
     */
    public void setIapVerifyHandler(InvokeJavaMethodDelegate delegate) {
        SystemUtil.getInstance().setIapVerifyHandler(delegate);
    }

    /**
     * 执行内购验证
     * @param extra
     */
    public void exeIapVerifyHandler(HashMap extra) {
        SystemUtil.getInstance().exeIapVerifyHandler(extra);
    }

    /**
     * 设置未消费订单的回调
     * @param delegate
     */
    public void setUnconsumedHandler(InvokeJavaMethodDelegate delegate) {
        SystemUtil.getInstance().setUnconsumedHandler(delegate);
    }

    /**
     * 执行验证未消费订单
     * @param extra
     */
    public void exeUnconsumedHandler(HashMap extra) {
        SystemUtil.getInstance().exeUnconsumedHandler(extra);
    }

    /**
     * 执行验证后订单的处理
     * @param isSuccesscrashReportException
     * @param environment
     */
    public void executeVerifyResult(boolean isSuccess, String environment) {
        SystemUtil.getInstance().executeVerifyResult(isSuccess, environment);
    }

    /**
     * 主动处理未验证订单
     * @param delegate
     */
    public void restoreTransactions(InvokeJavaMethodDelegate delegate) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                GoogleIabHelper.getInstance().restoreTransactions();
            };
        });
    }

    public void rateGame() {
        Activity activity = SystemUtil.getInstance().getActivity();
        final String appPackageName = SystemUtil.getInstance().getAppBundleId(); // getAppBundleId() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    /**
     * 需要自己控制生命周期，在这个生命周期内都可以使用这个线程
     *
     */
    public static class BackgroundThread extends HandlerThread {
        private static BackgroundThread mInstance;
        private static Handler mHandler;

        public BackgroundThread() {
            super("ThreadName", android.os.Process.THREAD_PRIORITY_DEFAULT);
        }

        public static void prepareThread() {
            if (mInstance == null) {
                mInstance = new BackgroundThread();
                // 创建HandlerThread后一定要记得start()
                mInstance.start();
                // 获取HandlerThread的Looper,创建Handler，通过Looper初始化
                mHandler = new Handler(mInstance.getLooper());
            }
        }

        /**
         * 如果需要在后台线程做一件事情，那么直接调用post方法，使用非常方便
         */
        public static void post(final Runnable runnable) {
            if(mHandler != null)
                mHandler.post(runnable);
        }

        public static void postDelayed(final Runnable runnable, long nDelay) {
            if(mHandler != null)
                mHandler.postDelayed(runnable, nDelay);
        }

        /**
         * 退出HandlerThread
         */
        public static void destroyThread() {
            if (mInstance != null) {
                mInstance.quit();
                mInstance = null;
                mHandler = null;
            }
        }
    }


}
