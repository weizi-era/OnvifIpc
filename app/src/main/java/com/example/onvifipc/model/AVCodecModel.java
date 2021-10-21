package com.example.onvifipc.model;

import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.utils.SplitUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AVCodecModel extends BaseModel<List<String>> {

    public AVCodecModel(String basic, int position, IBaseModelListener<List<String>> mListener) {
        super(basic, position, mListener);
    }

    public void load(int type) {
        doNetRequest().getCodecInfo("Basic " + basic, type).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    if (stringArray != null && stringArray.length > 0) {

                        List<String> dataList = new ArrayList<>();
                        String streamMode = SplitUtils.getValue(stringArray, "root.VENC.streamMixType");
                        String h264EncLvl = SplitUtils.getValue(stringArray, "root.VENC.h264EncLvl");
                        String frameRate = SplitUtils.getValue(stringArray, "root.VENC.frameRate");
                        String frPreeferred = SplitUtils.getValue(stringArray, "root.VENC.frPreeferred");
                        String iFrameIntv = SplitUtils.getValue(stringArray, "root.VENC.iFrameIntv");
                        String veType = SplitUtils.getValue(stringArray, "root.VENC.veType");
                        String bitRate = SplitUtils.getValue(stringArray, "root.VENC.bitRate");
                        String bitRateType = SplitUtils.getValue(stringArray, "root.VENC.bitRateType");
                        String resolution = SplitUtils.getValue(stringArray, "root.VENC.resolution");
                        String gopMode = SplitUtils.getValue(stringArray, "root.VENC.gopMode");

                        dataList.add(0, streamMode);
                        dataList.add(1, h264EncLvl);
                        dataList.add(2, frameRate);
                        dataList.add(3, frPreeferred);
                        dataList.add(4, iFrameIntv);
                        dataList.add(5, veType);
                        dataList.add(6, bitRate);
                        dataList.add(7, bitRateType);
                        dataList.add(8, resolution);
                        dataList.add(9, gopMode);

                        mListener.onLoadSuccess(dataList);

                    }
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

    public void update(int streamTypeIndex, int streamModeIndex, int encodeLevelIndex, String frameRate, boolean firstBitrate,
                       String IFrameGap, int encodeTypeIndex, String bitRate, int bitrateTypeIndex, int resolution, int gopModeIndex) {
        Map<String, Integer> map = new HashMap<>();
        map.put("streamType", streamTypeIndex);
        map.put("VENC.streamMixType", streamModeIndex);
        map.put("VENC.h264EncLvl", encodeLevelIndex);
        map.put("VENC.frameRate", Integer.parseInt(frameRate));
        map.put("VENC.frPreeferred", firstBitrate ? 1 : 0);
        map.put("VENC.iFrameIntv", Integer.parseInt(IFrameGap));
        map.put("VENC.veType", encodeTypeIndex);
        map.put("VENC.bitRate", Integer.parseInt(bitRate));
        map.put("VENC.bitRateType", bitrateTypeIndex);
        map.put("VENC.resolution", resolution);
        map.put("VENC.gopMode", gopModeIndex);
        doNetRequest().updateCodecInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");

                    mListener.onUpdateSuccess(resultCode);
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
}
