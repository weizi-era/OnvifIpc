package com.example.onvifipc.base;

public interface IBaseView {

    /**
     * 网络错误，数据加载失败
     */
    void onLoadFailed();

    /**
     * 数据加载中...，
     */
    void onLoading();

    /**
     * 加载完成
     */
    void onLoadSuccess();
}
