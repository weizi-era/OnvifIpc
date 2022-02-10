package com.example.onvifipc.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.Device;
import com.example.onvifipc.contract.settings.Contract;
import com.example.onvifipc.presenter.SettingsPresenter;
import com.example.onvifipc.ui.activity.RouterConfigActivity;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

public class SettingsFragment extends BaseFragment implements OnRefreshListener, Contract.ISettingsView {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_router)
    LinearLayout ll_router;

    private SettingsPresenter settingsPresenter;

    @Override
    protected void initView() {
        ll_router.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RouterConfigActivity.class));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsPresenter = new SettingsPresenter(this, getContext());
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.autoRefresh();
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_settings;
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        settingsPresenter.findDevices();
        refreshLayout.finishRefresh(4000);
    }

    @Override
    public void onDataSuccess(List<Device> deviceList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "deviceList: " + deviceList);
                CameraAdapter adapter = new CameraAdapter(getContext(), deviceList);
                Log.d("TAG", "adapter: " + adapter);
                recyclerView.setAdapter(adapter);
            }
        });

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
