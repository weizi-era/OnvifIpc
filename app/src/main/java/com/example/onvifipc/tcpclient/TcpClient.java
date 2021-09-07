//package com.example.onvifipc.tcpclient;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.InetAddress;
//import java.net.Socket;
//
//public class TcpClient {
//    private static TcpClient instance;
//    private Socket mSocket;
//    String TAG_log = "Socket";
//
//    private OutputStream mOutputStream;
//    private InputStream mInputStream;
//    private SocketThread mSocketThread;
//    private boolean isStop = false;//thread flag
//
//
//
//    private TcpClient() {}
//
//    public static TcpClient getInstance() {
//        synchronized (TcpClient.class) {
//            if (instance == null) {
//                instance = new TcpClient();
//            }
//            return instance;
//        }
//    }
//
//    private class SocketThread extends Thread {
//
//        private final String ip;
//        private final int port;
//        public SocketThread(String ip, int port){
//            this.ip = ip;
//            this.port = port;
//        }
//
//        @Override
//        public void run() {
//
//            Log.d(TAG_log,"SocketThread start ");
//            super.run();
//
//            try {
//                if (mSocket != null) {
//                    mSocket.close();
//                    mSocket = null;
//                }
//                InetAddress ipAddress = InetAddress.getByName(ip);
//                mSocket = new Socket(ipAddress, port);
//
//                //设置不延时发送
//                mSocket.setTcpNoDelay(true);
//                //设置输入输出缓冲流大小
//                mSocket.setSendBufferSize(8*1024);
//                mSocket.setReceiveBufferSize(8*1024);
//
//                if (isConnect()) {
//                    mOutputStream = mSocket.getOutputStream();
//                    mInputStream = mSocket.getInputStream();
//
//                    isStop = false;
//
//                    uiHandler.sendEmptyMessage(1);
//                } else {
//                    uiHandler.sendEmptyMessage(-1);
//                    Log.e(TAG_log,"SocketThread connect fail");
//                    return;
//                }
//
//            } catch (Exception e) {
//                uiHandler.sendEmptyMessage(-1);
//                Log.e(TAG_log,"SocketThread connect io exception = " + e.getMessage());
//                e.printStackTrace();
//            }
//            Log.d(TAG_log,"SocketThread connect over ");
//
//            //read....
//            while (isConnect() && !isStop && !isInterrupted()) {
//
//                int size;
//                try {
//                    byte[] buffer = new byte[1024];
//                    if (mInputStream == null) return;
//                    size = mInputStream.read(buffer);//null data -1 , zrd serial rule size default 10
//                    if (size > 0) {
//                        Message msg = new Message();
//                        msg.what = 100;
//                        Bundle bundle = new Bundle();
//                        bundle.putByteArray("data",buffer);
//                        bundle.putInt("size",size);
//                        bundle.putInt("requestCode",requestCode);
//                        msg.setData(bundle);
//                        uiHandler.sendMessage(msg);
//                    }
//                    Log.i(TAG_log, "SocketThread read listening");
//                    //Thread.sleep(100);//log eof
//                }
//                catch (IOException e) {
//                    uiHandler.sendEmptyMessage(-1);
//                    Log.e(TAG_log,"SocketThread read io exception = "+e.getMessage());
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        }
//    }
//
//    //==============================socket connect============================
//    /**
//     * connect socket in thread
//     * Exception : android.os.NetworkOnMainThreadException
//     * */
//    public void connect(String ip, int port){
//        mSocketThread = new SocketThread(ip, port);
//        mSocketThread.start();
//    }
//
//    /**
//     * socket is connect
//     * */
//    public boolean isConnect(){
//        boolean flag = false;
//        if (mSocket != null) {
//            flag = mSocket.isConnected();
//        }
//        return flag;
//    }
//
//    /**
//     * socket disconnect
//     * */
//    public void disconnect() {
//        isStop = true;
//        try {
//            if (mOutputStream != null) {
//                mOutputStream.close();
//            }
//
//            if (mInputStream != null) {
//                mInputStream.close();
//            }
//
//            if (mSocket != null) {
//                mSocket.close();
//                mSocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (mSocketThread != null) {
//            mSocketThread.interrupt();//not intime destory thread,so need a flag
//        }
//    }
//
//    /**
//     * send byte[] cmd
//     * Exception : android.os.NetworkOnMainThreadException
//     * */
//    public void sendByteCmd(final byte[] mBuffer,int requestCode) {
//        this.requestCode = requestCode;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (mOutputStream != null) {
//                        mOutputStream.write(mBuffer);
//                        mOutputStream.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }
//
//    /**
//     * send string cmd to serial
//     */
//    public void sendStrCmds(String cmd, int requestCode) {
//        byte[] mBuffer = cmd.getBytes();
//        sendByteCmd(mBuffer,requestCode);
//    }
//
//
//    /**
//     * send prt content cmd to serial
//     */
//    public void sendChsPrtCmds(String content, int requestCode) {
//        try {
//            byte[] mBuffer = content.getBytes("GB2312");
//            sendByteCmd(mBuffer,requestCode);
//        }
//        catch (UnsupportedEncodingException e1){
//            e1.printStackTrace();
//        }
//    }
//
//    @SuppressLint("HandlerLeak")
//    Handler uiHandler = new Handler() {
//        @Override
//        public void handleMessage(@NotNull Message msg) {
//            super.handleMessage(msg);
//            switch(msg.what){
//                //connect error
//                case -1:
//                    if (null != onDataReceiveListener) {
//                        onDataReceiveListener.onConnectFail();
//                        disconnect();
//                    }
//                    break;
//
//                //connect success
//                case 1:
//                    if (null != onDataReceiveListener) {
//                        onDataReceiveListener.onConnectSuccess();
//                    }
//                    break;
//
//                //receive data
//                case 100:
//                    Bundle bundle = msg.getData();
//                    byte[] buffer = bundle.getByteArray("data");
//                    int size = bundle.getInt("size");
//                    int mequestCode = bundle.getInt("requestCode");
//                    if (null != onDataReceiveListener) {
//                        onDataReceiveListener.onDataReceive(buffer, size, mequestCode);
//                    }
//                    break;
//            }
//        }
//    };
//
//    /**
//     * socket response data listener
//     * */
//    private OnDataReceiveListener onDataReceiveListener = null;
//    private int requestCode = -1;
//    public interface OnDataReceiveListener {
//        void onConnectSuccess();
//        void onConnectFail();
//        void onDataReceive(byte[] buffer, int size, int requestCode);
//    }
//    public void setOnDataReceiveListener(
//            OnDataReceiveListener dataReceiveListener) {
//        onDataReceiveListener = dataReceiveListener;
//    }
//
//}
