package com.example.onvifipc.utils;

import android.util.Log;

import com.example.onvifipc.Common;

import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * 享元设计模式  构建一个复用池
 */
public class RetrofitPool {

    public static volatile RetrofitPool retrofitPool;

    private RetrofitPool() {}

    public static RetrofitPool getInstance() {

        synchronized (RetrofitPool.class) {
            if (retrofitPool == null) {
                retrofitPool = new RetrofitPool();
            }
        }
        return retrofitPool;
    }

    private static final HashMap<Integer, Retrofit> map = new HashMap<>();

    public Retrofit getRetrofit(Integer position) {
        if (map.containsKey(position)) {
            Log.d("TAG", "retrofit是从复用池拿的 ");
            return map.get(position);
        }

        Log.d("TAG", "网络请求拿到的IP是: " + Common.getBaseUrl(position));
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Common.getBaseUrl(position))
                .client(new OkHttpClient())
                .build();
        Log.d("TAG", "第一次创建retrofit");
        map.put(position, retrofit);
        return retrofit;
    }

}
