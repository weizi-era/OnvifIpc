package com.example.onvifipc.presenter;

import android.util.Log;

import com.example.onvifipc.adapter.HistoryVideoAdapter;
import com.example.onvifipc.bean.HistoryVideo;
import com.example.onvifipc.contract.replay.Contract;
import com.example.onvifipc.model.ReplayModel;
import com.example.onvifipc.utils.SplitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplayPresenter implements Contract.IReplayPresenter {

    private Contract.IReplayModel mReplayModel;
    private Contract.IReplayView mReplayView;
    private List<HistoryVideo> historyVideoList;
    private HistoryVideoAdapter adapter;

    public ReplayPresenter(Contract.IReplayView mReplayView) {
        mReplayModel = new ReplayModel();
        historyVideoList = new ArrayList<>();
        this.mReplayView = mReplayView;
    }

    private void setPlayInfo() {
        adapter = new HistoryVideoAdapter(historyVideoList);
    }

    @Override
    public void getHistoryVideo(String url, String authorization, Map<String, Long> map) {

        mReplayModel.getHistoryVideo(url, authorization, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String totalCount = SplitUtils.getValue(stringArray, "root.RECORD.totalCount");
                    Log.d("TAG", "onResponse: " + totalCount);
                    int totalCounts = Integer.parseInt(totalCount);
                    historyVideoList.clear();
                    for (int i = 0; i < totalCounts; i++) {
                        String beginTime = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".beginTime");
                        String endTime = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".endTime");
                        String size = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".size");
                        String property = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i +".property");
                        String convertBeginTime = SplitUtils.conversionTime(Long.parseLong(beginTime) * 1000);
                        String convertEndTime = SplitUtils.conversionTime(Long.parseLong(endTime) * 1000);
                        String byteToMb = SplitUtils.getNetFileSizeDescription(Long.parseLong(size));
                        historyVideoList.add(new HistoryVideo(i + 1, convertBeginTime, convertEndTime, byteToMb, property, false));
                    }

                    mReplayView.onDataSuccess(historyVideoList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mReplayView.onLoadFailed();
            }
        });
    }
}
