package com.china.fortune.common;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;

public class FloatssAndByte {
    static public byte[] toBytes(float[][] lsFloat) {
        int iLen = lsFloat[0].length;
        byte[] bData = new byte[iLen * 4];
        for (int i = 0; i < iLen; i++) {
            int f = Float.floatToIntBits(lsFloat[0][i]);
            Log.logClass(lsFloat[0][i] + " " + f);
            bData[i*4] = (byte)(f & 0xff);
            f >>= 8;
            bData[i*4 + 1] = (byte)(f & 0xff);
            f >>= 8;
            bData[i*4 + 2] = (byte)(f & 0xff);
            f >>= 8;
            bData[i*4 + 3] = (byte)(f & 0xff);
        }
        return bData;
    }

    static public float[][] toFloat(byte[] bData) {
        int iLen = bData.length / 4;
        float[][] lsFloat = new float[1][iLen];

        for (int i = 0; i < iLen; i++) {
            int f = bData[i * 4 + 3]& 0xff;;
            f <<= 8;
            f += bData[i * 4 + 2]& 0xff;;
            f <<= 8;
            f += bData[i * 4 + 1]& 0xff;;
            f <<= 8;
            f += bData[i * 4]& 0xff;;
            lsFloat[0][i] = Float.intBitsToFloat(f);

            Log.logClass(lsFloat[0][i] + " " + f);
        }
        return lsFloat;
    }

    static public void testFloatByte() {
        int f = 1065353216;
        byte[] bData = new byte[512];
        int i = 0;
        Log.logClass("" + f);
        bData[i*4] = (byte)(f & 0xff);
        f >>= 8;
        Log.logClass("" + f);
        bData[i*4 + 1] = (byte)(f & 0xff);
        f >>= 8;
        Log.logClass("" + f);
        bData[i*4 + 2] = (byte)(f & 0xff);
        f >>= 8;
        Log.logClass("" + f);
        bData[i*4 + 3] = (byte)(f & 0xff);

        f = bData[i * 4 + 3]& 0xff;;
        Log.logClass("" + f);
        f <<= 8;
        f += bData[i * 4 + 2]& 0xff;;
        Log.logClass("" + f);
        f <<= 8;
        f += bData[i * 4 + 1]& 0xff;;
        Log.logClass("" + f);
        f <<= 8;
        f += bData[i * 4]& 0xff;;
        Log.logClass("" + f);

    }
    public static void main(String[] args) {

//        testFloatByte();


//        float[][] lsFace = new float[1][512];
//        for (int i = 0; i < 512; i++) {
//            lsFace[0][i] = i;
//        }
//        for (int i = 0; i < 10; i++) {
//            Log.log(i + " " + lsFace[0][i]);
//        }
//        byte[] bData = toBytes(lsFace);
//        lsFace = toFloat(bData);
//        for (int i = 0; i < 10; i++) {
//            Log.log(i + " " + lsFace[0][i]);
//        }

        byte[] bData = FileUtils.readSmallFile("z:\\0029dd26e63245ed8963f48451a13fd2.v8");
        float[][] lsFace = toFloat(bData);
        for (int i = 0; i < 512; i++) {
            Log.logNoDate("" + lsFace[0][i]);
        }
    }

}
