package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class NTPTimeFragment extends BaseFragment implements View.OnClickListener, OnRefreshListener {

    @BindView(R.id.ntp_refresh)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.etAddress)
    TextInputEditText etAddress;
    @BindView(R.id.etInterval)
    TextInputEditText etInterval;
    @BindView(R.id.bt_save)
    Button btSave;
    private final String basic;
    private final int position;
    private String newAddress;
    private String newInterval;

    public NTPTimeFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    private void getNtpParams() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getNTPInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String ntpSvr = SplitUtils.getValue(stringArray, "root.NTP.ntpSvr");
                    String interval = SplitUtils.getValue(stringArray, "root.NTP.interval");
                    etAddress.setText(ntpSvr);
                    etInterval.setText(interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void updateNtpParams(String newAddress, String newInterval) {
        Log.d("TAG", "newAddress==: " + newAddress);
        Log.d("TAG", "newInterval==: " + newInterval);
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.updateNTPInfo("Basic " + basic, newAddress, newInterval).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String des = SplitUtils.getValue(stringArray, "root.ERR.des");
                    Log.d("TAG", "updateNtpParams: " + des);
                    if (des != null && des.equals("OK")) {
                        ToastUtils.showToast(getContext(), "参数修改成功！");
                    } else {
                        ToastUtils.showToast(getContext(), "参数修改失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ToastUtils.showToast(getContext(), "服务器错误，修改失败！" + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_save) {
            updateNtpParams(newAddress, newInterval);
        }
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        getNtpParams();
        refreshLayout.finishRefresh();
    }

    @Override
    protected void onLazyLoad() {
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()));
        refreshLayout.setOnRefreshListener(this);
        btSave.setOnClickListener(this);
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btSave.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btSave.setVisibility(View.VISIBLE);
                newAddress = s.toString();
            }
        });
        etInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btSave.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btSave.setVisibility(View.VISIBLE);
                newInterval = s.toString();
            }
        });

        getNtpParams();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseLoadService.showSuccess();
            }
        }, 1000);

    }

    @Override
    protected void onNetReload(View v) {
        getNtpParams();
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
        return R.layout.fragment_ntp;
    }
}
