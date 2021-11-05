package com.example.onvifipc.bean;

public class NetworkChangeEvent {

    public boolean isConnected;

    public NetworkChangeEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
