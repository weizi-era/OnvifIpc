package com.example.onvifipc.utils;

import android.util.Log;

public class CRC16Util {

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum|(b[0] & 0xff) << 24;
        accum = accum|(b[1] & 0xff) << 16;
        accum = accum|(b[2] & 0xff) << 8;
        accum = accum|(b[3] & 0xff) << 0;
        return Float.intBitsToFloat(accum);
    }

    public static String getCRC(byte[] bytes) {

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length - 2; i++) {
            CRC ^= (int) bytes[i];
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) == 1) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换
        CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        return Integer.toHexString(CRC);
    }

    public static float getFinalData(byte[] msg) {
        String s2 = ByteUtil.bytes2HexStr(msg);
        Log.d("TaskCenter", "原始16进制字符串: " + s2);
        String topFour = s2.substring(10, 14);
        String lastFour = s2.substring(6, 10);
        String dataHexString = topFour + lastFour;
        Log.d("TaskCenter", "数据16进制字符串: " + dataHexString);
        byte[] bytes = ByteUtil.hexStr2bytes(dataHexString);
        return getFloat(bytes);
    }
}
