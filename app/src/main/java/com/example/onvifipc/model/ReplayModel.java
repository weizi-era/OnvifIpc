package com.example.onvifipc.model;

import com.example.onvifipc.Api;
import com.example.onvifipc.contract.replay.Contract;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ReplayModel implements Contract.IReplayModel {
    @Override
    public Call<ResponseBody> getHistoryVideo(String url, String authorization, Map<String, Long> map) {

        Call<ResponseBody> call = new Retrofit.Builder().baseUrl(url).client(new OkHttpClient()).build().create(Api.class).getHistoryVideoInfo(authorization, map);
        return call;
    }
}
