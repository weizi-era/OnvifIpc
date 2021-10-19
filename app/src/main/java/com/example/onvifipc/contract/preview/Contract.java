package com.example.onvifipc.contract.preview;

import com.example.onvifipc.base.IBaseView;

public class Contract {

    public interface IPreviewModel {

    }

    public interface IPreviewPresenter {

        void getHardwareData(int registerAddr, int tag);
        void getComState();
    }

    public interface IPreviewView extends IBaseView {
        void showComState1(String result);
        void showComState2(String result);
        void onDataSuccess(int tag, String result);
    }
}
