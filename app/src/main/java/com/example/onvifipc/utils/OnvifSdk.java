package com.example.onvifipc.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;


import com.example.onvifipc.bean.Device;
import com.example.onvifipc.onvif.FindDevicesThread;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Onvif摄像头sdk (需要注意的是，大部分设备为完全兼容onvif 摄像头厂家会有自己的私有协议，所有在部分摄像头设备上无法使用不跟功能)
 * 本sdk集成
  */
public class OnvifSdk {


    private static Context mContext;

    /**
     * 初始化sdk,方法为异步操作，探索设备的时间为4秒，需注意
     * 探索发现设备
     *
     * @param context
     */
    public static void initSdk(Context context) {
        mContext = context;
    }

   /**
     * 搜索网段下的设备，可以是广播地址
     *
     * @param ipAdress 192.168.1.255
     * @param listener FindDevicesListener
     */
    public static void findDevice(Context context, String ipAdress, final FindDevicesThread.FindDevicesListener listener) {
        FindDevicesThread findDevicesThread = new FindDevicesThread(context, ipAdress, new FindDevicesThread.FindDevicesListener() {
            @Override
            public void searchResult(ArrayList<Device> devices) {
                Iterator<Device> iterator = devices.iterator();
                while (iterator.hasNext()) {
                    Device device = iterator.next();
                    device.setUserName("admin");
                    device.setUserPwd("Rock@688051");
                    if (device.getIpAddress() == null) {
                        iterator.remove();
                    }
                }
                listener.searchResult(devices);
            }
        });
        findDevicesThread.start();
    }

}
