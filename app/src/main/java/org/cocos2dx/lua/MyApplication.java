package org.cocos2dx.lua;

import androidx.multidex.MultiDexApplication;

import com.joycastle.gameplugin.GamePlugin;

/**
 * Created by geekgy on 16/4/23.
 */
public class MyApplication extends MultiDexApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        GamePlugin.getInstance().init(this);
    }
}
