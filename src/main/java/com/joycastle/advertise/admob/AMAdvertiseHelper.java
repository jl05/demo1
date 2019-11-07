package com.joycastle.advertise.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.joycastle.gamepluginbase.AdvertiseDelegate;
import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;
import com.joycastle.gamepluginbase.SystemUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by gaoyang on 9/29/16.
 */

@SuppressLint("MissingPermission")
public class AMAdvertiseHelper implements AdvertiseDelegate, RewardedVideoAdListener {
    private static final String TAG = "AMAdvertiseHelper";

    private static AMAdvertiseHelper instance = new AMAdvertiseHelper();

    private Class<MediationAdapter> vungleClass = null;
    private Bundle vungleExtras = null;

    private String appId = null;
    private String bannerId = null;
    private String interstitialId = null;
    private String videoId = null;
    private String testDeviceId = null;

    private AdView bannerAd = null;
    private InterstitialAd interstitialAd = null;
    private RewardedVideoAd mRewardedVideoAd = null;

    private boolean isInterstitialAdLoaded = false;
    private boolean isRewardAdLoaded = false;

    private boolean interstitialAdClicked = false;
    private InvokeJavaMethodDelegate interstitialAdListener = null;

    private boolean rewardAdViewed = false;
    private boolean rewardAdClicked = false;
    private InvokeJavaMethodDelegate rewardAdListener = null;

    public static AMAdvertiseHelper getInstance() {
        return instance;
    }

    private AMAdvertiseHelper() {}

    public void setVungle(Class clazz , Bundle extras){
        vungleClass = clazz;
        vungleExtras = extras;
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
        if(bannerAd==null) return 0;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bannerAd.getLayoutParams();
        layoutParams.gravity = bottom ? Gravity.BOTTOM : Gravity.TOP;
        bannerAd.setLayoutParams(layoutParams);
        bannerAd.setVisibility(View.VISIBLE);
        return bannerAd.getHeight();
    }

    @Override
    public void hideBannerAd() {
        if(bannerAd==null) return;
        bannerAd.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean isInterstitialAdReady() {
        return isInterstitialAdLoaded;
    }

    @Override
    public boolean showInterstitialAd(InvokeJavaMethodDelegate listener) {
        if (!this.isInterstitialAdReady()) return false;
        interstitialAdListener = listener;
        interstitialAdClicked = false;
        interstitialAd.show();
        return true;
    }

    @Override
    public boolean isVideoAdReady() {
        return isRewardAdLoaded;
    }

    @Override
    public boolean showVideoAd(InvokeJavaMethodDelegate listener) {
        if (mRewardedVideoAd == null) return false;
        if (!isVideoAdReady()) return false;
        rewardAdListener = listener;
        rewardAdViewed = false;
        rewardAdClicked = false;
        mRewardedVideoAd.show();
        return true;
    }

    @Override
    public String getName() {
        return "Admob";
    }

    @Override
    public void init(Application application) {
        try {
            Class clazz = Class.forName("com.joycastle.advertise.admob.adapter.facebook.FBAdvertiseHelper");
            Method getInstanceMethod = clazz.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method method = clazz.getMethod("init", Application.class);
            method.invoke(instance, application);
        } catch (Exception e) {
            Log.e(TAG, "facebook adapter is disable");
            e.printStackTrace();
        }

        try {
            Class clazz = Class.forName("com.joycastle.advertise.admob.adapter.applovin.APAdvertiseHelper");
            Method getInstanceMethod = clazz.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method method = clazz.getMethod("init", Application.class);
            method.invoke(instance, application);
        } catch (Exception e) {
            Log.e(TAG, "applovin adapter is disable");
            e.printStackTrace();
        }

        Log.i(TAG, "Admob installed");
    }

    /**
     * 预加载Banner
     */
    private void requestNewBanner() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(testDeviceId)
                .build();
        bannerAd.loadAd(adRequest);
    }

    /**
     * 预加载Interstitial
     */
    private void requestNewInterstitial() {
        if (interstitialAd.isLoaded())
            return;
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(testDeviceId);
        if (vungleClass != null && vungleExtras != null) {
            builder.addNetworkExtrasBundle(vungleClass, vungleExtras);
        }
        interstitialAd.loadAd(builder.build());
    }

    /**
     * 预加载Video
     */
    private void requestNewVideo(){
        if (mRewardedVideoAd.isLoaded())
            return;
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(testDeviceId);
        if (vungleClass != null && vungleExtras != null) {
            builder.addNetworkExtrasBundle(vungleClass, vungleExtras);
        }
        mRewardedVideoAd.loadAd(videoId, builder.build());
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        appId = SystemUtil.getInstance().getPlatCfgValue("admob_app_id");
        bannerId = SystemUtil.getInstance().getPlatCfgValue("admob_banner_id");
        interstitialId = SystemUtil.getInstance().getPlatCfgValue("admob_interstitial_id");
        videoId = SystemUtil.getInstance().getPlatCfgValue("admob_video_id");
        testDeviceId = SystemUtil.getInstance().getPlatCfgValue("admob_test_device_id");

        MobileAds.initialize(activity, appId);

        // init banner ad
//        bannerAd = new AdView(activity);
//        bannerAd.setAdSize(AdSize.SMART_BANNER);
//        bannerAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        requestNewBanner();
//                    }
//                }, 5000);
//            }
//        });
//        bannerAd.setAdUnitId(bannerId);
//        ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
//        AdSize adSize = bannerAd.getAdSize();
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adSize.getWidth(), adSize.getHeight());
//        viewGroup.addView(bannerAd, layoutParams);
//        bannerAd.setVisibility(View.INVISIBLE);
//        requestNewBanner();

        // init interstitial Ad
        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(interstitialId);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                isInterstitialAdLoaded = false;
                requestNewInterstitial();
                ArrayList arrayList = new ArrayList();
                arrayList.add(interstitialAdClicked);
                interstitialAdListener.onFinish(arrayList);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestNewInterstitial();
                    }
                }, 5000);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                interstitialAdClicked = true;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isInterstitialAdLoaded = true;
            }
        });
        requestNewInterstitial();

        //init reward video ad
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        requestNewVideo();
    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {
        if(mRewardedVideoAd!=null)
            mRewardedVideoAd.resume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        if(mRewardedVideoAd!=null)
            mRewardedVideoAd.pause(activity);
    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {
        if(mRewardedVideoAd!=null)
            mRewardedVideoAd.destroy(activity);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        isRewardAdLoaded = true;
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        isRewardAdLoaded = false;
        requestNewVideo();
        ArrayList arrayList = new ArrayList();
        arrayList.add(rewardAdViewed);
        arrayList.add(rewardAdClicked);
        rewardAdListener.onFinish(arrayList);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        rewardAdViewed = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        rewardAdClicked = true;
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestNewVideo();
            }
        }, 5000);
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
