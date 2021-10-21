package com.example.onvifipc.model;

import android.util.Log;

import com.example.onvifipc.Api;
import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GBSettingsModel extends BaseModel<List<String>> {

    public GBSettingsModel(String basic, int position, IBaseModelListener<List<String>> mListener) {
        super(basic, position, mListener);
    }

    public void load() {
        doNetRequest().getGbInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String streamType = SplitUtils.getValue(stringArray, "root.GB28181.streamType");
                    String enable = SplitUtils.getValue(stringArray, "root.GB28181.enable");
                    String serverId = SplitUtils.getValue(stringArray, "root.GB28181.svrId");
                    String serverIp = SplitUtils.getValue(stringArray, "root.GB28181.svrIp");
                    String devId = SplitUtils.getValue(stringArray, "root.GB28181.devId");
                    String devPwd = SplitUtils.getValue(stringArray, "root.GB28181.devPsw");
                    String mediaId = SplitUtils.getValue(stringArray, "root.GB28181.mediaId");
                    String svrPort = SplitUtils.getValue(stringArray, "root.GB28181.svrPort");
                    String devPort = SplitUtils.getValue(stringArray, "root.GB28181.devPort");
                    String protocol = SplitUtils.getValue(stringArray, "root.GB28181.protocol");
                    String expires = SplitUtils.getValue(stringArray, "root.GB28181.expires");
                    String regInterval = SplitUtils.getValue(stringArray, "root.GB28181.regInterval");
                    String heartBeat = SplitUtils.getValue(stringArray, "root.GB28181.heartBeat");
                    String timeOutCnt = SplitUtils.getValue(stringArray, "root.GB28181.timeOutCnt");

                    List<String> dataList = new ArrayList<>();

                    dataList.add(0, streamType);
                    dataList.add(1, enable);
                    dataList.add(2, serverId);
                    dataList.add(3, serverIp);
                    dataList.add(4, devId);
                    dataList.add(5, devPwd);
                    dataList.add(6, mediaId);
                    dataList.add(7, svrPort);
                    dataList.add(8, devPort);
                    dataList.add(9, protocol);
                    dataList.add(10, expires);
                    dataList.add(11, regInterval);
                    dataList.add(12, heartBeat);
                    dataList.add(13, timeOutCnt);

                    mListener.onLoadSuccess(dataList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
               mListener.onLoadFailure(t.getMessage());
            }
        });
    }

    public void update(String serverID, String serverIP, String serverPort, String deviceID, String password, String devicePort,
                       String expires, String gap, String heartCycle, String timeoutCount, String mediaChannelID) {
        Map<String, String> map = new HashMap<>();
        map.put("GB28181.svrId", serverID);
        map.put("GB28181.svrIp", serverIP);
        map.put("GB28181.svrPort", serverPort);
        map.put("GB28181.devId", deviceID);
        map.put("GB28181.devPsw", password);
        map.put("GB28181.devPort", devicePort);
        map.put("GB28181.expires", expires);
        map.put("GB28181.regInterval", gap);
        map.put("GB28181.heartBeat", heartCycle);
        map.put("GB28181.timeOutCnt", timeoutCount);
        map.put("GB28181.mediaId", mediaChannelID);

        doNetRequest().updateGbInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                    mListener.onUpdateSuccess(resultCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mListener.onLoadFailure(t.getMessage());
            }
        });
    }
}
