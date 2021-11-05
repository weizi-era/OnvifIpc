package com.example.onvifipc.presenter;

import android.util.Log;

import com.example.onvifipc.Common;
import com.example.onvifipc.contract.preview.Contract;
import com.example.onvifipc.model.PreviewModel;
import com.example.onvifipc.tcpclient.TaskCenter;
import com.example.onvifipc.tcpclient.TaskCenterCom;
import com.example.onvifipc.utils.ByteUtil;
import com.example.onvifipc.utils.CRC16Util;

import java.util.List;

public class PreviewPresenter implements Contract.IPreviewPresenter {

    private Contract.IPreviewModel mModel;
    private Contract.IPreviewView mView;

    public PreviewPresenter(Contract.IPreviewView iView) {
        this.mModel = new PreviewModel();
        this.mView = iView;
    }

    @Override
    public void getHardwareData(int tag) {
        synchronized (this) {
            byte[] msg = new byte[8];
            msg[0] = (byte)0x01;
            msg[1] = (byte)0x03;
            msg[2] = (byte)0x00;
            if (tag == Common.CODE) {
                msg[3] = (byte)0xBE;
                msg[4] = (byte)0x00;
                msg[5] = (byte)0x02;
            } else if (tag == Common.PARAMS) {
                msg[3] = (byte)0x00;
                msg[4] = (byte)0x00;
                msg[5] = (byte)0x18;
            }

            String crc = CRC16Util.calcCrc16(msg, msg.length - 2);
            msg[6] = (byte) Integer.parseInt(crc.substring(0, 2), 16);
            msg[7] = (byte) Integer.parseInt(crc.substring(2, 4), 16);


            Log.d("TaskCenter", "发送字节: " + ByteUtil.bytes2HexStr(msg));

            TaskCenter.getInstance().send(msg, tag);

            TaskCenter.getInstance().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
                @Override
                public void callback(String msg) {
                    if (tag == Common.CODE) {
                        List<String> finalData = CRC16Util.getMNData(msg);
                        mView.onSuccess(tag, finalData);
                    } else if (tag == Common.PARAMS) {
                        List<Float> floatList = CRC16Util.getFinalData(msg);
                        mView.onParamsSuccess(tag, floatList);
                    }
                }
            });
        }
    }

    @Override
    public void getComState() {

        mView.showComState1(TaskCenter.getInstance().isConnected());

        TaskCenterCom.getInstance().setReceivedCallback(new TaskCenterCom.OnReceiveCallbackBlock() {

            @Override
            public void callbackComData(byte[] bs) {
                String result = ByteUtil.bytes2HexStr(bs);
                Log.d("串口2接收：", "callback: " + result);
                mView.showComState2(result);
            }
        });
    }

}
