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
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.model.AVCodecModel;
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
public class AVCodecFragment extends BaseFragment implements MaterialSpinner.OnItemSelectedListener, View.OnClickListener, IBaseModelListener<List<String>> {


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
    private String[] streamTypeList;
    private String[] streamModeList;
    private String[] levelList;
    private String[] encTypeList;
    private String[] bitTypeList;
    private String[] gModeList;
    private String[] decToHexList;
    private int resolution;

    private AVCodecModel mAvCodecModel;

    public AVCodecFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onLazyLoad() {
        mAvCodecModel = new AVCodecModel(basic, position, this);
        streamTypeList = new String[]{"主码流", "次码流"};
        streamModeList = new String[]{"视频流", "复合流"};
        levelList = new String[]{"baseline profile", "main profile", "high profile"};
        encTypeList = new String[]{"H.264", "MJPEG", "JPEG", "MPEG4", "H.265"};
        bitTypeList = new String[]{"定码流", "变码流", "按质量编码"};
        gModeList = new String[]{"NORMAL P", "DUAL P", "SMART P"};
        decToHexList = new String[]{"1080P(1920*1080)", "720P(1280*720)"};

        bt_save.setOnClickListener(this);
        streamTypeSpin.setOnItemSelectedListener(this);

        mAvCodecModel.load(Common.MAIN_STREAM);
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_avcodec;
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.streamTypeSpin:
                streamTypeSpin.setSelectedIndex(position);
                mAvCodecModel.load(position);
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
                mAvCodecModel.update(streamTypeSpin.getSelectedIndex(), streamModeSpin.getSelectedIndex(),
                        encodeLevelSpin.getSelectedIndex(), et_streamFrame.getText().toString(), cb_firstBitrate.isChecked(),
                        et_IFrameGap.getText().toString(), encodeTypeSpin.getSelectedIndex(), et_bitRate.getText().toString(),
                        bitrateTypeSpin.getSelectedIndex(), resolution, gopModeSpin.getSelectedIndex());
                break;
        }
    }

    @Override
    public void onLoadSuccess(List<String> dataList) {
        et_streamFrame.setText(dataList.get(2));
        et_IFrameGap.setText(dataList.get(4));
        et_bitRate.setText(dataList.get(6));

        channelSpin.setItems("通道 01");
        channelSpin.setSelectedIndex(0);
        channelSpin.setBackgroundResource(R.drawable.spinner_bg);
        streamTypeSpin.setItems(streamTypeList);
        streamTypeSpin.setSelectedIndex(0);
        streamTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        cb_firstBitrate.setChecked(Integer.parseInt(dataList.get(3)) != 0);
        streamModeSpin.setItems(streamModeList);
        streamModeSpin.setSelectedIndex(Integer.parseInt(dataList.get(0)));
        streamModeSpin.setBackgroundResource(R.drawable.spinner_bg);
        encodeLevelSpin.setItems(levelList);
        encodeLevelSpin.setSelectedIndex(Integer.parseInt(dataList.get(1)));
        encodeLevelSpin.setBackgroundResource(R.drawable.spinner_bg);
        encodeTypeSpin.setItems(encTypeList);
        encodeTypeSpin.setSelectedIndex(Integer.parseInt(dataList.get(5)));
        encodeTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        bitrateTypeSpin.setItems(bitTypeList);
        bitrateTypeSpin.setSelectedIndex(Integer.parseInt(dataList.get(7)));
        bitrateTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
        gopModeSpin.setItems(gModeList);
        gopModeSpin.setSelectedIndex(Integer.parseInt(dataList.get(9)));
        gopModeSpin.setBackgroundResource(R.drawable.spinner_bg);
        streamResolutionSpin.setItems(decToHexList);
        String decToHex = Integer.toHexString(Integer.parseInt(dataList.get(8)));
        if (decToHex.equals(Common.RES_TYPE_1080)) {
            streamResolutionSpin.setSelectedIndex(0);
        } else {
            streamResolutionSpin.setSelectedIndex(1);
        }
        streamResolutionSpin.setBackgroundResource(R.drawable.spinner_bg);
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
