package com.example.onvifipc.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.model.GBSettingsModel;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.spinner_lib.MaterialSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class GBSettingsFragment extends BaseFragment implements View.OnClickListener, IBaseModelListener<List<String>> {

    @BindView(R.id.platformIndexSpin)
    MaterialSpinner platformIndexSpin;
    @BindView(R.id.checkbox)
    CheckBox cb_isUse;
    @BindView(R.id.streamSpin)
    MaterialSpinner streamSpin;
    @BindView(R.id.et_serverID)
    EditText et_serverID;
    @BindView(R.id.et_serverIP)
    EditText et_serverIP;
    @BindView(R.id.et_serverPort)
    EditText et_serverPort;
    @BindView(R.id.et_deviceID)
    EditText et_deviceID;
    @BindView(R.id.et_devicePort)
    EditText et_devicePort;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.transProtocolSpin)
    MaterialSpinner transProtocolSpin;
    @BindView(R.id.et_mediaChannelID)
    EditText et_mediaChannelID;
    @BindView(R.id.et_expires)
    EditText et_expires;
    @BindView(R.id.et_gap)
    EditText et_gap;
    @BindView(R.id.et_heartCycle)
    EditText et_heartCycle;
    @BindView(R.id.et_timeoutCount)
    EditText et_timeoutCount;
    @BindView(R.id.bt_save)
    Button bt_save;

    private String[] streamTypeList;
    private String[] transList;


    private final String basic;
    private final int position;

    private GBSettingsModel mGbSettingsModel;

    public GBSettingsFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {
        mGbSettingsModel = new GBSettingsModel(basic, position, this);
        streamTypeList = new String[] {"主码流", "次码流"};
        transList = new String[] {"UDP", "TCP"};
        bt_save.setOnClickListener(this);

        mGbSettingsModel.load();
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_gb;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                mGbSettingsModel.update(et_serverID.getText().toString(), et_serverIP.getText().toString(),
                        et_serverPort.getText().toString(), et_deviceID.getText().toString(), et_password.getText().toString(),
                        et_devicePort.getText().toString(), et_expires.getText().toString(), et_gap.getText().toString(),
                        et_heartCycle.getText().toString(), et_timeoutCount.getText().toString(), et_mediaChannelID.getText().toString());
                break;
        }
    }

    @Override
    public void onLoadSuccess(List<String> dataList) {
        platformIndexSpin.setItems(0);
        platformIndexSpin.setSelectedIndex(0);
        platformIndexSpin.setBackgroundResource(R.drawable.spinner_bg);
        cb_isUse.setChecked(Integer.parseInt(dataList.get(1)) != 0);
        streamSpin.setItems(streamTypeList);
        streamSpin.setSelectedIndex(Integer.parseInt(dataList.get(0)));
        streamSpin.setBackgroundResource(R.drawable.spinner_bg);
        transProtocolSpin.setItems(transList);
        transProtocolSpin.setSelectedIndex(Integer.parseInt(dataList.get(9)));
        transProtocolSpin.setBackgroundResource(R.drawable.spinner_bg);
        et_serverID.setText(dataList.get(2));
        et_serverIP.setText(dataList.get(3));
        et_deviceID.setText(dataList.get(4));
        et_password.setText(dataList.get(5));
        et_mediaChannelID.setText(dataList.get(6));
        et_serverPort.setText(dataList.get(7));
        et_devicePort.setText(dataList.get(8));
        et_expires.setText(dataList.get(10));
        et_gap.setText(dataList.get(11));
        et_heartCycle.setText(dataList.get(12));
        et_timeoutCount.setText(dataList.get(13));
    }

    @Override
    public void onLoadFailure(String message) {
        ToastUtils.showToast(getContext(), message);
    }

    @Override
    public void onUpdateSuccess(String response) {
        if (response != null && response.equals("0")) {
            ToastUtils.showToast(getContext(), "参数修改成功!");
        }
    }
}
