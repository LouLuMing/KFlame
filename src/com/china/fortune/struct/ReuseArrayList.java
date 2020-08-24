package com.china.fortune.struct;

import com.china.fortune.global.Log;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ReuseArrayList {
    private int iSize = (1 << 16) - 1;
    private AtomicReferenceArray<Object> lsObj = new AtomicReferenceArray<>(iSize + 1);
    private AtomicInteger aiIndex = new AtomicInteger(0);

    public int set(Object o) {
        for (int j = 0; j <= iSize; j++) {
            int i = aiIndex.getAndIncrement() & iSize;
            if (lsObj.compareAndSet(i, null, o)) {
                return i;
            }
        }
        Log.logClassError("no space");
        return -1;
    }

    public boolean set(int i, Object o) {
        return lsObj.compareAndSet(i, null, o);
    }

    public Object get(int i) {
        return lsObj.get(i);
    }

    public Object free(int i) {
        return lsObj.getAndSet(i, null);
    }

    public void clear() {
        for (int j = 0; j <= iSize; j++) {
            lsObj.set(j, null);
        }
    }
    public static void main(String[] args) {
        int i = 0xfff;
        int j = (1 << 16) -1;
        Log.log(i + ":" + j);
    }

}
