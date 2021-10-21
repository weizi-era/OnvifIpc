package com.example.onvifipc.base;

import com.example.onvifipc.Api;
import com.example.onvifipc.utils.RetrofitPool;


public class BaseModel<T> {

    public int position;
    public String basic;
    public IBaseModelListener<T> mListener;

    public BaseModel(String basic, int position, IBaseModelListener<T> mListener) {
        this.position = position;
        this.basic = basic;
        this.mListener = mListener;
    }

    public Api doNetRequest() {
        return RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
    }
}
