package com.example.onvifipc.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.Api;
import com.example.onvifipc.R;
import com.example.onvifipc.adapter.UserAdapter;
import com.example.onvifipc.base.BaseFragment;
import com.example.onvifipc.bean.User;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class UserManagerFragment extends BaseFragment {
    @BindView(R.id.user_recyclerView)
    RecyclerView recyclerView;
    private List<User> userList;
    private final String basic;
    private final int position;

    public UserManagerFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onLazyLoad() {
        userList = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        getUserInfo();

       // new Handler(Looper.getMainLooper()).postDelayed(() -> mBaseLoadService.showSuccess(), 1000);
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_user;
    }

    private void getUserInfo() {
        Api api = RetrofitPool.getInstance().getRetrofit(position).create(Api.class);
        api.getUserInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    String sCount = SplitUtils.getValue(stringArray, "root.USER.userCount");
                    int userCount = Integer.parseInt(sCount);
                    for (int i = 0; i < userCount; i++) {
                        String userName = SplitUtils.getValue(stringArray, "root.USER.U" + i + ".name");
                        String localRight = SplitUtils.getValue(stringArray, "root.USER.U" + i + ".localRight");
                        String remoteRight = SplitUtils.getValue(stringArray, "root.USER.U" + i + ".remoteRight");
                        int userLocalRight = Integer.parseInt(localRight);
                        int userRemoteRight = Integer.parseInt(remoteRight);
                        userList.add(new User(i + 1, userName, userLocalRight, userRemoteRight));
                    }
                    UserAdapter adapter = new UserAdapter(userList);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

            }
        });
    }

    private void updateUserInfo() {
        //todo  更新用户信息（账号 密码  权限）
    }



}
