package com.example.onvifipc.model;

import com.example.onvifipc.Api;
import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.bean.SerialData;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerialPortModel extends BaseModel<List<SerialData>> {

    private List<SerialData> serialDataList;

    public SerialPortModel(String basic, int position, IBaseModelListener<List<SerialData>> mListener) {
        super(basic, position, mListener);
        serialDataList = new ArrayList<>();
    }

    public void load() {
        doNetRequest().getSerialInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    if (stringArray != null && stringArray.length > 0) {
                        String serialCount = SplitUtils.getValue(stringArray, "root.SERIAL.serialCount");
                        int count = Integer.parseInt(serialCount);
                        for (int i = 0; i < count; i++) {
                            String workMode = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".workMode");
                            String baudRate = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".baudRate");
                            String dataBite = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".dataBit");
                            String stopBite = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".stopBit");
                            String flowType = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".flowType");
                            String parityType = SplitUtils.getValue(stringArray, "root.SERIAL.S" + i + ".parityType");
                            int mode = Integer.parseInt(workMode);
                            int flow = Integer.parseInt(flowType);
                            int parity = Integer.parseInt(parityType);
                            serialDataList.add(new SerialData(i, mode, baudRate, dataBite, stopBite, flow, parity));
                        }

                        mListener.onLoadSuccess(serialDataList);
                    }

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

    public void update() {
        Map<String, Integer> map = new HashMap<>();
        SerialData data = serialDataList.get(1);
        map.put("SERIAL.no", data.getSerialId());
        map.put("SERIAL.workMode", data.getWorkMode());
        map.put("SERIAL.alarmBox", 0);
        map.put("SERIAL.baudRate", Integer.parseInt(data.getBaudRate()));
        map.put("SERIAL.dataBit", Integer.parseInt(data.getDataBit()));
        map.put("SERIAL.stopBit",Integer.parseInt(data.getStopBit()));
        map.put("SERIAL.flowType", data.getFlowType());

        doNetRequest().updateSerialInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
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
