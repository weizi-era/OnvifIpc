package com.example.onvifipc.view;

import android.app.Dialog;
import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.onvifipc.R;


public class LoginDialog extends Dialog implements View.OnClickListener {

    private TextView cancel;
    private TextView confirm;
    private EditText userName;
    private EditText userPwd;
    private ImageView pwd_state;
    private boolean isCheck = false;

    private closeListener closeListener;
    private sureListener sureListener;

    public LoginDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.login_dialog);
        initView();

    }

    private void initView() {
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        userName = findViewById(R.id.et_userName);
        userPwd = findViewById(R.id.et_userPwd);
        pwd_state = findViewById(R.id.pwd_state);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        pwd_state.setOnClickListener(this);
        userPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                closeListener.close();
                break;
            case R.id.confirm:
                sureListener.sure(userName.getText().toString(), userPwd.getText().toString());
                break;
            case R.id.pwd_state:
                if (isCheck) {
                    isCheck = false;
                    pwd_state.setImageResource(R.mipmap.pwd_hide);
                    userPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    isCheck = true;
                    pwd_state.setImageResource(R.mipmap.pwd_show);
                    userPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                userPwd.setSelection(userPwd.getText().toString().length());
                break;

        }
    }

    public void setCloseListener(closeListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setSureListener(sureListener sureListener) {
        this.sureListener = sureListener;
    }

    public interface closeListener {
        void close();
    }

    public interface sureListener {
        void sure(String userName, String userPwd);
    }

}
