package com.example.onvifipc.bean;

public class Params {

    private String paramsName;
    private String paramsValue;

    public Params(String paramsName, String paramsValue) {
        this.paramsName = paramsName;
        this.paramsValue = paramsValue;
    }

    public String getParamsName() {
        return paramsName;
    }

    public void setParamsName(String paramsName) {
        this.paramsName = paramsName;
    }

    public String getParamsValue() {
        return paramsValue;
    }

    public void setParamsValue(String paramsValue) {
        this.paramsValue = paramsValue;
    }

    @Override
    public String toString() {
        return "Params{" +
                "paramsName='" + paramsName + '\'' +
                ", paramsValue='" + paramsValue + '\'' +
                '}';
    }
}
