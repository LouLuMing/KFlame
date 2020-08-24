package com.china.fortune.struct;

import com.china.fortune.global.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class ReuseArrayListBK {
    private int iSize = (1 << 16) - 1;
    private transient Object[] lsObj = null;
    private AtomicInteger aiIndex = new AtomicInteger(0);

    public ReuseArrayListBK() {
        lsObj = new Object[iSize + 1];
    }

    public int set(Object o) {
        for (int j = 0; j <= iSize; j++) {
            int i = aiIndex.getAndIncrement() & iSize;
            if (lsObj[i] == null) {
                lsObj[i] = o;
                return i;
            }
        }
        return -1;
    }

    public boolean set(int i, Object o) {
        if (lsObj[i] == null) {
            lsObj[i] = o;
            return true;
        } else {
            return false;
        }
    }

    public Object get(int i) {
        return lsObj[i];
    }

    public Object free(int i) {
        Object o = lsObj[i];
        lsObj[i] = null;
        return o;
    }

    public static void main(String[] args) {
        int i = 0xfff;
        int j = (1 << 16) -1;
        Log.log(i + ":" + j);
    }

}
