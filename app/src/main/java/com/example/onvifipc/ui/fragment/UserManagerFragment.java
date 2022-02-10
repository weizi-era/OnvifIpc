package com.example.onvifipc.ui.fragment;

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
import com.example.onvifipc.base.IBaseModelListener;
import com.example.onvifipc.bean.User;
import com.example.onvifipc.model.UserManagerModel;
import com.example.onvifipc.utils.RetrofitPool;
import com.example.onvifipc.utils.SplitUtils;
import com.example.onvifipc.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class UserManagerFragment extends BaseFragment implements IBaseModelListener<List<User>> {
    @BindView(R.id.user_recyclerView)
    RecyclerView recyclerView;
    private final String basic;
    private final int position;
    private UserManagerModel mUserManagerModel;

    public UserManagerFragment(String basic, int position) {
        this.basic = basic;
        this.position = position;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onLazyLoad() {
        mUserManagerModel = new UserManagerModel(basic, position, this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        mUserManagerModel.load();
    }


    @Override
    protected int onCreateFragmentView() {
        return R.layout.fragment_user;
    }

    @Override
    public void onLoadSuccess(List<User> userList) {
        UserAdapter adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadFailure(String message) {
        ToastUtils.showToast(getContext(), message);
    }

    @Override
    public void onUpdateSuccess(String response) {

    }
}
