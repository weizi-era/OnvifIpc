package com.example.onvifipc.fragment;

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
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.spinner_lib.MaterialSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class GBSettingsFragment extends BaseFragment implements View.OnClickListener {

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
    private Map<String, Integer> map;


    private final String basic;
    private final int position;

    public GBSettingsFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }


    @Override
    protected void onLazyLoad() {
        streamTypeList = new String[] {"主码流", "次码流"};
        transList = new String[] {"UDP", "TCP"};
        bt_save.setOnClickListener(this);
        map = new HashMap<>();
        getGbInfo();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseLoadService.showSuccess();
            }
        }, 1000);
    }

    @Override
    protected void onNetReload(View v) {

    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_gb;
    }

    private void getGbInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getGbInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String streamType = SplitUtils.getValue(stringArray, "root.GB28181.streamType");
                    String enable = SplitUtils.getValue(stringArray, "root.GB28181.enable");
                    String serverId = SplitUtils.getValue(stringArray, "root.GB28181.svrId");
                    String serverIp = SplitUtils.getValue(stringArray, "root.GB28181.svrIp");
                    String devId = SplitUtils.getValue(stringArray, "root.GB28181.devId");
                    String devPwd = SplitUtils.getValue(stringArray, "root.GB28181.devPsw");
                    String mediaId = SplitUtils.getValue(stringArray, "root.GB28181.mediaId");
                    String svrPort = SplitUtils.getValue(stringArray, "root.GB28181.svrPort");
                    String devPort = SplitUtils.getValue(stringArray, "root.GB28181.devPort");
                    String protocol = SplitUtils.getValue(stringArray, "root.GB28181.protocol");
                    String expires = SplitUtils.getValue(stringArray, "root.GB28181.expires");
                    String regInterval = SplitUtils.getValue(stringArray, "root.GB28181.regInterval");
                    String heartBeat = SplitUtils.getValue(stringArray, "root.GB28181.heartBeat");
                    String timeOutCnt = SplitUtils.getValue(stringArray, "root.GB28181.timeOutCnt");

                    int isUse = Integer.parseInt(enable);
                    int streamIndex = Integer.parseInt(streamType);
                    int transIndex = Integer.parseInt(protocol);

                    updateUI(streamIndex, isUse, serverId, serverIp, devId, devPwd, mediaId, transIndex, svrPort, devPort, expires, regInterval, heartBeat, timeOutCnt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d("TAG", "GBSettingsFragment: " + t.getMessage());
            }
        });
    }

    private void updateUI(int streamIndex, int isUse, String serverId, String serverIp, String devId, String devPwd, String mediaId, int transIndex, String svrPort, String devPort, String expires, String regInterval, String heartBeat, String timeOutCnt) {
        platformIndexSpin.setItems(0);
        platformIndexSpin.setSelectedIndex(0);
        platformIndexSpin.setBackgroundResource(R.drawable.spinner_bg);
        cb_isUse.setChecked(isUse != 0);
        streamSpin.setItems(streamTypeList);
        streamSpin.setSelectedIndex(streamIndex);
        streamSpin.setBackgroundResource(R.drawable.spinner_bg);
        transProtocolSpin.setItems(transList);
        transProtocolSpin.setSelectedIndex(transIndex);
        transProtocolSpin.setBackgroundResource(R.drawable.spinner_bg);
        et_serverID.setText(serverId);
        et_serverIP.setText(serverIp);
        et_deviceID.setText(devId);
        et_password.setText(devPwd);
        et_serverPort.setText(svrPort);
        et_devicePort.setText(devPort);
        et_mediaChannelID.setText(mediaId);
        et_expires.setText(expires);
        et_gap.setText(regInterval);
        et_heartCycle.setText(heartBeat);
        et_timeoutCount.setText(timeOutCnt);
    }

    // todo  更新GB28181配置
    private void updateGbInfo() {
        map.put("", Integer.parseInt(et_serverID.getText().toString()));
        map.put("", Integer.parseInt(et_serverIP.getText().toString()));
        map.put("", Integer.parseInt(et_serverPort.getText().toString()));
        map.put("", Integer.parseInt(et_deviceID.getText().toString()));
        map.put("", Integer.parseInt(et_deviceID.getText().toString()));
        map.put("", Integer.parseInt(et_expires.getText().toString()));
        map.put("", Integer.parseInt(et_gap.getText().toString()));
        map.put("", Integer.parseInt(et_password.getText().toString()));
        map.put("", Integer.parseInt(et_heartCycle.getText().toString()));
        map.put("", Integer.parseInt(et_timeoutCount.getText().toString()));

        RetrofitPool.getInstance().getRetrofit(position).create(Api.class)
                .updateGbInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                    if (resultCode != null && resultCode.equals("0")) {
                        ToastUtils.showToast(getContext(), "参数修改成功!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                updateGbInfo();
                break;
        }
    }
}
