package com.example.onvifipc.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.utils.DensityUtil;
import com.example.onvifipc.view.MyVideoView;

import butterknife.BindView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

@SuppressLint("NonConstantResourceId")
public class RealVideoActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    private boolean ISFULLSCREEN = false;
    private boolean ISPLAYING = false;
    @BindView(R.id.rl_group1)
    RelativeLayout rl_group1;
    @BindView(R.id.video_view1)
    NodePlayerView nodePlayerView1;
    @BindView(R.id.pb_progressbar1)
    ProgressBar pb_progressbar1;
    @BindView(R.id.btn_full_video1)
    Button btn_full_screen1;
    @BindView(R.id.btn_play_video1)
    Button btn_play_video1;
    @BindView(R.id.btn_video_back1)
    Button btn_video_back1;

    @BindView(R.id.rl_group2)
    RelativeLayout rl_group2;
    @BindView(R.id.video_view2)
    NodePlayerView nodePlayerView2;
    @BindView(R.id.pb_progressbar2)
    ProgressBar pb_progressbar2;
    @BindView(R.id.btn_full_video2)
    Button btn_full_screen2;
    @BindView(R.id.btn_play_video2)
    Button btn_play_video2;
    @BindView(R.id.btn_video_back2)
    Button btn_video_back2;

    @BindView(R.id.ll_replay)
    LinearLayout ll_replay;

    private NodePlayer nodePlayer1;
    private NodePlayer nodePlayer2;

    @Override
    protected void setData() {
        initView();
        initPlayer1();
        initPlayer2();
    }

    private void initView() {
        btn_full_screen1.setOnClickListener(this);
        btn_play_video1.setOnClickListener(this);
        btn_video_back1.setOnClickListener(this);
        btn_full_screen2.setOnClickListener(this);
        btn_play_video2.setOnClickListener(this);
        btn_video_back2.setOnClickListener(this);
        ll_replay.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_real_video;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer1() {
        nodePlayerView1.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView1.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        nodePlayer1 = new NodePlayer(this);
        //设置播放视图
        nodePlayer1.setPlayerView(nodePlayerView1);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer1.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer1.setInputUrl("rtsp://192.168.1.160:8554/0");
        nodePlayer1.setVideoEnable(true);
        nodePlayer1.setBufferTime(0);
        pb_progressbar1.setVisibility(View.GONE);
        nodePlayer1.start();

        nodePlayerView1.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer2() {

        nodePlayerView2.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView2.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        nodePlayer2 = new NodePlayer(this);
        //设置播放视图
        nodePlayer2.setPlayerView(nodePlayerView2);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer2.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer2.setInputUrl("rtsp://192.168.1.161:8554/0");
        nodePlayer2.setVideoEnable(true);
        nodePlayer2.setBufferTime(0);
        pb_progressbar2.setVisibility(View.GONE);
        nodePlayer2.start();

        nodePlayerView2.setOnTouchListener(this);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 100:
                    btn_full_screen1.setVisibility(View.GONE);
                    btn_play_video1.setVisibility(View.GONE);
                    btn_video_back1.setVisibility(View.GONE);
                    break;
                case 200:
                    btn_full_screen2.setVisibility(View.GONE);
                    btn_play_video2.setVisibility(View.GONE);
                    btn_video_back2.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.video_view1:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_video_back1.setVisibility(View.VISIBLE);
                    btn_play_video1.setVisibility(View.VISIBLE);
                    btn_full_screen1.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(100, 3000L);
                }
                break;
            case R.id.video_view2:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_video_back2.setVisibility(View.VISIBLE);
                    btn_play_video2.setVisibility(View.VISIBLE);
                    btn_full_screen2.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(200, 3000L);
                }
                break;
        }

        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_video1:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group1.setLayoutParams(params);
                } else {
                    ISFULLSCREEN = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    rl_group1.setLayoutParams(params);
                }
                break;
            case R.id.btn_video_back1:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group1.setLayoutParams(params);
                } else {
                    finish();
                }
                break;
            case R.id.btn_play_video1:
                if (ISPLAYING) {
                    if (nodePlayer1 != null) {
                        nodePlayer1.pause();
                        ISPLAYING = false;
                        btn_play_video1.setBackgroundResource(R.drawable.icon_play);
                    }
                } else {
                    if (nodePlayer1 != null) {
                        nodePlayer1.start();
                        ISPLAYING = true;
                        btn_play_video1.setBackgroundResource(R.drawable.icon_topause);
                    }
                }
                break;
            case R.id.btn_full_video2:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group2.setLayoutParams(params);
                } else {
                    ISFULLSCREEN = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    rl_group2.setLayoutParams(params);
                }
                break;
            case R.id.btn_video_back2:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group2.setLayoutParams(params);
                } else {
                    finish();
                }
                break;
            case R.id.btn_play_video2:
                if (ISPLAYING) {
                    if (nodePlayer2 != null) {
                        nodePlayer2.pause();
                        ISPLAYING = false;
                        btn_play_video2.setBackgroundResource(R.drawable.icon_play);
                    }
                } else {
                    if (nodePlayer2 != null) {
                        nodePlayer2.start();
                        ISPLAYING = true;
                        btn_play_video2.setBackgroundResource(R.drawable.icon_topause);
                    }
                }
                break;
            case R.id.ll_replay:
                startActivity(new Intent(RealVideoActivity.this, ReplayActivity.class));
                break;
        }
    }
}