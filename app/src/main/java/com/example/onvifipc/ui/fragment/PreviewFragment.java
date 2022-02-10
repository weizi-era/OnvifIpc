package com.example.onvifipc.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.onvifipc.Api;
import com.example.onvifipc.Common;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.contract.preview.Contract;
import com.example.onvifipc.presenter.PreviewPresenter;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PreviewFragment extends BaseFragment implements Contract.IPreviewView {

    @BindView(R.id.video_layout1)
    VLCVideoLayout mVideoLayout1;

    @BindView(R.id.video_layout2)
    VLCVideoLayout mVideoLayout2;

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
    @BindView(R.id.tv_angle)
    TextView tv_angle;
    @BindView(R.id.iv_com1)
    ImageView iv_com1;
    @BindView(R.id.iv_com2)
    ImageView iv_com2;
    @BindView(R.id.tv_videoState)
    TextView tv_videoState;
    @BindView(R.id.tv_temperature)
    TextView tv_temperature;
    @BindView(R.id.tv_humidity)
    TextView tv_humidity;


    private PreviewPresenter previewPresenter;

    private final String basic = Base64Utils.encodedStr("admin" + ":" + "Rock@688051");

    public static boolean STOP_FLAG = false;
    public static boolean START_FLAG = false;

    private Timer timer;
    private TimerTask task;

    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer1;
    private MediaPlayer mMediaPlayer2;


    @Override
    protected void onLazyLoad() {
        onLoadData();
    }

    @Override
    protected void initView() {


        //previewPresenter = new PreviewPresenter(this);

//        btn_full_screen1.setOnClickListener(this);
//        btn_play_video1.setOnClickListener(this);
//        btn_video_back1.setOnClickListener(this);
//        btn_full_screen2.setOnClickListener(this);
//        btn_play_video2.setOnClickListener(this);
//        btn_video_back2.setOnClickListener(this);

//        nodePlayer1 = new NodePlayer(getContext());
//        nodePlayer2 = new NodePlayer(getContext());

        final ArrayList<String> options = new ArrayList<>();
        options.add("-vvv");
        options.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");

        mLibVLC = new LibVLC(context, options);
        mMediaPlayer1 = new MediaPlayer(mLibVLC);
        mMediaPlayer2 = new MediaPlayer(mLibVLC);
        // 前置预览
        //initPlayer(mMediaPlayer1, mVideoLayout1);
        // 后置预览
        //initPlayer(mMediaPlayer2, mVideoLayout2);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mMediaPlayer1.stop();
            mMediaPlayer1.detachViews();
            mMediaPlayer2.stop();
            mMediaPlayer2.detachViews();
        } else {
            setPlayer1();
            setPlayer2();
        }
    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_preview;
    }

    private void onLoadData() {

        previewPresenter = new PreviewPresenter(this);
        timer = new Timer();
        previewPresenter.getHardwareData(Common.CODE);
        previewPresenter.getComState();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task = new TimerTask() {
            @Override
            public void run() {
                previewPresenter.getHardwareData(Common.PARAMS);
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 4000);

    }

    /**
     * 初始化播放器
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer(MediaPlayer mMediaPlayer, VLCVideoLayout mVideoLayout) {
        mMediaPlayer.getVLCVout().setWindowSize(mVideoLayout.getWidth(), mVideoLayout.getHeight());//宽，高  播放窗口的大小
        mMediaPlayer.setAspectRatio(mVideoLayout.getWidth() + ":" + mVideoLayout.getHeight());//宽，高  画面大小
        mMediaPlayer.setScale(0);
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void showComState1(boolean isConnected) {
        if (isConnected) {
            iv_com1.setImageResource(R.drawable.blue_state);
        } else {
            iv_com1.setImageResource(R.drawable.red_state);
        }
    }

    @Override
    public void showComState2(String result) {
        Message message = Message.obtain();
        message.what = Common.COM;
        message.obj = result;
        handler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.COM:
                    String result = (String) msg.obj;
                    if (result != null) {
                        iv_com2.setImageResource(R.drawable.blue_state);
                    } else {
                        iv_com2.setImageResource(R.drawable.red_state);
                    }
                    break;
                case Common.CODE:
                    List<String> codeList = (List<String>) msg.obj;
                    if (codeList != null && codeList.size() == 2) {
                        tv_mnCode.setText(codeList.get(1) + "01DF00" + codeList.get(0));
                    } else {
                        ToastUtils.showToast(context, "MN号数据出错");
                    }
                    break;
                case Common.PARAMS:
                    List<Float> paramsList = (List<Float>) msg.obj;
                    if (paramsList != null && paramsList.size() == 12) {
                        tv_temperature.setText(String.format("%.2f", paramsList.get(0)) + " ℃");
                        tv_humidity.setText(String.format("%.2f", paramsList.get(1)) + " RH");
                        tv_latitude.setText(String.format("%.6f", paramsList.get(2)));
                        tv_longitude.setText(String.format("%.6f", paramsList.get(3)));
                        tv_outSideVoltage.setText(String.format("%.2f", paramsList.get(4)) + " V");
                        tv_BatteryVoltage.setText(String.format("%.2f", paramsList.get(5)) + " V");
                        tv_speed.setText(String.format("%.2f", paramsList.get(9)) + " km/h");
                        tv_angle.setText(String.format("%.2f", paramsList.get(10)) + " 度");
                        //float v = Float.parseFloat(paramsList.get(11));
                        tv_lastElectricity.setText(String.format("%.2f", paramsList.get(11) * 100) + " %");

                        if (Float.parseFloat(String.format("%.2f", paramsList.get(4))) < 3.00f) {
                            Log.d("zjw", "外部电压: " + paramsList.get(4));
                            Log.d("zjw", "STOP_FLAG: " + STOP_FLAG);
                            if (!STOP_FLAG) {
                                controlVideo("http://192.168.1.160", 0);
                                controlVideo("http://192.168.1.161", 0);
                                STOP_FLAG = true;
                                START_FLAG = false;
                                tv_videoState.setTextColor(Color.RED);
                                tv_videoState.setText("停止录像");
                            }
                        } else {
                            Log.d("zjw", "外部电压: " + paramsList.get(4));
                            Log.d("zjw", "START_FLAG: " + START_FLAG);
                            if (!START_FLAG) {
                                controlVideo("http://192.168.1.160", 1);
                                controlVideo("http://192.168.1.161", 1);
                                START_FLAG = true;
                                STOP_FLAG = false;
                                tv_videoState.setTextColor(Color.GREEN);
                                tv_videoState.setText("正在录像");
                            }
                        }
                    } else {
                        ToastUtils.showToast(context, "硬件参数数据出错");
                    }
                    break;
            }
        }
    };

    @Override
    public void onSuccess(int tag, List<String> result) {
        Message message = Message.obtain();
        message.what = tag;
        message.obj = result;
        handler.sendMessage(message);
    }

    @Override
    public void onParamsSuccess(int tag, List<Float> result) {
        Message message = Message.obtain();
        message.what = tag;
        message.obj = result;
        handler.sendMessage(message);
    }

    /**
     * 当外部电压小于3V时，停止录像
     */
    private void controlVideo(String url, int enabled) {

        Map<String, Integer> map = new HashMap<>();
        map.put("RECORD.enableRec", enabled);
        map.put("RECORD.DAY7.allDayFlag", enabled);
        new Retrofit.Builder().baseUrl(url).build()
                .create(Api.class)
                .stopTimeRecord("Basic " + basic, map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String[] stringArray = SplitUtils.getStringArray(response.body());
                            String resultCode = SplitUtils.getValue(stringArray, "root.ERR.no");
                            if (resultCode != null && resultCode.equals("0")) {
                                if (enabled == 0) {
                                    ToastUtils.showToast(getContext(), "定时录像关闭");
                                } else if (enabled == 1) {
                                    ToastUtils.showToast(getContext(), "定时录像打开");
                                }
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

    }

    @Override
    public void onLoadSuccess() {

    }

    @Override
    public void onStart() {
        super.onStart();
        setPlayer1();
        setPlayer2();
    }

    private void setPlayer1() {
        mMediaPlayer1.attachViews(mVideoLayout1, null, true, false);
        Media media = new Media(mLibVLC, Uri.parse("rtsp://admin:Rock@688051@192.168.1.160:554/0"));
        //网络缓存
        media.addOption(":network-caching=220");
        mMediaPlayer1.setMedia(media);
        media.release();
        mMediaPlayer1.play();
    }

    private void setPlayer2() {
        mMediaPlayer2.attachViews(mVideoLayout2, null, true, false);
        Media media = new Media(mLibVLC, Uri.parse("rtsp://admin:Rock@688051@192.168.1.161:554/0"));
        //网络缓存
        media.addOption(":network-caching=220");
        mMediaPlayer2.setMedia(media);
        media.release();
        mMediaPlayer2.play();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer1.release();
        mMediaPlayer2.release();
        mLibVLC.release();
    }
}
