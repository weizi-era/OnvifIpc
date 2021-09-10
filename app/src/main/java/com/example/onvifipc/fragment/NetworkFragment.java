package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.ui.LoginActivity;
import com.example.onvifipc.utils.ActivityCollector;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.spinner_lib.MaterialSpinner;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class NetworkFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.ipv4_group)
    RadioGroup ipv4Group;
    @BindView(R.id.dns_group)
    RadioGroup dnsGroup;
    @BindView(R.id.autoIPV4)
    RadioButton autoIpv4;
    @BindView(R.id.useIPV4)
    RadioButton useIpv4;
    @BindView(R.id.autoDNS)
    RadioButton autoDns;
    @BindView(R.id.useDNS)
    RadioButton useDns;
    @BindView(R.id.tv_physicsAddr)
    TextView tv_physicsAddress;
    @BindView(R.id.ipv4_addr)
    EditText et_ipv4Addr;
    @BindView(R.id.ipv4_mask)
    EditText et_ipv4Mask;
    @BindView(R.id.ipv4_gateway)
    EditText et_ipv4Gateway;
    @BindView(R.id.dns_server1)
    EditText et_dnsServer1;
    @BindView(R.id.dns_server2)
    EditText et_dnsServer2;
    @BindView(R.id.bt_save)
    Button btSave;
    private List<String> ethLists;
    private final String basic;
    private final int position;
    private int count = 0;
    private int dhcp = 0;
    private int dns = 0;
    private Map<String, String> map;
    private boolean ipIsAuto = true;
    private boolean dnsIsAuto = true;

    public NetworkFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    private void initSpinner(List<String> ethLists) {
        Log.d("TAG", "initSpinner的网卡count ==: " + count);
        spinner.setItems(ethLists);
        spinner.setSelectedIndex(0);
        spinner.setBackgroundResource(R.drawable.spinner_bg);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                ToastUtils.showToast(getContext(), "点击了" + ethLists.get(position));
            }
        });

    }

    private void getNetworkInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getNetworkInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String ethCount = SplitUtils.getValue(stringArray, "root.ETH.ethCount");
                    String physicsAddress = SplitUtils.getValue(stringArray, "root.ETH.E0.mac");
                    String ipv4Address = SplitUtils.getValue(stringArray, "root.ETH.E0.ipaddr");
                    String ipv4Mask = SplitUtils.getValue(stringArray, "root.ETH.E0.netmask");
                    String ipv4Gateway = SplitUtils.getValue(stringArray, "root.ETH.E0.gateway");
                    String dnsServer1 = SplitUtils.getValue(stringArray, "root.ETH.E0.dns1");
                    String dnsServer2 = SplitUtils.getValue(stringArray, "root.ETH.E0.dns2");
                    String autoDhcp = SplitUtils.getValue(stringArray, "root.ETH.E0.dhcp");
                    String autoDns = SplitUtils.getValue(stringArray, "root.ETH.E0.autoDns");
                    dhcp = Integer.parseInt(autoDhcp);
                    dns = Integer.parseInt(autoDns);
                    count = Integer.parseInt(ethCount);
                    updateUI(count, dhcp, dns, physicsAddress, ipv4Address, ipv4Mask, ipv4Gateway, dnsServer1, dnsServer2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void updateUI(int count, int dhcp, int dns, String physicsAddress, String ipv4Address, String ipv4Mask, String ipv4Gateway, String dnsServer1, String dnsServer2) {
        Log.d("TAG", "dhcp=== : " + dhcp);
        tv_physicsAddress.setText(physicsAddress);
        et_ipv4Addr.setText(ipv4Address);
        et_ipv4Mask.setText(ipv4Mask);
        et_ipv4Gateway.setText(ipv4Gateway);
        et_dnsServer1.setText(dnsServer1);
        et_dnsServer2.setText(dnsServer2);
        if (dhcp == 0) {
            useIpv4.setChecked(true);
        } else {
            autoIpv4.setChecked(true);
        }

        if (dns == 0) {
            useDns.setChecked(true);
        } else {
            autoDns.setChecked(true);
        }
        for (int i = 1; i < count + 1; i++) {
            ethLists.add("ETH " + i);
        }
        Log.d("TAG", "这里的网卡count ==: " + count);
        initSpinner(ethLists);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.ipv4_group:
                if (autoIpv4.isChecked()) {
                    ipIsAuto = true;
                    et_ipv4Addr.setBackgroundResource(R.drawable.ip_shape);
                    et_ipv4Addr.setEnabled(false);
                    et_ipv4Mask.setBackgroundResource(R.drawable.ip_shape);
                    et_ipv4Mask.setEnabled(false);
                    et_ipv4Gateway.setBackgroundResource(R.drawable.ip_shape);
                    et_ipv4Gateway.setEnabled(false);
                } else {
                    ipIsAuto = false;
                    et_ipv4Addr.setBackgroundResource(R.drawable.params_bg);
                    et_ipv4Addr.setEnabled(true);
                    et_ipv4Mask.setBackgroundResource(R.drawable.params_bg);
                    et_ipv4Mask.setEnabled(true);
                    et_ipv4Gateway.setBackgroundResource(R.drawable.params_bg);
                    et_ipv4Gateway.setEnabled(true);
                }
                break;
            case R.id.dns_group:
                if (autoDns.isChecked()) {
                    dnsIsAuto = true;
                    et_dnsServer1.setBackgroundResource(R.drawable.ip_shape);
                    et_dnsServer1.setEnabled(false);
                    et_dnsServer2.setBackgroundResource(R.drawable.ip_shape);
                    et_dnsServer2.setEnabled(false);
                } else {
                    dnsIsAuto = false;
                    et_dnsServer1.setBackgroundResource(R.drawable.params_bg);
                    et_dnsServer1.setEnabled(true);
                    et_dnsServer2.setBackgroundResource(R.drawable.params_bg);
                    et_dnsServer2.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_save) {
            updateNetworkInfo();
        }
        getActivity().sendBroadcast(new Intent("FORCE_OFFLINE"));
    }

    private void updateNetworkInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        if (ipIsAuto) {
            map.put("ETH.dhcp", "1");
        } else {
            map.put("ETH.dhcp", "0");
        }
        if (dnsIsAuto) {
            map.put("ETH.autoDns", "1");
        } else {
            map.put("ETH.autoDns", "0");
        }
        map.put("ETH.ipaddr", et_ipv4Addr.getText().toString());
        map.put("ETH.netmask", et_ipv4Mask.getText().toString());
        map.put("ETH.gateway", et_ipv4Gateway.getText().toString());
        map.put("ETH.dns1", et_dnsServer1.getText().toString());
        map.put("ETH.dns2", et_dnsServer2.getText().toString());
        api.updateNetworkInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                    if (resultCode != null && resultCode.equals("0")) {
                        CameraAdapter.isFirst.put(position, true);
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

            }
        });
    }


    @Override
    protected void onLazyLoad() {
        btSave.setOnClickListener(this);
        ipv4Group.setOnCheckedChangeListener(this);
        dnsGroup.setOnCheckedChangeListener(this);

        getNetworkInfo();
        ethLists = new ArrayList<>();
        map = new HashMap<>();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseLoadService.showSuccess();
            }
        }, 1000);
    }

    @Override
    protected void onNetReload(View v) {
        getNetworkInfo();
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
        return R.layout.fragment_network;
    }

}
