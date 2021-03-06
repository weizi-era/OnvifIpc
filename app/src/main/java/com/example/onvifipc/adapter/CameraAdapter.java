package com.example.onvifipc.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Api;
import com.example.onvifipc.ui.activity.CameraInfoActivity;
import com.example.onvifipc.view.LoginDialog;
import com.example.onvifipc.R;
import com.example.onvifipc.bean.Device;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    private final List<Device> devices;
    private final Context context;
    private final List<Boolean> flagLists;
    private int errorCount = 1;

    public static Map<Integer, String> deviceIP;
    public static Map<Integer, Boolean> isFirst;

    public CameraAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
        isFirst = new HashMap<>();
        deviceIP = new HashMap<>();
        flagLists = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.camera_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        for (int i = 0; i < devices.size(); i++) {
            flagLists.add(false);
        }
        int adapterPosition = holder.getAdapterPosition();
        Device device = devices.get(adapterPosition);
        deviceIP.put(adapterPosition, SplitUtils.getDeviceIP(device));
        isFirst.put(adapterPosition, true);
        Log.d("TAG", "onBindViewHolder: " + isFirst);
        holder.cameraLogo.setImageResource(R.mipmap.camera_icon);
        holder.cameraUrl.setText(device.getServiceUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: " + deviceIP);
                if (!flagLists.get(adapterPosition)) {
                    LoginDialog mLoginDialog = new LoginDialog(context);
                    WindowManager.LayoutParams lp = mLoginDialog.getWindow().getAttributes();
                    lp.dimAmount = 0.8f;
                    mLoginDialog.getWindow().setAttributes(lp);
                    mLoginDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    mLoginDialog.setSureListener(new LoginDialog.sureListener() {
                        @Override
                        public void sure(String userName, String userPwd) {
                            if (userName != null && userPwd != null) {
                                login(holder, adapterPosition, userName, userPwd);
                                mLoginDialog.dismiss();
                            } else {
                                ToastUtils.showToast(context, "??????????????????????????????");
                            }
                        }
                    });
                    mLoginDialog.setCloseListener(new LoginDialog.closeListener() {
                        @Override
                        public void close() {
                            mLoginDialog.dismiss();
                        }
                    });
                    mLoginDialog.show();
                } else {
                    Intent intent = new Intent(context, CameraInfoActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("isFirst", isFirst.get(position));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView cameraLogo;
        private final TextView cameraUrl;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            cameraLogo = itemView.findViewById(R.id.cameraLogo);
            cameraUrl = itemView.findViewById(R.id.cameraUrl);
        }
    }

    public void login(ViewHolder holder, int position, String name, String pwd) {
        Log.d("TAG", "login: " + position);
        String basic = Base64Utils.encodedStr(name + ":" + pwd);
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.login("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String value = SplitUtils.getValue(stringArray, "root.ERR.des");
                    Log.d("TAG", "onResponse: " + value);
                    if (value != null && value.equals("OK")) {
                        holder.cameraLogo.setImageResource(R.mipmap.camera_icon_logined);
                        flagLists.set(position, true);
                        ToastUtils.showToast(context, "???????????????");
                        Intent intent = new Intent(context, CameraInfoActivity.class);
                        intent.putExtra("isFirst", isFirst.get(position));
                        intent.putExtra("value", value);
                        intent.putExtra("position", position);
                        intent.putExtra("username", name);
                        intent.putExtra("userpwd", pwd);
                        context.startActivity(intent);
                    } else {
                        ToastUtils.showToast(context, "????????????????????? " + errorCount + " ??????" + ",??????5???????????????30??????");
                        errorCount ++;
                        holder.cameraLogo.setImageResource(R.drawable.camera);
                        flagLists.set(position, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

}
