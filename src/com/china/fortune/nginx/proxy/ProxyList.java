package com.china.fortune.nginx.proxy;

import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.struct.FastList;

import java.util.concurrent.atomic.AtomicInteger;

public class ProxyList {
    public String sResource;
    public FastList<Proxy> lsHost = new FastList();
    private AtomicInteger iIndex = new AtomicInteger(0);
    public ProxyList(String s) {
        sResource = s;
    }

    public Proxy get() {
        int iStart = iIndex.getAndIncrement() & 0xffff;
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get((iStart + i) % lsHost.size());
            if (ph.isActive()) {
                return ph;
            }
        }
        return null;
    }

    HttpRequest hr = new HttpRequest();
    public void add(String sPath) {
        Proxy ph = new Proxy();
        ph.sPath = sPath;
        if (sPath.startsWith("http")
                || sPath.startsWith("ws")) {
            hr.parseURL(sPath);
            ph.sServer = hr.getServerIP();
            ph.iPort = hr.getServerPort();
        }
        lsHost.add(ph);
    }

    public ProxyList clone() {
        ProxyList phl = new ProxyList(sResource);
        for (int i = 0; i < lsHost.size(); i++) {
            phl.lsHost.add(lsHost.get(i));
        }
        return phl;
    }

    public void del(String path) {
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get(i);
            if (path.equals(ph.sPath)) {
                lsHost.remove(i);
                iIndex.set(0);
                break;
            }
        }
    }

    public boolean isMatch(String resource) {
        return resource.startsWith(sResource);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("resource", sResource);
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get(i);
            jarr.put(ph.sPath);
        }
        json.put("proxy", jarr);
        return json;
    }
}
