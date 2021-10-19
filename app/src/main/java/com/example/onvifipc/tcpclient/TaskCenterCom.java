package com.example.onvifipc.tcpclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.onvifipc.utils.ByteUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by shensky on 2018/1/15.
 */

public class TaskCenterCom {
    private static TaskCenterCom instance;
    private static final String TAG = "TaskCenterCom";
    //    Socket
    private Socket socket;
    //    IP地址
    private String ipAddress;
    //    端口号
    private int port;

    private Thread thread;

    //    Socket输入流
    private DataInputStream dis;
    //    连接回调
    private OnServerConnectedCallbackBlock connectedCallback;
    //    断开连接回调(连接失败)
    private OnServerDisconnectedCallbackBlock disconnectedCallback;
    //    接收信息回调
    private OnReceiveCallbackBlock receivedCallback;

    //    构造函数私有化
    private TaskCenterCom() {
        super();
    }
    //    提供一个全局的静态方法
    public static TaskCenterCom getInstance() {
        if (instance == null) {
            synchronized (TaskCenterCom.class) {
                if (instance == null) {
                    instance = new TaskCenterCom();
                }
            }
        }
        return instance;
    }


    /**
     * 通过IP地址(域名)和端口进行连接
     *
     * @param ipAddress  IP地址(域名)
     * @param port       端口
     */
    public void connect(final String ipAddress, final int port) {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipAddress, port);
                    socket.setSoTimeout(2000);//设置超时时间
                    socket.setTcpNoDelay(true);
                    socket.setKeepAlive(true);
                    if (isConnected()) {
                        TaskCenterCom.getInstance().ipAddress = ipAddress;
                        TaskCenterCom.getInstance().port = port;
                        if (connectedCallback != null) {
                            connectedCallback.callback();
                        }

                        dis = new DataInputStream(socket.getInputStream());

                        receiveComData();

                        Log.i(TAG,"连接成功");
                    } else {
                        Log.i(TAG,"连接失败");
                        if (disconnectedCallback != null) {
                            disconnectedCallback.callback(new IOException("连接失败"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnect();
                    Log.e(TAG,"连接异常");
                    if (disconnectedCallback != null) {
                        disconnectedCallback.callback(e);
                    }
                }
            }
        });

        thread.start();
    }
    /**
     * 判断是否连接
     */
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }
    /**
     * 连接
     */
    public void connect() {
        connect(ipAddress, port);
    }
    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnected()) {
            try {
                socket.close();
                if (socket.isClosed()) {
                    if (disconnectedCallback != null) {
                        disconnectedCallback.callback(new IOException("断开连接"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        removeCallback();
    }

    /**
     * 接收串口数据
     */
    public void receiveComData() {
        while (isConnected()) {
            try {
                /**得到的是16进制数，需要进行解析*/
                byte[] bt = new byte[2];
                Log.d(TAG, "receiveComData1111: " + ByteUtil.bytes2HexStr(bt));
//                获取接收到的字节和字节数
                int length = dis.read(bt);
//                获取正确的字节
                byte[] bs = new byte[length];
                Log.d(TAG, "receiveComData2222: " + ByteUtil.bytes2HexStr(bs));

                System.arraycopy(bt, 0, bs, 0, length);

                if (receivedCallback != null) {
                    receivedCallback.callbackComData(bs);
                }
                Log.i(TAG,"接收成功");
            } catch (IOException e) {
                Log.i(TAG,"接收失败");
            }
        }
    }

    /**
     * 回调声明
     */
    public interface OnServerConnectedCallbackBlock {
        void callback();
    }
    public interface OnServerDisconnectedCallbackBlock {
        void callback(IOException e);
    }
    public interface OnReceiveCallbackBlock {

        void callbackComData(byte[] bs);
    }

    public void setConnectedCallback(OnServerConnectedCallbackBlock connectedCallback) {
        this.connectedCallback = connectedCallback;
    }

    public void setDisconnectedCallback(OnServerDisconnectedCallbackBlock disconnectedCallback) {
        this.disconnectedCallback = disconnectedCallback;
    }

    public void setReceivedCallback(OnReceiveCallbackBlock receivedCallback) {
        this.receivedCallback = receivedCallback;
    }
    /**
     * 移除回调
     */
    private void removeCallback() {
        connectedCallback = null;
        disconnectedCallback = null;
        receivedCallback = null;
    }
}