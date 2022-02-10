package com.example.onvifipc.ui.activity;


import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.onvifipc.R;
import com.example.onvifipc.base.BaseActivity;
import com.just.agentweb.AgentWeb;

import butterknife.BindView;

public class RouterConfigActivity extends BaseActivity {

    @BindView(R.id.web_content)
    LinearLayout web_content;
    @BindView(R.id.webTitle)
    TextView webTitle;

    private AgentWeb mAgentWeb;

    @Override
    protected void initView() {
        webTitle.setText("路由器配置");
    }

    @Override
    protected void initData() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(web_content, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("http://192.168.1.1");
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
