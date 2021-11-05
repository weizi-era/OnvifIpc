package com.example.onvifipc;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.example.onvifipc.callback.EmptyCallback;
import com.example.onvifipc.callback.LoadingCallback;
import com.example.onvifipc.utils.ActivityManager;
import com.kingja.loadsir.core.LoadSir;


public class BaseApplication extends Application {

    private static ActivityManager activityManager;
    private static BaseApplication application;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        Utils.init(this);
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(LoadingCallback.class)
                .commit();
    }

    private void init() {
        activityManager = new ActivityManager();
        context = getApplicationContext();
        application = this;
    }

    public static BaseApplication getApplication() {
        return application;
    }

    public static Context getContext() {
        return context;
    }

    public static ActivityManager getActivityManager() {
        return activityManager;
    }
}
