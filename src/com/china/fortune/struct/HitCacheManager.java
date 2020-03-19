package com.china.fortune.struct;

import com.china.fortune.global.Log;

public class HitCacheManager {
    static final private int ciMaxCache = 8;

    private HitCache[] lsCache = new HitCache[ciMaxCache];
    private int iCache = 0;

    public int init(FastList<String> lsData) {
        int iRes = 0;
        iCache = 0;
        FastList<String> lsClone= lsData.clone();
        while (iCache < ciMaxCache) {
            HitCache hc = new HitCache(lsClone, iCache+1);
            for (int i = 0; i < lsClone.size(); i++) {
                String sData = lsClone.get(i);
                if (sData != null) {
                    if (hc.find(sData) >= 0) {
                        lsClone.set(i, null);
                    }
                }
            }

            lsCache[iCache++] = hc;
            if (lsClone.size() == lsClone.countNull()) {
                iRes = iCache;
                break;
            }
        }

        if (iRes == 0) {
            for (int i = 0; i < lsClone.size(); i++) {
                String s = lsClone.get(i);
                if (s != null) {
                    Log.logClassError("Can't Hash " + s);
                }
            }

        }
        return iRes;
    }

    public void showDetail(FastList<String> lsData) {
        for (int i = 0; i < iCache; i++) {
            lsCache[i].showDetail(lsData);
        }
    }

    public void showUsage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iCache; i++) {
            sb.append('[');
            sb.append(i);
            sb.append(':');
            sb.append(lsCache[i].calUsage());
            sb.append("] ");
        }
        sb.setLength(sb.length() - 1);
        Log.logClass(sb.toString());
    }

    public int find(String s) {
        if (iCache > 0) {
            int iFound = lsCache[0].find(s);
            if (iFound < 0) {
                for (int i = 1; i < iCache; i++) {
                    iFound = lsCache[i].find(s);
                    if (iFound >= 0) {
                        if (lsCache[0].checkHashCode(iFound, s)) {
                            return iFound;
                        } else {
                            return -1;
                        }
                    }
                }
            } else {
                return iFound;
            }
        }
        return -1;
    }
}
