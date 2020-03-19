package com.china.fortune.nginx.proxy;

import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.struct.FastList;

import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHostList {
    public FastList<ProxyHost> lsHost = new FastList();
    private AtomicInteger iIndex = new AtomicInteger(0);
    public ProxyHost get() {
        long lNow = System.currentTimeMillis();
        int iStart = iIndex.getAndIncrement() & 0xffff;
        for (int i = 0; i < lsHost.size(); i++) {
            ProxyHost ph = lsHost.get((iStart + i) % lsHost.size());
            if (ph.isActive(lNow)) {
                return ph;
            }
        }
        return null;
    }

    public void add(String sPath, long lTimeout) {
        HttpRequest hr = new HttpRequest();
        hr.parseURL(sPath);
        ProxyHost ph = new ProxyHost();
        ph.sPath = sPath;
        ph.httpRequest = hr;
        ph.lLiveTicket = lTimeout;
        lsHost.add(ph);
    }

    public void updatePath(String path) {
        for (int i = 0; i < lsHost.size(); i++) {
            ProxyHost ph = lsHost.get(i);
            if (path.equals(ph.sPath)) {
                ph.active();
            }
        }
    }

    public ProxyHostList clone() {
        ProxyHostList phl = new ProxyHostList();
        for (int i = 0; i < lsHost.size(); i++) {
            phl.lsHost.add(lsHost.get(i));
        }
        return phl;
    }

    public void delPath(String path) {
        for (int i = 0; i < lsHost.size(); i++) {
            ProxyHost ph = lsHost.get(i);
            if (path.equals(ph.sPath)) {
                lsHost.remove(i);
                iIndex.set(0);
                break;
            }
        }
    }

    public void addPath(String path) {
        add(path, Long.MAX_VALUE);
    }
}
