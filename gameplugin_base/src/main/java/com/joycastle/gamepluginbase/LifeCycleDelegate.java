package com.joycastle.gamepluginbase;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by geekgy on 16/4/23.
 */
public interface LifeCycleDelegate {

    public void init(Application application);

    public void onCreate(Activity activity, Bundle savedInstanceState);

    public void onStart(Activity activity);

    public void onResume(Activity activity);

    public void onPause(Activity activity);

    public void onStop(Activity activity);

    public void onDestroy(Activity activity);

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}
