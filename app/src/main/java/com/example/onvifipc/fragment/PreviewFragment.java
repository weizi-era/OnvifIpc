package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.base.Event;
import com.example.onvifipc.contract.preview.Contract;
import com.example.onvifipc.presenter.PreviewPresenter;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PreviewFragment extends BaseFragment implements  Contract.IPreviewView {

    @BindView(R.id.rl_group1)
    RelativeLayout rl_group1;
    @BindView(R.id.video_view1)
    NodePlayerView nodePlayerView1;

    @BindView(R.id.rl_group2)
    RelativeLayout rl_group2;
    @BindView(R.id.video_view2)
    NodePlayerView nodePlayerView2;

    @BindView(R.id.tv_latitude)
    TextView tv_latitude;
    @BindView(R.id.tv_longitude)
    TextView tv_longitude;
    @BindView(R.id.tv_outSideVoltage)
    TextView tv_outSideVoltage;
    @BindView(R.id.tv_BatteryVoltage)
    TextView tv_BatteryVoltage;
    @BindView(R.id.tv_lastElectricity)
    TextView tv_lastElectricity;
    @BindView(R.id.tv_speed)
    TextView tv_speed;
    @BindView(R.id.tv_mnCode)
    TextView tv_mnCode;
    @BindView(R.id.iv_com1)
    ImageView iv_com1;
    @BindView(R.id.iv_com2)
    ImageView iv_com2;

    public NodePlayer nodePlayer1;
    public NodePlayer nodePlayer2;

    private PreviewPresenter previewPresenter;

    private final String basic = Base64Utils.encodedStr("admin" + ":" + "Rock@688051");

    private static final int LATITUDE = 1;
    private static final int LONGITUDE = 2;
    private static final int OUTSIDEVOLTAGE = 3;
    private static final int BATTERYVOLTAGE = 4;
    private static final int SPEED = 5;
    public static final int MNTOPFOUR = 6;
    public static final int MNLASTFOUR = 7;
    public static final int LASTBAT = 8;

    private Timer timer;
    private TimerTask task;
    private String[] mnList = new String[2];


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void onLazyLoad() {
        initView();
        onLoadData();
    }

    @Override
    protected void initView() {

        previewPresenter = new PreviewPresenter(this);
        timer = new Timer();
//        btn_full_screen1.setOnClickListener(this);
//        btn_play_video1.setOnClickListener(this);
//        btn_video_back1.setOnClickListener(this);
//        btn_full_screen2.setOnClickListener(this);
//        btn_play_video2.setOnClickListener(this);
//        btn_video_back2.setOnClickListener(this);

        nodePlayer1 = new NodePlayer(getContext());
        nodePlayer2 = new NodePlayer(getContext());
        // 前置预览
        initPlayer(nodePlayer1, nodePlayerView1, "rtsp://192.168.1.160:8554/0");
        // 后置预览
        initPlayer(nodePlayer2, nodePlayerView2, "rtsp://192.168.1.161:8554/0");

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            nodePlayer1.pause();
            nodePlayer2.pause();
        } else {
            nodePlayer1.start();
            nodePlayer2.start();
        }
    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_preview;
    }

    private void onLoadData() {

        previewPresenter.getHardwareData(0xBF, MNTOPFOUR); // 设备MN号前四位
        previewPresenter.getHardwareData(0xBE, MNLASTFOUR); // 设备MN号后四位

        task = new TimerTask() {
            @Override
            public void run() {
                previewPresenter.getHardwareData(0x04, LATITUDE);  // 纬度
                previewPresenter.getHardwareData(0x06, LONGITUDE);  // 经度
                previewPresenter.getHardwareData(0x08, OUTSIDEVOLTAGE);  // 外部电压
                previewPresenter.getHardwareData(0x0A, BATTERYVOLTAGE);  // 电池电量
                previewPresenter.getHardwareData(0x12, SPEED); // 速度
                previewPresenter.getHardwareData(0x16, LASTBAT); // 剩余电量
            }
        };

        timer.schedule(task, 0, 4000);

    }

    /**
     * 初始化播放器
     * @param nodePlayer
     * @param nodePlayerView
     * @param url
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer(NodePlayer nodePlayer, NodePlayerView nodePlayerView, String url) {

        //progressbar.setVisibility(View.VISIBLE);
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer.setInputUrl(url);
        nodePlayer.setVideoEnable(true);
        nodePlayer.setBufferTime(0);
        nodePlayer.start();
        //progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void showComState1(String result) {
        if (result != null) {
            iv_com1.setImageResource(R.mipmap.red);
        }
    }

    @Override
    public void showComState2(String result) {
        if (result != null) {
            iv_com2.setImageResource(R.mipmap.blue);
        } else {
            iv_com2.setImageResource(R.mipmap.red);
        }
    }


    @Override
    public void onDataSuccess(int tag, String result) {
        switch (tag) {
            case LATITUDE:
                tv_latitude.setText(result);
                break;
            case LONGITUDE:
                tv_longitude.setText(result);
                break;
            case OUTSIDEVOLTAGE:
                tv_outSideVoltage.setText(result + "V");
                if (Float.parseFloat(result) < 3) {
                    stopVideo();
                }
                break;
            case BATTERYVOLTAGE:
                tv_BatteryVoltage.setText(result + "V");
                break;
            case SPEED:
                tv_speed.setText(result + "km/h");
                break;
            case MNTOPFOUR:
                mnList[0] = result;
                break;
            case MNLASTFOUR:
                mnList[1] = result;
                tv_mnCode.setText(mnList[0] + "01DF00" + mnList[1]);
                break;
            case LASTBAT:
                tv_lastElectricity.setText(result + "%");
        }
    }

    /**
     * 当外部电压小于3V时，停止录像
     */
    private void stopVideo() {

        new Retrofit.Builder().baseUrl("http://192.168.1.160").build()
                .create(Api.class)
                .updateTimeRecord("Basic " + basic, 0)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String[] stringArray = SplitUtils.getStringArray(response.body());
                            String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                            if (resultCode != null && resultCode.equals("0")) {
                                //ToastUtils.showToast(getContext(), "参数修改成功!");
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
    public void onLoadFailed() {

    }

    @Override
    public void onLoading() {
       startLoadingView();
    }

    @Override
    public void onLoadSuccess() {

    }

    public void startLoadingView() {
        Event e = new Event();
        e.type = Event.TYPE_HARDWARE;
        EventBus.getDefault().post(e);
    }
}
