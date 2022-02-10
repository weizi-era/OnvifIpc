package com.example.onvifipc.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Message;
import android.util.Log;

/**
 * wifi 工具类
 */
public class WifiUtils {

    public static boolean isConnected(Context context) {
        String wifiName;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            wifiName = wifiInfo.getSSID();  //wifi名称
        } else {
            wifiName = wifiInfo.getSSID().replace("\"", "");
        }

        return wifiName.contains("RKDF");
    }
}
