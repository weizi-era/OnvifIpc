package com.example.onvifipc.contract.preview;

import com.example.onvifipc.base.IBaseView;

import java.util.List;

public class Contract {

    public interface IPreviewModel {

    }

    public interface IPreviewPresenter {

        void getHardwareData(int tag);
        //void getMNData();
        void getComState();
    }

    public interface IPreviewView extends IBaseView {
        void showComState1(boolean isConnected);
        void showComState2(String result);
        //void onDataSuccess(int tag, String result);
        void onSuccess(int tag, List<String> result);
        void onParamsSuccess(int tag, List<Float> result);
        //void onMNSuccess(List<String> result);
    }
}
