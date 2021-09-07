package com.example.onvifipc;

import android.app.Application;

import com.example.onvifipc.callback.EmptyCallback;
import com.example.onvifipc.callback.LoadingCallback;
import com.kingja.loadsir.core.LoadSir;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(LoadingCallback.class)
                .commit();
    }
}
