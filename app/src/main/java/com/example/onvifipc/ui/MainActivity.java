package com.example.onvifipc.ui;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Common;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.bean.Device;
import com.example.onvifipc.onvif.FindDevicesThread;
import com.example.onvifipc.utils.OnvifSdk;
import com.example.onvifipc.utils.ToastUtils;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements OnRefreshListener {


    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private CameraAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setData() {
        OnvifSdk.initSdk(this);
        initView();
    }

    private void initView() {
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.autoRefresh();
    }

    public void findDevices() {
        OnvifSdk.findDevice(this, "192.168.1.255", new FindDevicesThread.FindDevicesListener() {
            public void searchResult(final ArrayList<Device> devices) {
                if (devices.size() > 0) {
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
        refreshLayout.finishRefresh(3000);
    }
}
