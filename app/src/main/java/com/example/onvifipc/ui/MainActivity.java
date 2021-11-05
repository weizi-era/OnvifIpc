package com.example.onvifipc.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Api;
import com.example.onvifipc.Common;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.bean.Device;
import com.example.onvifipc.fragment.PreviewFragment;
import com.example.onvifipc.fragment.ReplayFragment;
import com.example.onvifipc.fragment.SettingsFragment;
import com.example.onvifipc.onvif.FindDevicesThread;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.OnvifSdk;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity  {

    @BindView(R.id.main_navi_view)
    BottomNavigationView mainNaviView;

    private Fragment mFragment;

    private FragmentTransaction transaction;
    private PreviewFragment previewFragment;
    private ReplayFragment replayFragment;
    private SettingsFragment settingsFragment;

    private final String basic = Base64Utils.encodedStr("admin" + ":" + "Rock@688051");
    private Map<String, String> map;
    private boolean isFirst = true;

    private long pressTime = 0L;

    private int mLastIndex = -1;

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    public void reConnect() {

    }

    @Override
    protected void initData() {
//        if (isFirst) {
//            new AlertDialog.Builder(this).setTitle("提示")
//                .setMessage("请初始化IP地址")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        updateDefaultIp();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//            isFirst = false;
//        }
    }

    private void initFragment() {
        previewFragment = new PreviewFragment();
        replayFragment = new ReplayFragment();
        settingsFragment = new SettingsFragment();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_container, previewFragment).commit();
        mFragment = previewFragment;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // recreate时保存当前页面位置
        outState.putInt("index", mLastIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复recreate前的页面
        mLastIndex = savedInstanceState.getInt("index");
        switchFragment(previewFragment);
    }

    @Override
    protected void initView() {
        map = new HashMap<>();
        initFragment();
        mainNaviView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                onTabItemSelected(item.getItemId());
                return true;
            }
        });
    }

    private void updateDefaultIp() {
        map.put("ETH.ipaddr", "192.168.1.161");
        new Retrofit.Builder().baseUrl("http://192.168.1.160")
                .client(new OkHttpClient())
                .build()
                .create(Api.class)
                .updateNetworkInfo("Basic " + basic, map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String[] stringArray = SplitUtils.getStringArray(response.body());
                            String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                            if (resultCode != null && resultCode.equals("0")) {
                                ToastUtils.showToast(MainActivity.this, "初始化成功！");
                            } else {
                                ToastUtils.showToast(MainActivity.this, "初始化失败！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        ToastUtils.showToast(MainActivity.this, "网络错误");
                    }
                });
    }

    private void onTabItemSelected(int id) {
        switch (id) {
            case R.id.navi_preview:
                switchFragment(previewFragment);
                break;
            case R.id.navi_replay:
                switchFragment(replayFragment);
                break;
            case R.id.navi_settings:
                switchFragment(settingsFragment);
                break;
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFragment != fragment) {
            if (!fragment.isAdded()) {
                transaction.hide(mFragment).add(R.id.main_container, fragment).commit();
            } else {
                transaction.hide(mFragment).show(fragment).commit();
            }
            mFragment = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - pressTime > Common.EXIT_TIME) {
            pressTime = System.currentTimeMillis();
            ToastUtils.showToast(this, "再按一次退出程序");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoadFailed() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoadSuccess() {

    }
}
