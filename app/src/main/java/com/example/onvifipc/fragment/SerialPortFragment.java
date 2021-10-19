package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.SerialData;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.spinner_lib.MaterialSpinner;

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
public class SerialPortFragment extends BaseFragment implements MaterialSpinner.OnItemSelectedListener, View.OnClickListener {
    @BindView(R.id.serialSpin)
    MaterialSpinner serialSpin;
    @BindView(R.id.modeSpin)
    MaterialSpinner modeSpin;
    @BindView(R.id.bpsSpin)
    MaterialSpinner bpsSpin;
    @BindView(R.id.dataSpin)
    MaterialSpinner dataSpin;
    @BindView(R.id.stopSpin)
    MaterialSpinner stopSpin;
    @BindView(R.id.flowSpin)
    MaterialSpinner flowSpin;
    @BindView(R.id.checkSpin)
    MaterialSpinner checkSpin;
    @BindView(R.id.bt_save)
    Button btSave;
    private final String basic;
    private final int position;
    private List<String> serialList;
    private String[] modeList;
    private String[] bpsList;
    private String[] dataList;
    private String[] stopList;
    private String[] flowList;
    private String[] checkList;
    private List<SerialData> serialDataList;
    private Map<String, Integer> map;

    public SerialPortFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onLazyLoad() {
        serialList = new ArrayList<>();
        serialDataList = new ArrayList<>();
        map = new HashMap<>();
        modeList = new String[]{"透明通道", "PTZ 模式", "报警盒模式"};
        bpsList = new String[]{"600", "1200", "1800", "2400", "4800", "9600", "19200", "38400", "57600", "115200"};
        dataList = new String[]{"5", "6", "7", "8"};
        stopList = new String[]{"1", "2"};
        flowList = new String[]{"无", "软流控", "硬流控"};
        checkList = new String[]{"无", "奇校验", "偶校验"};
        btSave.setOnClickListener(this);
        serialSpin.setOnItemSelectedListener(this);
        modeSpin.setOnItemSelectedListener(this);
        bpsSpin.setOnItemSelectedListener(this);
        dataSpin.setOnItemSelectedListener(this);
        stopSpin.setOnItemSelectedListener(this);
        flowSpin.setOnItemSelectedListener(this);
        checkSpin.setOnItemSelectedListener(this);

        getSerialInfo();

//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBaseLoadService.showSuccess();
//            }
//        }, 1000);
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_serialport;
    }

    private void getSerialInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getSerialInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
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
                    SerialData data = serialDataList.get(0);
                    updateUI(count, data.getWorkMode(), data.getBaudRate(),
                            data.getDataBit(), data.getStopBit(), data.getFlowType(), data.getParityType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updateUI(int count, int mode, String rate, String dataBites, String stopBites, int flow, int parity) {
        for (int i = 1; i < count + 1; i++) {
            serialList.add("串口 " + i);
        }
        serialSpin.setItems(serialList);
        serialSpin.setBackgroundResource(R.drawable.spinner_bg);
        serialSpin.setSelectedIndex(0);
        modeSpin.setItems(modeList);
        modeSpin.setBackgroundResource(R.drawable.spinner_bg);
        modeSpin.setSelectedIndex(mode);
        bpsSpin.setItems(bpsList);
        bpsSpin.setBackgroundResource(R.drawable.spinner_bg);
        bpsSpin.setSelectedIndex(SplitUtils.getIndex(bpsList, rate));
        dataSpin.setItems(dataList);
        dataSpin.setBackgroundResource(R.drawable.spinner_bg);
        dataSpin.setSelectedIndex(SplitUtils.getIndex(dataList, dataBites));
        stopSpin.setItems(stopList);
        stopSpin.setBackgroundResource(R.drawable.spinner_bg);
        stopSpin.setSelectedIndex(SplitUtils.getIndex(stopList, stopBites));
        flowSpin.setItems(flowList);
        flowSpin.setBackgroundResource(R.drawable.spinner_bg);
        flowSpin.setSelectedIndex(flow);
        checkSpin.setItems(checkList);
        checkSpin.setBackgroundResource(R.drawable.spinner_bg);
        checkSpin.setSelectedIndex(parity);
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        SerialData data = serialDataList.get(serialSpin.getSelectedIndex());
        switch (view.getId()) {
            case R.id.serialSpin:
                serialSpin.setSelectedIndex(position);
                modeSpin.setSelectedIndex(data.getWorkMode());
                bpsSpin.setSelectedIndex(SplitUtils.getIndex(bpsList, data.getBaudRate()));
                dataSpin.setSelectedIndex(SplitUtils.getIndex(dataList, data.getDataBit()));
                stopSpin.setSelectedIndex(SplitUtils.getIndex(stopList, data.getStopBit()));
                flowSpin.setSelectedIndex(data.getFlowType());
                checkSpin.setSelectedIndex(data.getParityType());
                break;
            case R.id.modeSpin:
                modeSpin.setSelectedIndex(position);
                data.setWorkMode(position);
                break;
            case R.id.bpsSpin:
                bpsSpin.setSelectedIndex(position);
                data.setBaudRate(bpsList[position]);
                break;
            case R.id.dataSpin:
                dataSpin.setSelectedIndex(position);
                data.setDataBit(dataList[position]);
                break;
            case R.id.stopSpin:
                stopSpin.setSelectedIndex(position);
                data.setStopBit(stopList[position]);
                break;
            case R.id.flowSpin:
                flowSpin.setSelectedIndex(position);
                data.setFlowType(position);
                break;
            case R.id.checkSpin:
                checkSpin.setSelectedIndex(position);
                data.setParityType(position);
                break;

        }
    }

    private void updateSerialInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        SerialData data = serialDataList.get(1);
        map.put("SERIAL.no", data.getSerialId());
        map.put("SERIAL.workMode", data.getWorkMode());
        map.put("SERIAL.alarmBox", 0);
        map.put("SERIAL.baudRate", Integer.parseInt(data.getBaudRate()));
        map.put("SERIAL.dataBit", Integer.parseInt(data.getDataBit()));
        map.put("SERIAL.stopBit",Integer.parseInt(data.getStopBit()));
        map.put("SERIAL.flowType", data.getFlowType());

        api.updateSerialInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
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

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                updateSerialInfo();
                break;
        }
    }
}
