package com.example.onvifipc.base;

public interface IBaseModelListener<DATA> {

    void onLoadSuccess(DATA data);
    void onLoadFailure(String message);
    void onUpdateSuccess(String response);
}
