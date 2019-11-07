
package com.joycastle.analytic.flurry;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.joycastle.gamepluginbase.AnalyticDelegate;
import com.joycastle.gamepluginbase.SystemUtil;

import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by geekgy on 16/4/23.
 */

public class FLAnalyticHelper implements AnalyticDelegate, FlurryAgentListener {

    private static final String TAG = "FLAnalyticHelper";
    private static FLAnalyticHelper instance = new FLAnalyticHelper();

    public static FLAnalyticHelper getInstance() {
        return instance;
    }

    private FLAnalyticHelper() {}

    @Override
    public void setAccoutInfo(HashMap map){
        Object userId = map.get("userId");
        Object gender = map.get("gender");
        Object age = map.get("age");
        if (userId != null) {
            FlurryAgent.setUserId((String) userId);
        }
        if (gender != null) {
            if (gender.equals("male")) {
                FlurryAgent.setGender(Constants.MALE);
            } else if (gender.equals("female")) {
                FlurryAgent.setGender(Constants.FEMALE);
            }
        }
        if (age != null) {
            FlurryAgent.setAge((int)age);
        }
    }

    @Override
    public void onEvent(String eventId) {
        FlurryAgent.logEvent(eventId);
    }

    @Override
    public void onEvent(String eventId, String eventLabel){
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("key", eventLabel);
        this.onEvent(eventId, eventData);
    }

    @Override
    public void onEvent(String eventId, HashMap<String, String> eventData) {
        HashMap<String, String> hashMap = new HashMap<>();
        Iterator iterator = eventData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            hashMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        FlurryAgent.logEvent(eventId, hashMap);
    }

    @Override
    public void setLevel(int level){
    }

    @Override
    public void charge(String iapId, double cash, double coin, int channal){
        Currency currency = Currency.getInstance(Locale.US);
        String currencyCode = currency.getCurrencyCode();
        HashMap<String, String> params = new HashMap<>();
        FlurryAgent.logPayment(iapId, iapId, (int)coin, cash, currencyCode, "", params);
    }

    @Override
    public void reward(double coin, int reason){
    }

    @Override
    public void purchase(String good, int amount, double coin){

    }

    @Override
    public void use(String good, int amount, double coin){
    }

    @Override
    public void onMissionBegin(String missionId) {
    }

    @Override
    public void onMissionCompleted(String missionId) {
    }

    @Override
    public void onMissionFailed(String missionId, String reason) {
    }

    @Override
    public void init(Application application) {

    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        String appKey = SystemUtil.getInstance().getPlatCfgValue("flurry_key");
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withLogLevel(Log.VERBOSE)
                .withListener(this)
                .withCaptureUncaughtExceptions(true)
                .withContinueSessionMillis(10000)
                .build(activity, appKey);
        
        Log.i(TAG, "Flurry installed, Version: "+FlurryAgent.getReleaseVersion());
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

    }


    @Override
    public void onSessionStarted() {

    }
}
