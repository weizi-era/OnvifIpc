package com.example.onvifipc.utils;

import android.text.format.DateFormat;

import com.example.onvifipc.bean.Device;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;

public class SplitUtils {

    public static String[] getStringArray(ResponseBody response) throws IOException {
        if (response == null) {
            return null;
        }

        String result = response.string();

        return result.split("[\r\n|=]");

    }

    public static String getDeviceIP(Device device) {
        if (device == null) {
            return null;
        }

        String serviceUrl = device.getIpAddress();
        if (serviceUrl == null) {
            return null;
        }
        String[] split = serviceUrl.split(":");
        return split[0];
    }

    public static String getValue(String[] results, String key) {

        int index = 0;
        String value = null;
        if (results == null) {
            return null;
        }
        if (results.length == 0) {
            return null;
        }
        for (String result : results) {
            if (result.equals(key)) {
                value = results[index + 1];
            }
            index ++;
        }
        return value;
    }

    public static int getIndex(String[] t, String value) {
        for (int i = 0; i < t.length; i++) {
            if (t[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public static String conversionTime(long time) {
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", time).toString();
    }

    public static String getNetFileSizeDescription(long size) {
        StringBuilder bytes = new StringBuilder();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

}
