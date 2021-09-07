package com.example.onvifipc.bean;

public class HistoryVideo extends SelectedBean {

    private int id;
    private String startTime;
    private String endTime;
    private String videoSize;
    private String property;

    public HistoryVideo(int id, String startTime, String endTime, String videoSize, String property, boolean isSelected) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.videoSize = videoSize;
        this.property = property;
        setSelected(isSelected);
    }

    public int getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getProperty() {
        return property;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getVideoSize() {
        return videoSize;
    }

    @Override
    public String toString() {
        return "HistoryVideo{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", videoSize='" + videoSize + '\'' +
                ", property='" + property + '\'' +
                '}';
    }
}
