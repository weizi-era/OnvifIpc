package com.example.onvifipc.model;

import com.example.onvifipc.Api;
import com.example.onvifipc.adapter.UserAdapter;
import com.example.onvifipc.base.BaseModel;
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.bean.User;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagerModel extends BaseModel<List<User>> {

    private List<User> userList;

    public UserManagerModel(String basic, int position, IBaseModelListener<List<User>> mListener) {
        super(basic, position, mListener);
        userList = new ArrayList<>();
    }

    public void load() {
        doNetRequest().getUserInfo("Basic " + basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String[] stringArray = SplitUtils.getStringArray(response.body());
                    if (stringArray != null && stringArray.length > 0) {
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

                        mListener.onLoadSuccess(userList);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                mListener.onLoadFailure(t.getMessage());
            }
        });
    }
}
