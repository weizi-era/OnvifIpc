package com.example.onvifipc.presenter;

import android.util.Log;

import com.example.onvifipc.contract.preview.Contract;
import com.example.onvifipc.fragment.PreviewFragment;
import com.example.onvifipc.model.PreviewModel;
import com.example.onvifipc.tcpclient.TaskCenter;
import com.example.onvifipc.tcpclient.TaskCenterCom;
import com.example.onvifipc.utils.ByteUtil;
import com.example.onvifipc.utils.CRC16Util;

public class PreviewPresenter implements Contract.IPreviewPresenter {

    private Contract.IPreviewModel mModel;
    private Contract.IPreviewView mView;

    public PreviewPresenter(Contract.IPreviewView iView) {
        this.mModel = new PreviewModel();
        this.mView = iView;
    }

    @Override
    public synchronized void getHardwareData(int registerAddr, int tag) {

        //mView.onDataLoading();
        synchronized (this) {
            byte[] msg = new byte[8];
            msg[0] = (byte)0x01;
            msg[1] = (byte)0x03;
            msg[2] = (byte)0x00;
            msg[3] = (byte)registerAddr;
            msg[4] = (byte)0x00;
            if (tag == PreviewFragment.MNTOPFOUR || tag == PreviewFragment.MNLASTFOUR) {
                msg[5] = (byte)0x01;
            } else {
                msg[5] = (byte)0x02;
            }

            Log.d("TAG", "校验之前的msg: " + ByteUtil.bytes2HexStr(msg));
            String crc = CRC16Util.calcCrc16(msg, msg.length - 2);
            Log.d("TAG", "CRC校验: " + crc);
            msg[6] = (byte) Integer.parseInt(crc.substring(0, 2), 16);
            msg[7] = (byte) Integer.parseInt(crc.substring(2, 4), 16);

            String s = ByteUtil.bytes2HexStr(msg);
            Log.d("TaskCenter", "发送消息: " + s);

            TaskCenter.getInstance().send(msg);

            TaskCenter.getInstance().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
                @Override
                public void callback(byte[] msg) {
                    if (tag == PreviewFragment.MNTOPFOUR || tag == PreviewFragment.MNLASTFOUR) {
                        long finalData = CRC16Util.getMNData(msg);
                        String result = String.format("%04d",finalData);
                        mView.onDataSuccess(tag, result);
                    } else {
                        float finalData = CRC16Util.getFinalData(msg);
                        if (tag == PreviewFragment.LASTBAT) {
                            finalData = finalData * 100;
                        }
                        String result = String.format("%.2f", finalData);
                        mView.onDataSuccess(tag, result);
                    }
                }

            });

            try {
                Thread.sleep(1000);  // 每次发送消息后睡眠300ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getComState() {
        TaskCenterCom.getInstance().setReceivedCallback(new TaskCenterCom.OnReceiveCallbackBlock() {

            @Override
            public void callbackComData(byte[] bs) {
                Log.d("串口2接收", "callbackComData: " + bs);
                String result = ByteUtil.bytes2HexStr(bs);
                Log.d("串口2接收：", "callback: " + result);
                mView.showComState2(result);
            }
        });
    }

}
