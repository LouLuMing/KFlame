package com.china.fortune.struct;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;

public class HitCache {
    private int[] lsHashHit = null;
    private int iSpan = 1;
    private int[] lsHashCode = null;

    public HitCache(FastList<String> lsData, int span) {
        iSpan = span;
        int iData = lsData.size();
        lsHashHit = new int[iData * 12];
        for (int i = 0; i < lsHashHit.length; i++) {
            lsHashHit[i] = -1;
        }
        lsHashCode = new int[lsData.size()];
        for (int i = 0; i < lsData.size(); i++) {
            lsHashCode[i] = StringUtils.calHashCode(lsData.get(i), span);
        }
        for (int i = 0; i < lsData.size(); i++) {
            String sData = lsData.get(i);
            if (sData != null) {
                int iCache1 = StringUtils.calHashCode(sData, iSpan) % lsHashHit.length;
                if (lsHashHit[iCache1] == -1) {
                    lsHashHit[iCache1] = i;
                } else {
                    lsHashHit[iCache1] = -2;
                }
            }
        }
    }

    public int calHashCode(String s) {
        if (s != null) {
            return StringUtils.calHashCode(s, iSpan) % lsHashHit.length;
        } else {
            return -1;
        }
    }

    public boolean checkHashCode(int index, String s) {
        int iHashCode = StringUtils.calHashCode(s, iSpan);
        return lsHashCode[index] == iHashCode;
    }

    public int getCapacity() {
        return lsHashHit.length;
    }

    public int getUsage() {
        int iUsage = 0;
        for (int i : lsHashHit) {
            if (i >= 0) {
                iUsage++;
            }
        }
        return iUsage;
    }

    public int find(String s) {
        if (s != null) {
            int iHashCode = StringUtils.calHashCode(s, iSpan);
            int iCache1 = iHashCode % lsHashHit.length;
            int index = lsHashHit[iCache1];
            if (index >= 0 && lsHashCode[index] == iHashCode) {
                return index;
            }
        }
        return -1;
    }

    public void showDetail(FastList<String> lsData) {
        int iCount = 0;
        for (int i : lsHashHit) {
            if (i >= 0) {
                iCount++;
                Log.log(iSpan + ":" + lsData.get(i));
            }
        }
        if (iCount == 0) {
            Log.log(iSpan + " no hit");
        }
    }

}
