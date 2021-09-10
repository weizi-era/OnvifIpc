//package com.example.onvifipc.tcpclient;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//
//public class TcpWork extends Thread {
//
//    private Context context;
//    private Handler handler;
//
//    public TcpWork(Context context, Handler handler) {
//        this.context = context;
//        this.handler = handler;
//    }
//
//    @Override
//    public void run() {
//        initDataReceiver();
//        if (TcpClient.getInstance().isConnect()) {
////            byte[] data = et_send.getText().toString().getBytes();
////            TcpClient.getInstance().sendByteCmd(data, 1001);
//        } else {
//          //  ToastUtils.showToast(context, "尚未连接");
//        }
//    }
//
//    /**
//     * socket data receive
//     * */
//    private void initDataReceiver() {
//        TcpClient.getInstance().setOnDataReceiveListener(dataReceiveListener);
//    }
//
//    private final TcpClient.OnDataReceiveListener dataReceiveListener = new TcpClient.OnDataReceiveListener() {
//        @Override
//        public void onConnectSuccess() {
//            Log.d("TcpWork", "onConnectSuccess: 已连接");
//            Message message = Message.obtain();
//            message.what = 10;
//            handler.sendMessage(message);
//        }
//
//        @Override
//        public void onConnectFail() {
//            Log.d("TcpWork", "onConnectSuccess: 连接失败");
//            Message message = Message.obtain();
//            message.what = 11;
//            handler.sendMessage(message);
//        }
//
//        @Override
//        public void onDataReceive(byte[] buffer, int size, int requestCode) {
//            //获取有效长度的数据
//            byte[] data = new byte[size];
//            System.arraycopy(buffer, 0, data, 0, size);
//
//            //final String oxValue = Arrays.toString(HexUtil.Bytetox(data));
//        }
//    };
//}
