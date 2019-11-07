package com.joycastle.gameplugin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.joycastle.gamepluginbase.AnalyticDelegate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gaoyang on 9/29/16.
 */

public class AnalyticHelper implements AnalyticDelegate{
    private static final String TAG = "AnalyticHelper";

    private static AnalyticHelper instance = new AnalyticHelper();

    private ArrayList<AnalyticDelegate> delegates;

    public static AnalyticHelper getInstance() { return instance; }

    private AnalyticHelper() {
        delegates = new ArrayList<>();
    }

    @Override
    public void setAccoutInfo(HashMap map){
        for (AnalyticDelegate delegate : delegates) {
            delegate.setAccoutInfo(map);
        }
    }

    @Override
    public void onEvent(String eventId) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onEvent(eventId);
        }
    }

    @Override
    public void onEvent(String eventId, String eventLabel){
        for (AnalyticDelegate delegate : delegates) {
            delegate.onEvent(eventId, eventLabel);
        }
    }

    @Override
    public void onEvent(String eventId, HashMap eventData){
        for (AnalyticDelegate delegate : delegates) {
            delegate.onEvent(eventId, eventData);
        }
    }

    @Override
    public void setLevel(int level){
        for (AnalyticDelegate delegate : delegates) {
            delegate.setLevel(level);
        }
    }

    @Override
    public void charge(String iapId, double cash, double coin, int channal){
        for (AnalyticDelegate delegate : delegates) {
            delegate.charge(iapId, cash, coin, channal);
        }
    }

    @Override
    public void reward(double coin, int reason){
        for (AnalyticDelegate delegate : delegates) {
            delegate.reward(coin, reason);
        }
    }

    @Override
    public void purchase(String good, int amount, double coin){
        for (AnalyticDelegate delegate : delegates) {
            delegate.purchase(good, amount, coin);
        }
    }

    @Override
    public void use(String good, int amount, double coin){
        for (AnalyticDelegate delegate : delegates) {
            delegate.use(good, amount, coin);
        }
    }

    @Override
    public void onMissionBegin(String missionId) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onMissionBegin(missionId);
        }
    }

    @Override
    public void onMissionCompleted(String missionId) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onMissionCompleted(missionId);
        }
    }

    @Override
    public void onMissionFailed(String missionId, String reason) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onMissionFailed(missionId, reason);
        }
    }

    @Override
    public void init(Application application) {
        try {
            Class clazz = Class.forName("com.joycastle.analytic.flurry.FLAnalyticHelper");
            Method method = clazz.getMethod("getInstance");
            AnalyticDelegate delegate = (AnalyticDelegate) method.invoke(null);
            delegates.add(delegate);
            delegate.init(application);
        } catch (Exception e) {
            Log.e(TAG, "Flurry is disable");
        }

        try {
            Class clazz = Class.forName("com.joycastle.analytic.facebook.FBAnalyticHelper");
            Method method = clazz.getMethod("getInstance");
            AnalyticDelegate delegate = (AnalyticDelegate) method.invoke(null);
            delegates.add(delegate);
            delegate.init(application);
        } catch (Exception e) {
            Log.e(TAG, "facebook is disable");
        }
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onCreate(activity, savedInstanceState);
        }
    }

    @Override
    public void onStart(Activity activity) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onStart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onDestroy(activity);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        for (AnalyticDelegate delegate : delegates) {
            delegate.onActivityResult(activity, requestCode, resultCode, data);
        }
    }
}
