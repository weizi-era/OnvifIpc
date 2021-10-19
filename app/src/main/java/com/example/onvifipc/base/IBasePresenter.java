package com.example.onvifipc.base;

public interface IBasePresenter<T extends IBaseView> {

    /**
     * 绑定view
     * @param view
     */
    void attachView(T view);

    /**
     * 分离view
     */
    void detachView();

    /**
     * 判断view是否已经销毁
     * @return true 未销毁
     */
    boolean isViewAttach();
}
