package com.example.onvifipc.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.onvifipc.R;


public class LoginDialog extends Dialog implements View.OnClickListener {

    private TextView cancel;
    private TextView confirm;
    private EditText userName;
    private EditText userPwd;

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

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
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
