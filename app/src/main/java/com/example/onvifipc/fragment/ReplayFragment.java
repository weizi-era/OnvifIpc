package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.adapter.HistoryVideoAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.HistoryVideo;
import com.example.onvifipc.contract.replay.Contract;
import com.example.onvifipc.presenter.ReplayPresenter;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.example.spinner_lib.MaterialSpinner;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class ReplayFragment extends BaseFragment implements Contract.IReplayView, View.OnClickListener, MaterialSpinner.OnItemSelectedListener, View.OnTouchListener {

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

    private final int color = Color.parseColor("#4169E1");
    private long startTimeStamp = 0L;
    private long endTimeStamp = 0L;
    private final String basic = Base64Utils.encodedStr("admin" + ":" + "Rock@688051");
    private Map<String, Long> map;
    private final String[] cameraList = new String[] {"前置摄像头", "后置摄像头"};
    private final String[] videoTypeList = new String[] {"定时录像"};
    private String ip;

    ReplayPresenter replayPresenter;

    @Override
    protected void initView() {

        map = new HashMap<>();
        replayPresenter = new ReplayPresenter(this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
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

        initPlayer();

        initSpinner();
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

    private void initPlayer() {
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleToFill);

        nodePlayer = new NodePlayer(getContext());
        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);
        nodePlayer.setVideoEnable(true);
        nodePlayer.setBufferTime(0);

        nodePlayerView.setOnTouchListener(this);
    }

    private void initDatePicker(TextView textView) {

        new CardDatePickerDialog.Builder(getContext())
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

    @Override
    protected void onLazyLoad() {


    }

    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_replay;
    }

    @Override
    public void onDataSuccess(List<HistoryVideo> historyVideoList) {

        if (historyVideoList != null && historyVideoList.size() > 0) {
            iv_novideo.setVisibility(View.GONE);
            HistoryVideoAdapter adapter = new HistoryVideoAdapter(historyVideoList);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new HistoryVideoAdapter.OnItemClickListener() {
                @Override
                public void onButtonClicked(View view, int position) {
                    adapter.setDefSelect(position);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    HistoryVideo historyVideo = historyVideoList.get(position);
                    try {
                        Date start = format.parse(historyVideo.getStartTime());
                        Date end = format.parse(historyVideo.getEndTime());
                        String property = historyVideo.getProperty();
                        startPlay(ip, String.valueOf(start.getTime() / 1000), String.valueOf(end.getTime() / 1000), property);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            iv_novideo.setVisibility(View.VISIBLE);
        }
    }

    private void startPlay(String ip, String startTime, String endTime, String property) {
        iv_prepause.setVisibility(View.GONE);
        String rtspUrl = "rtsp://" + ip + ":554" + "/1/0/0/start=" + startTime + "&end=" + endTime  + "&property=" + property;
        Log.d("startPlay", "initPlayer: " + rtspUrl);
        nodePlayer.setInputUrl(rtspUrl);
        pb_progressbar.setVisibility(View.GONE);
        nodePlayer.start();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_startTime:
                initDatePicker(startTime);
                break;
            case R.id.iv_endTime:
                initDatePicker(endTime);
                break;
            case R.id.btnQuery:
                getHistoryVideo();
                break;
        }
    }

    private void getHistoryVideo() {

        if (startTime.getText().toString().isEmpty() || endTime.getText().toString().isEmpty()) {
            ToastUtils.showToast(getContext(), "开始时间和结束时间不能为空");
            return;
        }
        if (startTimeStamp > endTimeStamp) {
            ToastUtils.showToast(getContext(), "开始时间不能大于结束时间");
            return;
        }

        if (endTimeStamp - startTimeStamp > 3600) {
            ToastUtils.showToast(getContext(), "间隔不能大于1小时");
            return;
        }

        map.put("beginTime", startTimeStamp);
        map.put("endTime", endTimeStamp);

        btn_play_video.setVisibility(View.GONE);

        replayPresenter.getHistoryVideo("http://" + ip, "Basic " + basic, map);
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

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull @NotNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    btn_full_screen.setVisibility(View.GONE);
                    btn_play_video.setVisibility(View.GONE);
                    btn_video_back.setVisibility(View.GONE);
                    break;
            }
        }
    };

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
