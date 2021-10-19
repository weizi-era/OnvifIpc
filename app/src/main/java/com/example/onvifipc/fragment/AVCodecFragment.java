package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.onvifipc.Api;
import com.example.onvifipc.Common;
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
public class AVCodecFragment extends BaseFragment implements MaterialSpinner.OnItemSelectedListener, View.OnClickListener {


    @BindView(R.id.channelSpin)
    MaterialSpinner channelSpin;
    @BindView(R.id.streamTypeSpin)
    MaterialSpinner streamTypeSpin;
    @BindView(R.id.et_streamFrame)
    EditText et_streamFrame;
    @BindView(R.id.cb_firstBitrate)
    CheckBox cb_firstBitrate;
    @BindView(R.id.et_IFrameGap)
    EditText et_IFrameGap;
    @BindView(R.id.et_bitRate)
    EditText et_bitRate;
    @BindView(R.id.streamResolutionSpin)
    MaterialSpinner streamResolutionSpin;
    @BindView(R.id.streamModeSpin)
    MaterialSpinner streamModeSpin;
    @BindView(R.id.encodeTypeSpin)
    MaterialSpinner encodeTypeSpin;
    @BindView(R.id.encodeLevelSpin)
    MaterialSpinner encodeLevelSpin;
    @BindView(R.id.bitrateTypeSpin)
    MaterialSpinner bitrateTypeSpin;
    @BindView(R.id.gopModeSpin)
    MaterialSpinner gopModeSpin;
    @BindView(R.id.bt_save)
    Button bt_save;
    private final String basic;
    private final int position;
    private Map<String, Integer> map;
    private String[] streamTypeList;
    private String[] streamModeList;
    private String[] levelList;
    private String[] encTypeList;
    private String[] bitTypeList;
    private String[] gModeList;
    private String[] decToHexList;
    private int resolution;

    public AVCodecFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onLazyLoad() {
        map = new HashMap<>();
        streamTypeList = new String[] {"主码流", "次码流"};
        streamModeList = new String[] {"视频流", "复合流"};
        levelList = new String[] {"baseline profile", "main profile", "high profile"};
        encTypeList = new String[] {"H.264", "MJPEG", "JPEG", "MPEG4", "H.265"};
        bitTypeList = new String[] {"定码流", "变码流", "按质量编码"};
        gModeList = new String[] {"NORMAL P", "DUAL P", "SMART P"};
        decToHexList = new String[] {"1080P(1920*1080)", "720P(1280*720)"};

        bt_save.setOnClickListener(this);
        streamTypeSpin.setOnItemSelectedListener(this);

        //默认主码流
        getAVCodecInfo(Common.MAIN_STREAM);

//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBaseLoadService.showSuccess();
//            }
//        }, 1000);
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_avcodec;
    }

    private void getAVCodecInfo(int type) {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getCodecInfo("Basic " + basic, type).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String streamMode = SplitUtils.getValue(stringArray, "root.VENC.streamMixType");
                    String h264EncLvl = SplitUtils.getValue(stringArray, "root.VENC.h264EncLvl");
                    String frameRate = SplitUtils.getValue(stringArray, "root.VENC.frameRate");
                    String frPreeferred = SplitUtils.getValue(stringArray, "root.VENC.frPreeferred");
                    String iFrameIntv = SplitUtils.getValue(stringArray, "root.VENC.iFrameIntv");
                    String veType = SplitUtils.getValue(stringArray, "root.VENC.veType");
                    String bitRate = SplitUtils.getValue(stringArray, "root.VENC.bitRate");
                    String bitRateType = SplitUtils.getValue(stringArray, "root.VENC.bitRateType");
                    String resolution = SplitUtils.getValue(stringArray, "root.VENC.resolution");
                    String gopMode = SplitUtils.getValue(stringArray, "root.VENC.gopMode");
                    et_streamFrame.setText(frameRate);
                    et_IFrameGap.setText(iFrameIntv);
                    et_bitRate.setText(bitRate);
                    int frPreeferreds = Integer.parseInt(frPreeferred);
                    int streamModes = Integer.parseInt(streamMode);
                    int level = Integer.parseInt(h264EncLvl);
                    int encType = Integer.parseInt(veType);
                    int bitType = Integer.parseInt(bitRateType);
                    int gMode = Integer.parseInt(gopMode);
                    String decToHex = Integer.toHexString(Integer.parseInt(resolution));
                    updateUI(streamModes, frPreeferreds, level, encType, bitType, gMode, decToHex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updateUI(int frPreeferreds, int streamModes, int level, int encType, int bitType, int gMode, String decToHex) {
        channelSpin.setItems("通道 01");
        channelSpin.setSelectedIndex(0);
        channelSpin.setBackgroundResource(R.drawable.spinner_bg);
        streamTypeSpin.setItems(streamTypeList);
        streamTypeSpin.setSelectedIndex(0);
        streamTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        cb_firstBitrate.setChecked(frPreeferreds != 0);
        streamModeSpin.setItems(streamModeList);
        streamModeSpin.setSelectedIndex(streamModes);
        streamModeSpin.setBackgroundResource(R.drawable.spinner_bg);
        encodeLevelSpin.setItems(levelList);
        encodeLevelSpin.setSelectedIndex(level);
        encodeLevelSpin.setBackgroundResource(R.drawable.spinner_bg);
        encodeTypeSpin.setItems(encTypeList);
        encodeTypeSpin.setSelectedIndex(encType);
        encodeTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        bitrateTypeSpin.setItems(bitTypeList);
        bitrateTypeSpin.setSelectedIndex(bitType);
        bitrateTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        gopModeSpin.setItems(gModeList);
        gopModeSpin.setSelectedIndex(gMode);
        gopModeSpin.setBackgroundResource(R.drawable.spinner_bg);
        streamResolutionSpin.setItems(decToHexList);
        if (decToHex.equals(Common.RES_TYPE_1080)) {
            streamResolutionSpin.setSelectedIndex(0);
        } else {
            streamResolutionSpin.setSelectedIndex(1);
        }
        streamResolutionSpin.setBackgroundResource(R.drawable.spinner_bg);
    }

    private void updateAVCodecInfo() {
        map.put("streamType", streamTypeSpin.getSelectedIndex());
        map.put("VENC.streamMixType", streamModeSpin.getSelectedIndex());
        map.put("VENC.h264EncLvl", encodeLevelSpin.getSelectedIndex());
        map.put("VENC.frameRate", Integer.parseInt(et_bitRate.getText().toString()));
        map.put("VENC.frPreeferred", cb_firstBitrate.isChecked() ? 1 : 0);
        map.put("VENC.iFrameIntv", Integer.parseInt(et_IFrameGap.getText().toString()));
        map.put("VENC.veType", encodeTypeSpin.getSelectedIndex());
        map.put("VENC.bitRate", Integer.parseInt(et_bitRate.getText().toString()));
        map.put("VENC.bitRateType", bitrateTypeSpin.getSelectedIndex());
        map.put("VENC.resolution", resolution);
        map.put("VENC.gopMode", gopModeSpin.getSelectedIndex());
        RetrofitPool.getInstance().getRetrofit(position).create(Api.class)
                .updateCodecInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
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
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ToastUtils.showToast(getContext(), "网络请求失败!" + t.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.streamTypeSpin:
                streamTypeSpin.setSelectedIndex(position);
                getAVCodecInfo(position);
                break;
            case R.id.streamResolutionSpin:
                streamResolutionSpin.setSelectedIndex(position);
                int selectedIndex = streamResolutionSpin.getSelectedIndex();
                if (selectedIndex == 0) {
                    resolution = Integer.parseInt(Common.RES_TYPE_1080, 16);
                } else {
                    resolution = Integer.parseInt(Common.RES_TYPE_720, 16);
                }
                break;
            case R.id.streamModeSpin:
                streamModeSpin.setSelectedIndex(position);
                break;
            case R.id.encodeTypeSpin:
                encodeTypeSpin.setSelectedIndex(position);
                break;
            case R.id.encodeLevelSpin:
                encodeLevelSpin.setSelectedIndex(position);
                break;
            case R.id.bitrateTypeSpin:
                bitrateTypeSpin.setSelectedIndex(position);
                break;
            case R.id.gopModeSpin:
                gopModeSpin.setSelectedIndex(position);
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                updateAVCodecInfo();
                break;
        }
    }
}
