package com.example.onvifipc.base;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<T extends IBaseView, M extends IBaseModel> implements IBasePresenter<T> {

    protected M mModel;
    private WeakReference<T> weakReference;

    @Override
    public void attachView(T view) {
        weakReference = new WeakReference<>(view);
        if (this.mModel == null) {
            this.mModel = createModel();
        }
    }

    @Override
    public void detachView() {
        if (isViewAttach()) {
            weakReference.clear();
            weakReference = null;
        }

    }

    protected T getView() {
        return weakReference.get();
    }

    protected void showLoading() {
        if (isViewAttach()) {
            getView().onLoading();
        }
    }

    protected void dismissLoading() {
        if (isViewAttach()) {
            getView().onLoadSuccess();
        }
    }

    protected void onDataError() {
        if (isViewAttach()) {
            getView().onLoadFailed();
        }
    }

    @Override
    public boolean isViewAttach() {
        return weakReference != null && weakReference.get() != null;
    }

    protected abstract M createModel();
}
