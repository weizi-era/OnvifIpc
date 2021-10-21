package com.example.onvifipc.model;

import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.utils.SplitUtils;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NTPTimeModel extends BaseModel<String[]> {

    public NTPTimeModel(String basic, int position, IBaseModelListener<String[]> mListener) {
        super(basic, position, mListener);
    }

    public void load() {
        doNetRequest().getNTPInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String ntpSvr = SplitUtils.getValue(stringArray, "root.NTP.ntpSvr");
                    String interval = SplitUtils.getValue(stringArray, "root.NTP.interval");
                    String[] data = new String[] {ntpSvr, interval};

                    mListener.onLoadSuccess(data);
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

    public void update(String newAddress, String newInterval) {
        doNetRequest().updateNTPInfo("Basic " + basic, newAddress, newInterval).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String des = SplitUtils.getValue(stringArray, "root.ERR.des");

                    mListener.onUpdateSuccess(des);

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
