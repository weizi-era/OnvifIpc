package com.example.onvifipc.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseActivity;
import com.example.onvifipc.utils.ToastUtils;
import com.permissionx.guolindev.PermissionX;

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setData() {
        initView();
        PermissionX.init(this)
                .permissions(Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(deniedList, "需要您同意以下授权才能正常使用","同意","拒绝"))
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        ToastUtils.showToast(LoginActivity.this, "您同意了所有权限");
                    } else {
                        ToastUtils.showToast(LoginActivity.this, "您拒绝了以下权限" + deniedList);
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
        }
    }

    private void initView() {
        btLogin.setOnClickListener(this);
        btTouristLogin.setOnClickListener(this);
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
