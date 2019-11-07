package com.joycastle.advertise.adcolony;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.adcolony.sdk.*;
import com.joycastle.gamepluginbase.AdvertiseDelegate;
import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;
import com.joycastle.gamepluginbase.SystemUtil;

import java.util.ArrayList;

/**
 * Created by gaoyang on 9/30/16.
 */

public class ACAdvertiseHelper implements AdvertiseDelegate {
    private static final String TAG = "ACAdvertiseHelper";

    private static ACAdvertiseHelper instance = new ACAdvertiseHelper();

    private String zone_id = null;

    private Boolean isLoadAD = false;
    private AdColonyInterstitial ad;
    private AdColonyInterstitialListener ad_listener;
    private AdColonyAdOptions ad_options;

    public static ACAdvertiseHelper getInstance() {
        return instance;
    }

    private ACAdvertiseHelper() {}

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
    public int showBannerAd(boolean protrait, boolean bottom) {
        Log.i(TAG, "didn't support");
        return 0;
    }

    @Override
    public void hideBannerAd() {
        Log.i(TAG, "didn't support");
    }

    @Override
    public boolean isInterstitialAdReady() {
        Log.i(TAG, "didn't support");
        return false;
    }

    @Override
    public boolean showInterstitialAd(InvokeJavaMethodDelegate listener) {
//        Log.i(TAG, "didn't support");
        ad.show();
        return true;
    }

    @Override
    public boolean isVideoAdReady() {

        return isLoadAD;
    }

    @Override
    public boolean showVideoAd(InvokeJavaMethodDelegate listener) {
        Log.i(TAG, "Adcolony showVideoAd");
        if(!isLoadAD)
        {
            AdColony.requestInterstitial(zone_id, ad_listener, ad_options);
        }
        ad.show();
        return true;
    }

    @Override
    public String getName() {
        return "Adcolony";
    }

    @Override
    public void init(Application application) {
        Log.i(TAG, "Adcolony installed");
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {

        String origin_store = SystemUtil.getInstance().getPlatCfgValue("origin_store");
        String app_id = SystemUtil.getInstance().getPlatCfgValue("app_id");
        zone_id = SystemUtil.getInstance().getPlatCfgValue("zone_id");

        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
                .setUserAge( 26 )
                .setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                .setUserGender( AdColonyUserMetadata.USER_MALE );

        ad_options = new AdColonyAdOptions()
                .setUserMetadata( metadata );
//                .enableConfirmationDialog(true)
//                .enableResultsDialog(true);

        AdColonyAppOptions app_options = new AdColonyAppOptions()
                .setUserID( "unique_user_id" );

        AdColony.configure(activity, app_options, app_id, zone_id);
        Log.i(TAG, "Adcolony onCreate");
        ad_listener = new AdColonyInterstitialListener()
        {
            /** Ad passed back in request filled callback, ad can now be shown */
            @Override
            public void onRequestFilled( AdColonyInterstitial ad )
            {
                ACAdvertiseHelper.this.ad = ad;
                isLoadAD = true;
                Log.d( TAG, "onRequestFilled" );
            }

            /** Ad request was not filled */
            @Override
            public void onRequestNotFilled( AdColonyZone zone )
            {

                Log.d( TAG, "onRequestNotFilled");
            }

            /** Ad opened, reset UI to reflect state change */
            @Override
            public void onOpened( AdColonyInterstitial ad )
            {

                Log.d( TAG, "onOpened" );
            }

            /** Request a new ad if ad is expiring */
            @Override
            public void onExpiring( AdColonyInterstitial ad )
            {
                AdColony.requestInterstitial( zone_id, this, ad_options );
                Log.d( TAG, "onExpiring" );
            }
        };
//        AdColony.requestInterstitial(zone_id, ad_listener, ad_options);


    }

    @Override
    public void onStart(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {
//        AdColony.resume(activity);

        if (ad == null || ad.isExpired())
        {
            /**
             * Optionally update location info in the ad options for each request:
             * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
             * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
             * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
             */
            AdColony.requestInterstitial( zone_id, ad_listener, ad_options );
        }
    }

    @Override
    public void onPause(Activity activity) {
//        AdColony.pause();
    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }
}
