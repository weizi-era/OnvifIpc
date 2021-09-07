package com.example.onvifipc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.bean.Params;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CameraParamsAdapter extends RecyclerView.Adapter<CameraParamsAdapter.ViewHolder> {

    private List<Params> paramsList;

    public CameraParamsAdapter(List<Params> paramsList) {
        this.paramsList = paramsList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cameraparams_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Params params = paramsList.get(position);
        holder.key.setText(params.getParamsName());
        holder.value.setText(params.getParamsValue());
    }

    @Override
    public int getItemCount() {
        return paramsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView key;
        TextView value;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            key = view.findViewById(R.id.key);
            value = view.findViewById(R.id.value);
        }
    }
}
