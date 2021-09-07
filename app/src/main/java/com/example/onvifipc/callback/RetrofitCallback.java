package com.example.onvifipc.callback;

public interface RetrofitCallback {

    void onSuccess(String value);
    void onError(Throwable throwable);
}
