package com.example.onvifipc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.HistoryVideoAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.bean.HistoryVideo;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.DensityUtil;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.onvifipc.view.MyVideoView;
import com.example.spinner_lib.MaterialSpinner;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressLint("NonConstantResourceId")
public class ReplayActivity extends BaseActivity implements MaterialSpinner.OnItemSelectedListener, View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.cameraSpin)
    MaterialSpinner cameraSpin;
    @BindView(R.id.videoTypeSpin)
    MaterialSpinner videoTypeSpin;
    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.iv_startTime)
    ImageView iv_startTime;
    @BindView(R.id.iv_endTime)
    ImageView iv_endTime;
    @BindView(R.id.btnQuery)
    Button btnQuery;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.iv_novideo)
    ImageView iv_novideo;
    @BindView(R.id.iv_prepause)
    ImageView iv_prepause;
    @BindView(R.id.pb_progressbar)
    ProgressBar pb_progressbar;
    @BindView(R.id.video_view)
    NodePlayerView nodePlayerView;
    @BindView(R.id.btn_full_video)
    Button btn_full_screen;
    @BindView(R.id.btn_play_video)
    Button btn_play_video;
    @BindView(R.id.btn_video_back)
    Button btn_video_back;

    private NodePlayer nodePlayer;

    private List<HistoryVideo> historyVideoList;
    private final int color = Color.parseColor("#4169E1");
    private long startTimeStamp = 0L;
    private long endTimeStamp = 0L;
    private final String basic = Base64Utils.encodedStr("admin" + ":" + "Rock@688051");
    private Map<String, Long> map;
    private final String[] cameraList = new String[] {"前置摄像头", "后置摄像头"};
    private final String[] videoTypeList = new String[] {"定时录像"};
    private String ip;

    @Override
    protected void setData() {

        initView();

        initSpinner();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    iv_prepause.setVisibility(View.GONE);
                    pb_progressbar.setVisibility(View.VISIBLE);
                    Bundle bundle = msg.getData();
                    String startTime = bundle.getString("startTime");
                    String endTime = bundle.getString("endTime");
                    String property = bundle.getString("property");
                    initPlayer(ip, startTime, endTime, property);
                    break;
                case 200:
                    btn_full_screen.setVisibility(View.GONE);
                    btn_play_video.setVisibility(View.GONE);
                    btn_video_back.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void initPlayer(String ip, String startTime, String endTime, String property) {
        String rtspUrl = "rtsp://" + ip + ":554" + "/1/0/0/start=" + startTime + "&end=" + endTime  + "&property=" + property;
        Log.d("TAG", "initPlayer: " + rtspUrl);

        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        nodePlayer = new NodePlayer(this);
        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer.setInputUrl(rtspUrl);
        nodePlayer.setVideoEnable(true);
        nodePlayer.setBufferTime(0);
        pb_progressbar.setVisibility(View.GONE);
        nodePlayer.start();

        nodePlayerView.setOnTouchListener(this);
    }

    private void initSpinner() {
        cameraSpin.setItems(cameraList);
        cameraSpin.setSelectedIndex(0);
        ip = "192.168.1.160";
        cameraSpin.setBackgroundResource(R.drawable.spinner_bg);

        videoTypeSpin.setItems(videoTypeList);
        videoTypeSpin.setSelectedIndex(0);
        videoTypeSpin.setBackgroundResource(R.drawable.spinner_bg);
    }

    private void initDatePicker(TextView textView) {

        new CardDatePickerDialog.Builder(this)
                .setTitle("日期选择")
                .showBackNow(true)
                .showFocusDateInfo(true)
                .showDateLabel(true)
                .setThemeColor(color)
                .setBackGroundModel(CardDatePickerDialog.STACK)
                .setWrapSelectorWheel(false)
                .setLabelText("年","月","日","时","分", "秒")
                .setOnChoose("选择", new Function1<Long, Unit>() {
                    @Override
                    public Unit invoke(Long aLong) {
                        if (textView.getId() == R.id.startTime) {
                            startTimeStamp = aLong / 1000;
                        } else {
                            endTimeStamp = aLong / 1000;
                        }
                        textView.setText(SplitUtils.conversionTime(aLong));
                        return null;
                    }
                })
                .setOnCancel("关闭", new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        return null;
                    }
                }).build().show();
    }

    private void initView() {
        map = new HashMap<>();
        historyVideoList = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = (DefaultItemAnimator) recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setSupportsChangeAnimations(false);
        }
        cameraSpin.setOnItemSelectedListener(this);
        videoTypeSpin.setOnItemSelectedListener(this);
        iv_startTime.setOnClickListener(this);
        iv_endTime.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btn_full_screen.setOnClickListener(this);
        btn_play_video.setOnClickListener(this);
        btn_video_back.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_replay;
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.cameraSpin:
                cameraSpin.setSelectedIndex(position);
                if (cameraList[position].equals("前置摄像头")) {
                    ip = "192.168.1.160";
                } else {
                    ip = "192.168.1.161";
                }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_startTime:
                initDatePicker(startTime);
                break;
            case R.id.iv_endTime:
                initDatePicker(endTime);
                break;
            case R.id.btnQuery:
                getHistoryVideoInfo(ip);
                break;
            case R.id.btn_video_back:
                    finish();
                break;
        }
    }

    private void getHistoryVideoInfo(String ip) {

        if (startTimeStamp > endTimeStamp) {
            ToastUtils.showToast(this, "开始时间不能大于结束时间");
            return;
        }

        if (endTimeStamp - startTimeStamp > 3600) {
            ToastUtils.showToast(this, "间隔不能大于1小时");
            return;
        }

        map.put("beginTime", startTimeStamp);
        map.put("endTime", endTimeStamp);
        new Retrofit.Builder().baseUrl("http://" + ip).client(new OkHttpClient()).build().create(Api.class)
                .getHistoryVideoInfo("Basic " + basic, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String totalCount = SplitUtils.getValue(stringArray, "root.RECORD.totalCount");
                    Log.d("TAG", "onResponse: " + totalCount);
                    int totalCounts = Integer.parseInt(totalCount);
                    historyVideoList.clear();
                    for (int i = 0; i < totalCounts; i++) {
                        String beginTime = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".beginTime");
                        String endTime = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".endTime");
                        String size = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i + ".size");
                        String property = SplitUtils.getValue(stringArray, "root.RECORD.ITEM" + i +".property");
                        String convertBeginTime = SplitUtils.conversionTime(Long.parseLong(beginTime) * 1000);
                        String convertEndTime = SplitUtils.conversionTime(Long.parseLong(endTime) * 1000);
                        String byteToMb = SplitUtils.getNetFileSizeDescription(Long.parseLong(size));
                        historyVideoList.add(new HistoryVideo(i + 1, convertBeginTime, convertEndTime, byteToMb, property, false));
                    }

                    HistoryVideoAdapter adapter = new HistoryVideoAdapter(historyVideoList, handler);
                    if (historyVideoList != null && historyVideoList.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        iv_novideo.setVisibility(View.GONE);
                        adapter.updateData(historyVideoList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        iv_novideo.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
        Log.d("TAG", "getHistoryVideoInfo: " + startTimeStamp);
        Log.d("TAG", "getHistoryVideoInfo: " + endTimeStamp);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.video_view:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_video_back.setVisibility(View.VISIBLE);
                    btn_play_video.setVisibility(View.VISIBLE);
                    btn_full_screen.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(200, 3000L);
                }
                break;
        }

        return true;
    }
}