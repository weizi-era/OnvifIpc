package com.example.onvifipc;


import com.example.onvifipc.adapter.CameraAdapter;

public class Common {

    public static String getBaseUrl(int position) {
        return "http://" + CameraAdapter.deviceIP.get(position);
    }

    public static final String DEV_TYPE = "IPC";

    public static final int SERVER_PORT = 58602;
    public static final String SERVER_IP = "192.168.1.160";
    public static final String UPDATE_IP = "192.168.1.161";

    public static final String RES_TYPE_1080 = "4000";
    public static final String RES_TYPE_720 = "1000";

    public static final int MAIN_STREAM = 0;  // 主码流
    public static final int MINOR_STREAM = 1; // 次码流


    public static final int PARAMS = 2;   //硬件参数标志
    public static final int CODE = 3;   // 设备MN号标志
    public static final int COM = 4;   // 串口状态标志
    /**
     * 两次点击返回键的时间差
     */
    public static final long EXIT_TIME = 2000;
}
