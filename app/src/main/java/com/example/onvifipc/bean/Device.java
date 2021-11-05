package com.example.onvifipc.bean;

import java.util.ArrayList;

/**
 * 设备信息 实体
 */
public class Device {

    /**
     * 用户名/密码
     */
    private String userName;
    private String userPwd;

    //IP地址
    private String ipAddress;

    /**
     * serviceUrl,uuid 通过广播包搜索设备获取
     */
    private String serviceUrl;
    private String uuid;

    /**
     * getCapabilities
     */
    private String mediaUrl;
    private String imageUrl;


    public Device() {

    }

    public Device(String userName, String userPwd) {
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.ipAddress = serviceUrl.substring(serviceUrl.indexOf("//") + 2, serviceUrl.indexOf("/on"));
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }



    @Override
    public String toString() {
        return "Device{" +
                "userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", uuid='" + uuid + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
