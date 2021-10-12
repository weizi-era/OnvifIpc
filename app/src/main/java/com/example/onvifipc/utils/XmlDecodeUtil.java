package com.example.onvifipc.utils;

import android.util.Xml;


import com.example.onvifipc.bean.Device;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 15:54
 * Description : xml 解析
 */

public class XmlDecodeUtil {
    /**
     * 获取设备信息
     */
    public static Device getDeviceInfo(String xml) throws Exception {
        Device device = new Device();
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("XAddrs")) {
                        String addrs = parser.nextText();
                        String[] strs = addrs.split(" ");
                        String url = strs[0];
                        device.setServiceUrl(url);
                    }
                    if (parser.getName().equals("MessageID")) {
                        device.setUuid(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return device;
    }
}
