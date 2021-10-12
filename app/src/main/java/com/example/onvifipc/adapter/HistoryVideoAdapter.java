package com.example.onvifipc.adapter;

import android.annotation.SuppressLint;
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
    private Handler handler;

    private int mSelectedPos = -1;


    public HistoryVideoAdapter(List<HistoryVideo> historyVideoList, Handler handler) {
        this.historyVideoList = historyVideoList;
        this.handler = handler;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_video_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        HistoryVideo historyVideo = historyVideoList.get(position);
        holder.id.setText(historyVideo.getId() + "");
        holder.startTime.setText(historyVideo.getStartTime());
        holder.endTime.setText(historyVideo.getEndTime());
        holder.videoSize.setText(historyVideo.getVideoSize());

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.btnPlay.setBackgroundResource(R.drawable.button_bg);
                SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Message message = Message.obtain();
                    Date start = format.parse(historyVideo.getStartTime());
                    Date end = format.parse(historyVideo.getEndTime());
                    Bundle bundle = new Bundle();
                    bundle.putString("startTime", String.valueOf(start.getTime() / 1000));
                    bundle.putString("endTime", String.valueOf(end.getTime() / 1000));
                    bundle.putString("property", historyVideo.getProperty());
                    message.what = 100;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateData(List<HistoryVideo> historyVideoList){
        this.historyVideoList = historyVideoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return historyVideoList == null ? 0 : historyVideoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView startTime;
        TextView endTime;
        TextView videoSize;
        Button btnPlay;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            videoSize = itemView.findViewById(R.id.videoSize);
            btnPlay = itemView.findViewById(R.id.play);
        }
    }
}
