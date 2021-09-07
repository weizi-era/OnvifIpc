package com.example.onvifipc.fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Api;
import com.example.onvifipc.Common;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraParamsAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.Params;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeviceInfoFragment extends BaseFragment implements OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.device_refresh)
    SmartRefreshLayout refreshLayout;

    private List<Params> paramsList;
    private String basic;
    private int position;
    private CameraParamsAdapter adapter;


    public DeviceInfoFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    private void getCameraParams() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getDeviceInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String value = null;
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String devType = SplitUtils.getValue(stringArray, "root.SYSINFO.devType");
                    if (devType != null && devType.equals("2")) {
                        value = Common.DEV_TYPE;
                    }
                    Log.d("TAG", "devType===: "  + devType);
                    paramsList.add(new Params("设备类型", value));
                    paramsList.add(new Params("设备型号", SplitUtils.getValue(stringArray, "root.SYSINFO.productName")));
                    paramsList.add(new Params("厂商名称", SplitUtils.getValue(stringArray, "root.SYSINFO.companyName")));
                    paramsList.add(new Params("厂商地址", SplitUtils.getValue(stringArray, "root.SYSINFO.companyAddr")));
                    paramsList.add(new Params("设备ID", SplitUtils.getValue(stringArray, "root.SYSINFO.serialID")));
                    paramsList.add(new Params("软件版本", SplitUtils.getValue(stringArray, "root.SYSINFO.softwareVer")));
                    paramsList.add(new Params("软件编译日期", SplitUtils.getValue(stringArray, "root.SYSINFO.softwareDate")));
                    paramsList.add(new Params("DSP软件版本", SplitUtils.getValue(stringArray, "root.SYSINFO.dspSoftwareVer")));
                    paramsList.add(new Params("DSP软件编译日期", SplitUtils.getValue(stringArray, "root.SYSINFO.dspSoftwareDate")));
                    paramsList.add(new Params("前面板版本", SplitUtils.getValue(stringArray, "root.SYSINFO.panelVer")));
                    paramsList.add(new Params("硬件版本", SplitUtils.getValue(stringArray, "root.SYSINFO.hardwareVer")));

                    adapter = new CameraParamsAdapter(paramsList);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        Log.d("TAG", "onRefresh: " + adapter);
        adapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    protected void onLazyLoad() {
        paramsList = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setOnRefreshListener(this);

        getCameraParams();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseLoadService.showSuccess();
            }
        }, 1000);
    }

    @Override
    protected void onNetReload(View v) {
        Log.d("TAG", "onNetReload: ");
        getCameraParams();
//        mBaseLoadService.showCallback(LoadingCallback.class);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBaseLoadService.showSuccess();
//            }
//        }, 1000);

    }

    @Override
    protected int onCreateFragmentView() {
        Log.d("TAG", "onCreateFragmentView: ");
        return R.layout.fragment_deviceinfo;
    }
}
