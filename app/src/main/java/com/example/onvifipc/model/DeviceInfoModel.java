package com.example.onvifipc.model;

import com.example.onvifipc.Common;
import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.bean.Params;
import com.example.onvifipc.utils.SplitUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceInfoModel extends BaseModel<List<Params>> {

    public DeviceInfoModel(String basic, int position, IBaseModelListener<List<Params>> mListener) {
        super(basic, position, mListener);
    }

    public void load() {
        doNetRequest().getDeviceInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String value = null;
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String devType = SplitUtils.getValue(stringArray, "root.SYSINFO.devType");
                    if (devType != null && devType.equals("2")) {
                        value = Common.DEV_TYPE;
                    }

                    List<Params> paramsList = new ArrayList<>();
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

                    mListener.onLoadSuccess(paramsList);
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
