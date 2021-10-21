package com.example.onvifipc.fragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraParamsAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.Params;
import com.example.onvifipc.model.DeviceInfoModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.utils.ToastUtils;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;


public class DeviceInfoFragment extends BaseFragment implements OnRefreshListener, IBaseModelListener<List<Params>> {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.device_refresh)
    SmartRefreshLayout refreshLayout;

    private String basic;
    private int position;
    private CameraParamsAdapter adapter;
    private DeviceInfoModel mDeviceInfoModel;


    public DeviceInfoFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        Log.d("TAG", "onRefresh: " + adapter);
        adapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setOnRefreshListener(this);

        mDeviceInfoModel = new DeviceInfoModel(basic, position, this);
        mDeviceInfoModel.load();
    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_deviceinfo;
    }

    @Override
    public void onLoadSuccess(List<Params> params) {
        adapter = new CameraParamsAdapter(params);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadFailure(String message) {
        ToastUtils.showToast(getContext(), "服务器出错，请求失败:" + message);
    }

    @Override
    public void onUpdateSuccess(String response) {

    }
}
