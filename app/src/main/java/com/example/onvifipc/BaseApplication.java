package com.example.onvifipc;

import android.app.Application;
import android.content.Context;

import com.example.onvifipc.callback.EmptyCallback;
import com.example.onvifipc.callback.LoadingCallback;
import com.example.onvifipc.tcpclient.TaskCenter;
import com.example.onvifipc.tcpclient.TaskCenterCom;
import com.example.onvifipc.utils.ActivityManager;
import com.kingja.loadsir.core.LoadSir;
import com.littlegreens.netty.client.NettyTcpClient;

public class BaseApplication extends Application {

    private static ActivityManager activityManager;
    private static BaseApplication application;
    private static Context context;
    public static NettyTcpClient nettyTcpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(LoadingCallback.class)
                .commit();

        TaskCenter.getInstance().connect(Common.SERVER_IP, Common.SERVER_PORT);
        TaskCenterCom.getInstance().connect(Common.UPDATE_IP, Common.SERVER_PORT);

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
