package com.example.onvifipc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.onvifipc.bean.NetworkChangeEvent;

import org.greenrobot.eventbus.EventBus;

public class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = WifiUtils.isConnected(context);
        EventBus.getDefault().post(new NetworkChangeEvent(isConnected));
    }
}
