package com.example.onvifipc.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onvifipc.BaseApplication;
import com.example.onvifipc.Common;
import com.example.onvifipc.tcpclient.TaskCenter;
import com.example.onvifipc.tcpclient.TaskCenterCom;
import com.example.onvifipc.ui.activity.LoginActivity;
import com.example.onvifipc.bean.NetworkChangeEvent;
import com.example.onvifipc.utils.ToastUtils;
import com.example.onvifipc.utils.WifiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    private static final int SLIDE_TRANSITION_TIME = 1000;//滑动转场的时间
    public Fade mFadeTransition;

    private ForceOfflineReceiver receiver;

    private Context mContext;

    protected boolean mCheckNetwork = true;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            Log.i("BaseActivity", "onCreate fixOrientation when Oreo, result = " + result);
        }
        super.onCreate(savedInstanceState);

        BaseApplication.getActivityManager().addActivity(this);

        setContentView(setLayoutResourceID());

        mContext = this;

        unbinder = ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        hasNetwork(WifiUtils.isConnected(mContext));

        initView();

        initData();
    }


    protected abstract void initView();

    protected abstract void initData();

    public abstract int setLayoutResourceID();

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, filter);
    }

    protected void setupWindowAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFadeTransition = new Fade();
            mFadeTransition.setDuration(SLIDE_TRANSITION_TIME);
            getWindow().setEnterTransition(mFadeTransition);
            getWindow().setExitTransition(mFadeTransition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }

    public abstract void reConnect();

    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event) {
        hasNetwork(event.isConnected);
    }

    public boolean isCheckNetWork() {
        return mCheckNetwork;
    }

    private void hasNetwork(boolean isConnected) {
        if (isCheckNetWork()) {
            if (isConnected) {
                reConnect();
                TaskCenter.getInstance().connect(Common.SERVER_IP, Common.SERVER_PORT);
                TaskCenterCom.getInstance().connect(Common.UPDATE_IP, Common.SERVER_PORT);
                ToastUtils.showToast(mContext, "已连接设备WIFI");
            } else {
                TaskCenter.getInstance().disconnect();
                TaskCenterCom.getInstance().disconnect();
                ToastUtils.showToast(mContext, "未连接设备WIFI");
            }
        }
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            Log.i("BaseActivity", "onCreate fixOrientation when Oreo. ");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    static class ForceOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("IP地址已修改，请重新登录.")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BaseApplication.getActivityManager().finishAll();
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    })
                    .show();
        }
    }

}
