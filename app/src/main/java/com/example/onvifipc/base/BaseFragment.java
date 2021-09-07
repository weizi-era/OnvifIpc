package com.example.onvifipc.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {

    protected boolean isLazyLoaded = false;
    private boolean isPrepared = false;

    protected LoadService mBaseLoadService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(onCreateFragmentView(), container , false);
        ButterKnife.bind(this, rootView);
        mBaseLoadService = LoadSir.getDefault().register(rootView, new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                Log.d("TAG", "onReload: 重新加载了吗");
                onNetReload(v);
            }
        });
        Log.d("TAG", "onCreateView: ");

        return mBaseLoadService.getLoadLayout();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TAG", "onActivityCreated: ");
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("TAG", "setUserVisibleHint: " + isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    private void lazyLoad() {
        if (getUserVisibleHint() && isPrepared && !isLazyLoaded) {
            onLazyLoad();
            isLazyLoaded = true;
        } else {
            //当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
            if (isLazyLoaded) {
                stopLoad();
            }
        }
    }

    protected void stopLoad(){

    }

    @UiThread
    protected abstract void onLazyLoad();

    protected abstract void onNetReload(View v);

    protected abstract int onCreateFragmentView();
}
