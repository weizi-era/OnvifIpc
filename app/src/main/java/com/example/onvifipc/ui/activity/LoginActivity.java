package com.example.onvifipc.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.utils.ToastUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;


import butterknife.BindView;

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
    @BindView(R.id.loading)
    ProgressBar progressBar;
    @BindView(R.id.loadingText)
    TextView loadingText;

    private boolean isCheck = false;

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_login;
    }

    @Override
    public void reConnect() {

    }

    @Override
    protected void initData() {

        final RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
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
                progressBar.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                startActivity(new Intent(LoginActivity.this, TouristActivity.class));
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

    @Override
    protected void onStop() {
        super.onStop();
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        btLogin.setOnClickListener(this);
        btTouristLogin.setOnClickListener(this);
        pwd_state.setOnClickListener(this);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void login() {
        if (account.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            ToastUtils.showToast(this, "请输入账号或密码");
            return;
        }
        if (account.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            ToastUtils.showToast(this, "登录成功！");
            finish();
        } else {
            ToastUtils.showToast(this, "账号或密码错误");
        }
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
