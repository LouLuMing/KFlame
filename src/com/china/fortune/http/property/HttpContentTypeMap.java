package com.china.fortune.http.property;

import com.china.fortune.struct.FastList;
import com.china.fortune.struct.HitCacheManager;

public class HttpContentTypeMap {
    private FastList<String> lsTag = new FastList<String>();
    private FastList<String> lsType = new FastList<>();
    private FastList<Boolean> lsGZip = new FastList<>();
    private HitCacheManager hcm = new HitCacheManager();

    public HttpContentTypeMap () {
        put("css", "text/css");
        put("html", "text/html");
        put("txt", "text/plain");
        put("xml", "text/xml");

        put("png", "image/png", false);
        put("gif", "image/gif", false);
        put("ico", "image/x-icon", false);
        put("jpg", "image/jpeg", false);

        put("js", "application/x-javascript");
        put("json", "application/json");
        put("form", "application/x-www-form-urlencoded");

        put("pdf", "application/pdf");

        initHitCache();
    }

    public int initHitCache() {
        return hcm.init(lsTag);
    }

    public void put(String sKey, String sType) {
        if (sKey != null && sType != null) {
            if (!lsTag.contains(sKey)) {
                lsTag.add(sKey);
                lsType.add(sType);
                lsGZip.add(true);
            }
        }
    }

    public void put(String sKey, String sType, boolean bGZip) {
        if (sKey != null && sType != null) {
            if (!lsTag.contains(sKey)) {
                lsTag.add(sKey);
                lsType.add(sType);
                lsGZip.add(bGZip);
            }
        }
    }

    public int find(String sTag) {
        return hcm.find(sTag);
    }

    public String getType(int i) {
        if (i >= 0) {
            return lsType.get(i);
        }
        return "application/octet-stream";
    }

    public String getType(String sTag) {
        if (sTag != null) {
            int i = hcm.find(sTag);
            if (i >= 0) {
                return lsType.get(i);
            }
        }
        return "application/octet-stream";
    }

    public boolean getGZip(int i) {
        if (i >= 0) {
            return lsGZip.get(i);
        } else {
            return true;
        }
    }

}

