package com.example.onvifipc.model;

import android.widget.EditText;

import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkModel extends BaseModel<String[]> {

    public NetworkModel(String basic, int position, IBaseModelListener<String[]> mListener) {
        super(basic, position, mListener);
    }

    public void load() {
        doNetRequest().getNetworkInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
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

                    String[] data = new String[]{ethCount, physicsAddress, ipv4Address, ipv4Mask, ipv4Gateway,
                            dnsServer1, dnsServer2, autoDhcp, autoDns};

                    mListener.onLoadSuccess(data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                mListener.onLoadFailure(t.getMessage());
            }
        });
    }

    public void update(boolean ipIsAuto, boolean dnsIsAuto, String ipv4Addr, String ipv4Mask, String ipv4Gateway, String dnsServer1, String dnsServer2) {
        Map<String, String> map = new HashMap<>();
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

        map.put("ETH.ipaddr", ipv4Addr);
        map.put("ETH.netmask", ipv4Mask);
        map.put("ETH.gateway", ipv4Gateway);
        map.put("ETH.dns1", dnsServer1);
        map.put("ETH.dns2", dnsServer2);

        doNetRequest().updateNetworkInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                    mListener.onUpdateSuccess(resultCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mListener.onLoadFailure(t.getMessage());
            }
        });
    }

}
