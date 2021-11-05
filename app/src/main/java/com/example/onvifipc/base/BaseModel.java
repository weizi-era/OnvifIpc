package com.example.onvifipc.base;

import com.example.onvifipc.Api;
import com.example.onvifipc.utils.RetrofitPool;

import java.lang.ref.WeakReference;


public abstract class BaseModel<T> {

    // 防止内存泄漏
    protected WeakReference<IBaseModelListener> listenerWeakReference;

    public void registerListener(IBaseModelListener listener) {
        if (listener != null) {
            listenerWeakReference = new WeakReference<>(listener);
        }
    }

    private int position;
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
