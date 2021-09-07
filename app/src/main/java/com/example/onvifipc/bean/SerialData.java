package com.example.onvifipc.bean;

public class SerialData {

    private int SerialId;
    private int workMode;
    private String baudRate;
    private String dataBit;
    private String stopBit;
    private int flowType;
    private int parityType;

    public SerialData(int serialId, int workMode, String baudRate, String dataBit, String stopBit, int flowType, int parityType) {
        SerialId = serialId;
        this.workMode = workMode;
        this.baudRate = baudRate;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.flowType = flowType;
        this.parityType = parityType;
    }

    public int getSerialId() {
        return SerialId;
    }

    public void setSerialId(int serialId) {
        SerialId = serialId;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public String getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(String baudRate) {
        this.baudRate = baudRate;
    }

    public String getDataBit() {
        return dataBit;
    }

    public void setDataBit(String dataBit) {
        this.dataBit = dataBit;
    }

    public String getStopBit() {
        return stopBit;
    }

    public void setStopBit(String stopBit) {
        this.stopBit = stopBit;
    }

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
    }

    public int getParityType() {
        return parityType;
    }

    public void setParityType(int parityType) {
        this.parityType = parityType;
    }

    @Override
    public String toString() {
        return "SerialData{" +
                "SerialId=" + SerialId +
                ", workMode=" + workMode +
                ", baudRate=" + baudRate +
                ", dataBit=" + dataBit +
                ", stopBit=" + stopBit +
                ", flowType=" + flowType +
                ", parityType=" + parityType +
                '}';
    }
}
