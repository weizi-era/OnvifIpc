package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.model.NetworkModel;
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
public class NetworkFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, IBaseModelListener<String[]> {

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
    private boolean ipIsAuto = true;
    private boolean dnsIsAuto = true;

    private NetworkModel mNetworkModel;

    public NetworkFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    private void initSpinner(List<String> ethLists) {

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
            mNetworkModel.update(ipIsAuto, dnsIsAuto, et_ipv4Addr.getText().toString(), et_ipv4Mask.getText().toString(),
                    et_ipv4Gateway.getText().toString(), et_dnsServer1.getText().toString(), et_dnsServer2.getText().toString());
        }
        getActivity().sendBroadcast(new Intent("FORCE_OFFLINE"));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {
        ethLists = new ArrayList<>();
        mNetworkModel = new NetworkModel(basic, position, this);
        btSave.setOnClickListener(this);
        ipv4Group.setOnCheckedChangeListener(this);
        dnsGroup.setOnCheckedChangeListener(this);

        mNetworkModel.load();
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_network;
    }

    @Override
    public void onLoadSuccess(String[] strings) {

        int dhcp = Integer.parseInt(strings[7]);
        int dns = Integer.parseInt(strings[8]);
        int count = Integer.parseInt(strings[0]);
        tv_physicsAddress.setText(strings[1]);
        et_ipv4Addr.setText(strings[2]);
        et_ipv4Mask.setText(strings[3]);
        et_ipv4Gateway.setText(strings[4]);
        et_dnsServer1.setText(strings[5]);
        et_dnsServer2.setText(strings[6]);
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
        initSpinner(ethLists);
    }

    @Override
    public void onLoadFailure(String message) {
        ToastUtils.showToast(getContext(), message);
    }

    @Override
    public void onUpdateSuccess(String response) {
        if (response != null && response.equals("0")) {
            CameraAdapter.isFirst.put(position, true);
            ToastUtils.showToast(getContext(), "参数修改成功！");
        } else {
            ToastUtils.showToast(getContext(), "参数修改失败！");
        }
    }
}
