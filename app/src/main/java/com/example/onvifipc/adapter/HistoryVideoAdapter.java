package com.example.onvifipc.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onvifipc.R;
import com.example.onvifipc.bean.HistoryVideo;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class HistoryVideoAdapter extends RecyclerView.Adapter<HistoryVideoAdapter.ViewHolder> {
    private List<HistoryVideo> historyVideoList;
    private int defItem = -1;//默认值

    public HistoryVideoAdapter(List<HistoryVideo> historyVideoList) {
        this.historyVideoList = historyVideoList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_video_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        HistoryVideo historyVideo = historyVideoList.get(position);
        holder.id.setText(historyVideo.getId() + "");
        holder.startTime.setText(historyVideo.getStartTime());
        holder.endTime.setText(historyVideo.getEndTime());
        holder.videoSize.setText(historyVideo.getVideoSize());
        if (defItem != -1) {
            if (defItem == position) {
                holder.btnPlay.setBackgroundResource(R.drawable.button_bg);
            } else {
                holder.btnPlay.setBackgroundResource(R.drawable.spinner_bg);
            }
        }
    }

    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

//    public void updateData(List<HistoryVideo> historyVideoList){
//        this.historyVideoList = historyVideoList;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return historyVideoList == null ? 0 : historyVideoList.size();
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onButtonClicked(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView startTime;
        TextView endTime;
        TextView videoSize;
        Button btnPlay;

        public ViewHolder(@NonNull @NotNull View itemView, final OnItemClickListener onClickListener) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            videoSize = itemView.findViewById(R.id.videoSize);
            btnPlay = itemView.findViewById(R.id.play);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onClickListener.onButtonClicked(v, position);
                        }
                    }
                }
            });
        }
    }
}
