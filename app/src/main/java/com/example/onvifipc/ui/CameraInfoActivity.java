package com.example.onvifipc.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.callback.RetrofitCallback;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.adapter.MyPagerAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.fragment.AVCodecFragment;
import com.example.onvifipc.fragment.DeviceInfoFragment;
import com.example.onvifipc.fragment.GBSettingsFragment;
import com.example.onvifipc.fragment.NTPTimeFragment;
import com.example.onvifipc.fragment.NetworkFragment;
import com.example.onvifipc.fragment.SerialPortFragment;
import com.example.onvifipc.fragment.UserManagerFragment;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.DensityUtil;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.view.MyVideoView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class CameraInfoActivity extends BaseActivity implements View.OnClickListener, RetrofitCallback, View.OnTouchListener {
    private boolean ISFULLSCREEN = false;
    private boolean ISPLAYING = false;
    @BindView(R.id.rl_group)
    RelativeLayout rl_group;
    @BindView(R.id.video_view)
    NodePlayerView nodePlayerView;
    @BindView(R.id.pb_progressbar)
    ProgressBar pb_progressbar;
    @BindView(R.id.btn_full_video)
    Button btn_full_screen;
    @BindView(R.id.btn_play_video)
    Button btn_play_video;
    @BindView(R.id.btn_video_back)
    Button btn_video_back;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private NodePlayer nodePlayer;

    private String basic;
    private int position;
    private SharedPreferences sp;

    private String username;

    private String userpwd;

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_camera_info;
    }

    @Override
    protected void initData() {
        checkUserNameAndPwd();
        basic = Base64Utils.encodedStr(username + ":" + userpwd);
        getRtspUrl(this);
        initViewpager();
    }

    @Override
    protected void initView() {
        btn_full_screen.setOnClickListener(this);
        btn_play_video.setOnClickListener(this);
        btn_video_back.setOnClickListener(this);
        sp = getSharedPreferences("isFirst", MODE_PRIVATE);
    }

    private void checkUserNameAndPwd() {
        Intent intent = getIntent();
        boolean isFirst = intent.getBooleanExtra("isFirst", false);
        position = intent.getIntExtra("position", 0);
        if (intent.getStringExtra("username") != null
                && intent.getStringExtra("userpwd") != null
                && (!Objects.equals(intent.getStringExtra("username"), sp.getString("name", ""))
                || !Objects.equals(intent.getStringExtra("userpwd"), sp.getString("pwd", "")))) {

            CameraAdapter.isFirst.put(position, true);
        }
        if (isFirst) {
            Log.d("TAG", "用户首次登录");
            username = intent.getStringExtra("username");
            userpwd = intent.getStringExtra("userpwd");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", username);
            editor.putString("pwd", userpwd);
            CameraAdapter.isFirst.put(position, false);
            editor.apply();
        } else {
            Log.d("TAG", "用户不是首次登录");
            username = sp.getString("name", "");
            userpwd = sp.getString("pwd", "");
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 100) {
                btn_full_screen.setVisibility(View.GONE);
                btn_play_video.setVisibility(View.GONE);
                btn_video_back.setVisibility(View.GONE);
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer(String url) {
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        nodePlayer = new NodePlayer(this);
        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer.setInputUrl(url);
        nodePlayer.setVideoEnable(true);
        nodePlayer.setBufferTime(0);
        pb_progressbar.setVisibility(View.GONE);
        nodePlayer.start();

        nodePlayerView.setOnTouchListener(this);
    }

    private void initViewpager() {
        List<Fragment> fragmentList = new ArrayList<>();
        String[] mTabList = new String[] {"版本信息", "NTP校时", "网络设置", "GB28181配置", "视频编码参数", "串口配置", "用户管理"};

        for (String s : mTabList) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        fragmentList.add(new DeviceInfoFragment(basic, position));
        fragmentList.add(new NTPTimeFragment(basic, position));
        fragmentList.add(new NetworkFragment(basic, position));
        fragmentList.add(new GBSettingsFragment(basic, position));
        fragmentList.add(new AVCodecFragment(basic, position));
        fragmentList.add(new SerialPortFragment(basic, position));
        fragmentList.add(new UserManagerFragment(basic, position));

        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmentList, mTabList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int param1Int) {}

            public void onPageScrolled(int param1Int1, float param1Float, int param1Int2) {}

            public void onPageSelected(int param1Int) {}
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getRtspUrl(final RetrofitCallback callback) {
        RetrofitPool.getInstance().getRetrofit(position)
                .create(Api.class)
                .getRtspUrl("Basic " + this.basic)
                .enqueue(new Callback<ResponseBody>() {
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        try {
                            String url = SplitUtils.getValue(SplitUtils.getStringArray(response.body()), "root.RTSP.url");
                            if (callback != null)
                                callback.onSuccess(url);

                        } catch (Exception e) {
                            if (callback != null)
                                callback.onError(e);
                        }
                    }

                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        if (callback != null)
                            callback.onError(t);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_video:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group.setLayoutParams(params);
                } else {
                    ISFULLSCREEN = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    rl_group.setLayoutParams(params);
                }
                break;
            case R.id.btn_video_back:
                if (ISFULLSCREEN) {
                    ISFULLSCREEN = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    rl_group.setLayoutParams(params);
                } else {
                    finish();
                }
                break;
            case R.id.btn_play_video:
                if (ISPLAYING) {
                    if (nodePlayer != null) {
                        nodePlayer.pause();
                        ISPLAYING = false;
                        btn_play_video.setBackgroundResource(R.drawable.icon_play);
                    }
                } else {
                    if (nodePlayer != null) {
                        nodePlayer.start();
                        ISPLAYING = true;
                        btn_play_video.setBackgroundResource(R.drawable.icon_topause);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && ISFULLSCREEN) {
            ISFULLSCREEN = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 220.0F));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            rl_group.setLayoutParams(params);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            btn_video_back.setVisibility(View.VISIBLE);
            btn_play_video.setVisibility(View.VISIBLE);
            btn_full_screen.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(100, 3000L);
        }
        return true;
    }

    @Override
    public void onSuccess(String url) {
        initPlayer(url);
    }

    @Override
    public void onError(Throwable t) {
        Log.e("TAG", "onError: " + t.getMessage());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nodePlayer != null && nodePlayer.isPlaying())
            nodePlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nodePlayer.stop();
        nodePlayer.release();
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
}
