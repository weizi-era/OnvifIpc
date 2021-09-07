package com.example.onvifipc.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.bean.User;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tv_userId.setText(user.getId() + "");
        holder.tv_userName.setText(user.getUserName());
        holder.tv_LocalPermissions.setText(user.getLocalPermissions() + "");
        holder.tv_RemotePermissions.setText(user.getRemotePermissions() + "");
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_item;
        private TextView tv_userId;
        private TextView tv_userName;
        private TextView tv_LocalPermissions;
        private TextView tv_RemotePermissions;
        private LinearLayout slide;
        private RelativeLayout slide_itemView;
        private TextView tv_update;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            slide_itemView = itemView.findViewById(R.id.slide_itemView);
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_userId = itemView.findViewById(R.id.userId);
            tv_userName = itemView.findViewById(R.id.userName);
            tv_LocalPermissions = itemView.findViewById(R.id.LocalPermissions);
            tv_RemotePermissions = itemView.findViewById(R.id.RemotePermissions);
            slide = itemView.findViewById(R.id.slide);
            tv_update = itemView.findViewById(R.id.tv_update);
        }
    }
}
