package com.example.onvifipc.contract.settings;

import com.example.onvifipc.base.IBaseView;
import com.example.onvifipc.bean.Device;

import java.util.List;

public class Contract {

    public interface ISettingsModel {

        void getDeviceList();
    }

    public interface ISettingsPresenter {

        void findDevices();
    }

    public interface ISettingsView extends IBaseView {

        void onDataSuccess(List<Device> deviceList);
    }
}
