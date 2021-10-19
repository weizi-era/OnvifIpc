package com.example.onvifipc.presenter;

import android.content.Context;

import com.example.onvifipc.bean.Device;
import com.example.onvifipc.contract.settings.Contract;
import com.example.onvifipc.model.SettingsModel;
import com.example.onvifipc.onvif.FindDevicesThread;
import com.example.onvifipc.utils.OnvifSdk;

import java.util.ArrayList;

public class SettingsPresenter implements Contract.ISettingsPresenter {

    private Contract.ISettingsView mSettingsView;
    private Contract.ISettingsModel mSettingsModel;
    private Context context;

    public SettingsPresenter(Contract.ISettingsView settingsView, Context context) {
        mSettingsModel = new SettingsModel();
        this.mSettingsView = settingsView;
        this.context = context;
    }

    @Override
    public void findDevices() {

        OnvifSdk.findDevice(context, "192.168.1.255", new FindDevicesThread.FindDevicesListener() {
            @Override
            public void searchResult(ArrayList<Device> devices) {
                if (devices != null && devices.size() > 0) {
                    
                    mSettingsView.onDataSuccess(devices);
                }
            }
        });
    }
}
