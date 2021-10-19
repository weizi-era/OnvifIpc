package com.example.onvifipc.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import com.kingja.loadsir.core.LoadService;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

    protected boolean isLazyLoaded = false;
    private boolean isPrepared = false;

    protected LoadService mBaseLoadService;

    protected Activity context;

    protected Unbinder unbinder;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(onCreateFragmentView(), container, false);
        }

        unbinder = ButterKnife.bind(this, mRootView);


        initView();

//        mBaseLoadService = LoadSir.getDefault().register(rootView, new Callback.OnReloadListener() {
//            @Override
//            public void onReload(View v) {
//                Log.d("TAG", "onReload: 重新加载了吗");
//                onNetReload(v);
//            }
//        });
//        Log.d("TAG", "onCreateView: ");

        return mRootView;
    }

    protected abstract void initView();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TAG", "onActivityCreated: ");
        isPrepared = true;
        lazyLoad();
    }

    /**
     * 绑定
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.context = (Activity) context;
        }
    }

    /**
     * 解绑
     */
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @UiThread
    protected abstract void onLazyLoad();


    protected abstract int onCreateFragmentView();

}
