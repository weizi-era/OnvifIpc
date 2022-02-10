package com.example.onvifipc.tcpclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.blankj.utilcode.util.ArrayUtils;
import com.example.onvifipc.Common;
import com.example.onvifipc.utils.ByteUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;

/**
 * TCP 硬件参数任务类
 */
public class TaskCenter {
    private static TaskCenter instance;
    private static final String TAG = "TaskCenter";
    //    Socket
    private Socket socket;
    //    IP地址
    private String ipAddress;
    //    端口号
    private int port;

    private Thread thread;

    //    Socket输出流
    private DataOutputStream dos;
    private OutputStream os;
    //    Socket输入流
    private InputStream is;
    private DataInputStream dis;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    //    连接回调
    private OnServerConnectedCallbackBlock connectedCallback;
    //    断开连接回调(连接失败)
    private OnServerDisconnectedCallbackBlock disconnectedCallback;
    //    接收信息回调
    private OnReceiveCallbackBlock receivedCallback;

    //    构造函数私有化
    private TaskCenter() {
        super();
    }
    //    提供一个全局的静态方法
    public static TaskCenter getInstance() {
        if (instance == null) {
            synchronized (TaskCenter.class) {
                if (instance == null) {
                    instance = new TaskCenter();
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
              //      socket.setSoTimeout(5000);//设置超时时间
                    socket.setTcpNoDelay(true);
                    socket.setKeepAlive(true);
                    if (isConnected()) {
                        TaskCenter.getInstance().ipAddress = ipAddress;
                        TaskCenter.getInstance().port = port;
                        if (connectedCallback != null) {
                            connectedCallback.callback();
                        }

                        bos = new BufferedOutputStream(socket.getOutputStream());
                        bis = new BufferedInputStream(socket.getInputStream());
                        os = socket.getOutputStream();
                        is = socket.getInputStream();

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
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
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
     * 接收数据
     */
    public void receive(int tag) {

        synchronized (this) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //   while (isConnected()) {
                    try {
                        byte[] buf = new byte[1024];

                        StringBuilder builder = new StringBuilder();
                        int len = 0;

                        len = is.read(buf);

                        if (len != -1) {
                            byte[] temp = new byte[len];
                            System.arraycopy(buf, 0, temp, 0, len);

                            builder.append(ByteUtil.bytes2HexStr(temp));
                        }

                        Log.d(TAG, "读取的字节===: " + builder.toString());

                        if (len > 0) {
                            if (tag == Common.CODE && len == 9) {
                                if (receivedCallback != null) {
                                    receivedCallback.callback(builder.toString());
                                }
                            } else if (tag == Common.PARAMS && len == 53 && builder.toString().startsWith("010330")) {
                                if (receivedCallback != null) {
                                    receivedCallback.callback(builder.toString());
                                }
                            }
                        }

                        Log.i(TAG,"接收Modbus数据成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //    }
                }
            }).start();
        }
    }

    /**
     * 发送数据
     *
     * @param data  数据
     */
    public void send(final byte[] data, int tag) {
        synchronized (this) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (socket != null) {
                        try {
                            os.write(data);
                            os.flush();
                            Log.i(TAG,"发送成功");

                            Thread.sleep(2000);

                            receive(tag);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG,"发送失败");
                        }
                    } else {
                        connect();
                    }
                }
            }).start();
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
        void callback(String msg);
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