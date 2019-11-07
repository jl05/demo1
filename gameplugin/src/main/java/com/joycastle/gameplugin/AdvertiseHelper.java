package com.joycastle.gameplugin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.joycastle.gamepluginbase.AdvertiseDelegate;
import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by gaoyang on 9/29/16.
 */

public class AdvertiseHelper implements AdvertiseDelegate {
    private static final String TAG = "AdvertiseHelper";
    private static AdvertiseHelper instance = new AdvertiseHelper();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private ArrayList<AdvertiseDelegate> mDelegates;

    public static AdvertiseHelper getInstance() { return instance; }

    private AdvertiseHelper() {
        mDelegates = new ArrayList<>();
    }

    @Override
    public void setBannerAdName(String name) {

    }

    @Override
    public void setSpotAdNames(ArrayList names) {

    }

    @Override
    public void setVideoAdNames(ArrayList names) {

    }

    @Override
    public int showBannerAd(final boolean protrait, final boolean bottom) {
        if (mDelegates.size() <= 0) {
            return 0;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegates.get(0).showBannerAd(protrait, bottom);
            }
        });
        return 0;
    }

    @Override
    public void hideBannerAd() {
        if (mDelegates.size() <= 0) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mDelegates.get(0).hideBannerAd();
            }
        });
    }

    @Override
    public boolean isInterstitialAdReady() {
        boolean result = false;
        for (AdvertiseDelegate delegate : mDelegates) {
            result = delegate.isInterstitialAdReady();
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean showInterstitialAd(final InvokeJavaMethodDelegate listener) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                for (AdvertiseDelegate delegate : mDelegates) {
                    result = delegate.showInterstitialAd(new InvokeJavaMethodDelegate() {
                        @Override
                        public void onFinish(ArrayList<Object> resArrayList) {
                            boolean interstitialAdClicked = (boolean) resArrayList.get(0);
                            if (interstitialAdClicked) {
                                AnalyticHelper.getInstance().onEvent("SpotAd Clicked");
                            } else {
                                AnalyticHelper.getInstance().onEvent("SpotAd Dismiss");
                            }
                            listener.onFinish(resArrayList);
                        }
                    });
                    if (result) {
                        break;
                    }
                }
                if (result) {
                    AnalyticHelper.getInstance().onEvent("SpotAd Show Success");
                } else {
                    AnalyticHelper.getInstance().onEvent("SpotAd Show Failed");
                }
            }
        });
        return true;
    }

    @Override
    public boolean isVideoAdReady() {
        boolean result = false;
        for (AdvertiseDelegate delegate : mDelegates) {
            result = delegate.isVideoAdReady();
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean showVideoAd(final InvokeJavaMethodDelegate listener) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                for (AdvertiseDelegate delegate : mDelegates) {
                    result = delegate.showVideoAd(new InvokeJavaMethodDelegate() {
                        @Override
                        public void onFinish(ArrayList<Object> resArrayList) {
                            boolean rewardAdViewed = (boolean) resArrayList.get(0);
                            boolean rewardAdClicked = (boolean) resArrayList.get(1);
                            if (rewardAdViewed) {
                                AnalyticHelper.getInstance().onEvent("VedioAd Play Finish");
                            }
                            if (rewardAdClicked) {
                                AnalyticHelper.getInstance().onEvent("VedioAd Clicked");
                            }
                            listener.onFinish(resArrayList);
                        }
                    });
                    if (result) {
                        break;
                    }
                }
                if (result) {
                    AnalyticHelper.getInstance().onEvent("VedioAd Show Success");
                } else {
                    AnalyticHelper.getInstance().onEvent("VedioAd Show Failed");
                }
            }
        });
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void init(Application application) {
        try {
            Class clazz = Class.forName("com.joycastle.advertise.mopub.MoPubAdvertiseHelper");
            Method method = clazz.getMethod("getInstance");
            AdvertiseDelegate delegate = (AdvertiseDelegate) method.invoke(null);
            mDelegates.add(delegate);
            delegate.init(application);
        } catch (Exception e) {
            Log.e(TAG, "mopub is disable");
        }

//        try {
//            Class clazz = Class.forName("com.joycastle.advertise.admob.AMAdvertiseHelper");
//            Method method = clazz.getMethod("getInstance");
//            AdvertiseDelegate delegate = (AdvertiseDelegate) method.invoke(null);
//            mDelegates.add(delegate);
//            delegate.init(application);
//        } catch (Exception e) {
//            Log.e(TAG, "Admob is disable");
//        }

//        try {
//            Class clazz = Class.forName("com.joycastle.advertise.adcolony.ACAdvertiseHelper");
//            Method method = clazz.getMethod("getInstance");
//            AdvertiseDelegate delegate = (AdvertiseDelegate) method.invoke(null);
//            mDelegates.add(delegate);
//            delegate.init(application);
//        } catch (Exception e) {
//            Log.e(TAG, "Adcolony is disable");
//        }
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onCreate(activity, savedInstanceState);
        }
    }

    @Override
    public void onStart(Activity activity) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onStart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onDestroy(activity);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        for (AdvertiseDelegate delegate : mDelegates) {
            delegate.onActivityResult(activity, requestCode, resultCode, data);
        }
    }
}
