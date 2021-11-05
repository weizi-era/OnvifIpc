package com.example.onvifipc;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {

    //登录
    @GET("/cgi-bin/login.cgi?action=list&group=LOGIN")
    Call<ResponseBody> login(@Header("Authorization") String Authorization);

    //获取RTSP地址
    @GET("/cgi-bin/video.cgi?action=list&group=RTSP&RTSP.streamNo=0")
    Call<ResponseBody> getRtspUrl(@Header("Authorization") String Authorization);

    //获取设备信息
    @GET("/cgi-bin/devInfo.cgi?action=list&group=SYSINFO")
    Call<ResponseBody> getDeviceInfo(@Header("Authorization") String Authorization);

    //NTP校时
    @GET("/cgi-bin/param.cgi?action=list&group=NTP")
    Call<ResponseBody> getNTPInfo(@Header("Authorization") String Authorization);
    @GET("/cgi-bin/param.cgi?action=update&group=NTP")
    Call<ResponseBody> updateNTPInfo(@Header("Authorization") String Authorization,
                                     @Query("NTP.ntpSvr") String address,
                                     @Query("NTP.interval") String interval);

    //网络设置
    @GET("/cgi-bin/param.cgi?action=list&group=ETH")
    Call<ResponseBody> getNetworkInfo(@Header("Authorization") String Authorization);
    @GET("/cgi-bin/param.cgi?action=update&group=ETH")
    Call<ResponseBody> updateNetworkInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, String> map);

    //GB28181配置
    @GET("/cgi-bin/param.cgi?action=list&group=GB28181")
    Call<ResponseBody> getGbInfo(@Header("Authorization") String Authorization);
    @GET("/cgi-bin/param.cgi?action=update&group=GB28181")
    Call<ResponseBody> updateGbInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, String> map);

    //视频编码参数
    @GET("/cgi-bin/param.cgi?action=list&group=VENC&channel=0")
    Call<ResponseBody> getCodecInfo(@Header("Authorization") String Authorization, @Query("streamType") int type);
    @GET("/cgi-bin/param.cgi?action=update&group=VENC&&channel=0")
    Call<ResponseBody> updateCodecInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, Integer> map);

    //串口配置
    @GET("/cgi-bin/param.cgi?action=list&group=SERIAL")
    Call<ResponseBody> getSerialInfo(@Header("Authorization") String Authorization);
    @GET("/cgi-bin/param.cgi?action=update&group=SERIAL")
    Call<ResponseBody> updateSerialInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, Integer> map);

    //用户管理
    @GET("/cgi-bin/param.cgi?action=list&group=USER")
    Call<ResponseBody> getUserInfo(@Header("Authorization") String Authorization);
    @GET("/cgi-bin/param.cgi?action=update&group=USER")
    Call<ResponseBody> updateUserInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, String> map);

    //历史视频查询
    @GET("/cgi-bin/record.cgi?action=list&group=RECORD&channel=0&type=1")
    Call<ResponseBody> getHistoryVideoInfo(@Header("Authorization") String Authorization, @QueryMap Map<String, Long> map);

    //定时录像
    @GET("/cgi-bin/param.cgi?action=update&group=RECORD&channel=0")
    Call<ResponseBody> stopTimeRecord(@Header("Authorization") String Authorization, @QueryMap Map<String, Integer> map);
}
