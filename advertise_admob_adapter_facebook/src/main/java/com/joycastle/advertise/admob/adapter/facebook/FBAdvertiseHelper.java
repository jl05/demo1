package com.joycastle.advertise.admob.adapter.facebook;import android.app.Activity;import android.app.Application;import android.content.Intent;import android.os.Bundle;import android.util.Log;import com.facebook.ads.AdSettings;import com.joycastle.gamepluginbase.LifeCycleDelegate;/** * Created by joye on 2018/7/19. */public class FBAdvertiseHelper implements LifeCycleDelegate {    private static final String TAG = "FBAdvertiseHelper";    private static FBAdvertiseHelper instance = new FBAdvertiseHelper();    public static FBAdvertiseHelper getInstance() {        return instance;    }    private FBAdvertiseHelper() {}    @Override    public void init(Application application) {        Log.i(TAG, "facebook adapter is init");    }    @Override    public void onCreate(Activity activity, Bundle savedInstanceState) {    }    @Override    public void onStart(Activity activity) {    }    @Override    public void onResume(Activity activity) {    }    @Override    public void onPause(Activity activity) {    }    @Override    public void onStop(Activity activity) {    }    @Override    public void onDestroy(Activity activity) {    }    @Override    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {    }}