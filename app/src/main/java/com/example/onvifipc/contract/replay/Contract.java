package com.example.onvifipc.contract.replay;

import com.example.onvifipc.base.IBaseView;
import com.example.onvifipc.bean.HistoryVideo;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class Contract {

    public interface IReplayModel {

        Call<ResponseBody> getHistoryVideo(String url, String authorization, Map<String, Long> map);
    }

    public interface IReplayPresenter {

        void getHistoryVideo(String url, String authorization, Map<String, Long> map);

    }

    public interface IReplayView extends IBaseView {
        void onDataSuccess(List<HistoryVideo> historyVideoList);
    }

}
