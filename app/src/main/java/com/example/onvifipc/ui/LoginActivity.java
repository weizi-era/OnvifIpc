package com.example.onvifipc.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.CameraAdapter;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.utils.Base64Utils;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;
import com.kingja.loadsir.core.LoadSir;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.Map;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressLint("NonConstantResourceId")
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.et_account)
    EditText account;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.btLogin)
    TextView btLogin;
    @BindView(R.id.btTouristLogin)
    TextView btTouristLogin;
    @BindView(R.id.pwd_state)
    ImageView pwd_state;

    private boolean isCheck = false;



    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setData() {
        initView();

        final RxPermissions rxPermissions = new RxPermissions(this);

//        String ip = "http://192.168.1.160:8000/";
//        String splitip1 = ip.substring(7, ip.indexOf(":8"));
//        String splitIp = ip.subSequence(7, 20).toString();
//        Log.d("TAG", "setData: " + splitip1);

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        ToastUtils.showToast(LoginActivity.this, "您同意了所有权限");
                    } else {
                        ToastUtils.showToast(LoginActivity.this, "您拒绝了以下权限");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                login();
                break;
            case R.id.btTouristLogin:
                startActivity(new Intent(LoginActivity.this, RealVideoActivity.class));
                finish();
                break;
            case R.id.pwd_state:
                if (isCheck) {
                    isCheck = false;
                    pwd_state.setImageResource(R.mipmap.pwd_hide);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    isCheck = true;
                    pwd_state.setImageResource(R.mipmap.pwd_show);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                password.setSelection(password.getText().toString().length());
                break;
        }
    }

    private void initView() {
        btLogin.setOnClickListener(this);
        btTouristLogin.setOnClickListener(this);
        pwd_state.setOnClickListener(this);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


    private void login() {
        if (account.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
            ToastUtils.showToast(this, "登录成功！");
            startActivity(new Intent(LoginActivity.this, ManagerMainActivity.class));
            finish();
        } else {
            ToastUtils.showToast(this, "账号或密码错误");
        }
    }

}
