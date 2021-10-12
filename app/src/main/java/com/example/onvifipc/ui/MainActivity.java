package com.example.onvifipc.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Common;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.bean.Device;
import com.example.onvifipc.onvif.FindDevicesThread;
import com.example.onvifipc.utils.OnvifSdk;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements OnRefreshListener, View.OnClickListener {


    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_preview)
    LinearLayout ll_preview;
    @BindView(R.id.ll_replay)
    LinearLayout ll_replay;

    private CameraAdapter adapter;
    private boolean isFirst = true;
    List<Device> arrayList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setData() {
        initView();
    }

    private void initView() {
        ll_preview.setOnClickListener(this);
        ll_replay.setOnClickListener(this);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(this);
        if (isFirst) {
            refreshLayout.autoRefresh();
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this.getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new CameraAdapter(MainActivity.this, arrayList);
            recyclerView.setAdapter(adapter);
        }
    }

    public void findDevices() {
        OnvifSdk.findDevice(this, "192.168.1.255", new FindDevicesThread.FindDevicesListener() {
            public void searchResult(final ArrayList<Device> devices) {
                if (devices.size() > 0) {
                    arrayList.addAll(devices);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this.getApplicationContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            adapter = new CameraAdapter(MainActivity.this, devices);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        findDevices();
        refreshLayout.finishRefresh(4000); // 由于OnvifSdk 搜索设备时间为4秒  故刷新延时4s
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_preview:
                Intent intent1 = new Intent(MainActivity.this, ManagerMainActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_replay:
                Intent intent2 = new Intent(MainActivity.this, ReplayActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
