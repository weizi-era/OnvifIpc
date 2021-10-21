package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.model.NTPTimeModel;
import com.example.onvifipc.utils.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class NTPTimeFragment extends BaseFragment implements View.OnClickListener, OnRefreshListener, IBaseModelListener<String[]> {

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

    private NTPTimeModel mNtpTimeModel;

    public NTPTimeFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_save) {
            mNtpTimeModel.update(newAddress, newInterval);
        }
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        mNtpTimeModel.load();
        refreshLayout.finishRefresh();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {

        mNtpTimeModel = new NTPTimeModel(basic, position, this);
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

        mNtpTimeModel.load();
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_ntp;
    }

    @Override
    public void onLoadSuccess(String[] strings) {
        etAddress.setText(strings[0]);
        etInterval.setText(strings[1]);
    }

    @Override
    public void onLoadFailure(String message) {
        ToastUtils.showToast(getContext(), "服务器错误，修改失败！:" + message);
    }

    @Override
    public void onUpdateSuccess(String response) {
        if (response != null && response.equals("OK")) {
            ToastUtils.showToast(getContext(), "参数修改成功！");
        } else {
            ToastUtils.showToast(getContext(), "参数修改失败！");
        }
    }
}
