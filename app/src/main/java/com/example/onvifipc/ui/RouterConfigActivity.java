package com.example.onvifipc.ui;


import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseActivity;

import butterknife.BindView;

public class RouterConfigActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void initView() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        webView.loadUrl("http://192.168.1.1");
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_router;
    }

    @Override
    public void reConnect() {

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
