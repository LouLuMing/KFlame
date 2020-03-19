package com.china.fortune.nginx.proxy;

import com.china.fortune.http.httpHead.HttpRequest;

public class ProxyHost {
    public String sPath;
    public HttpRequest httpRequest;
    public long lLiveTicket;
    static final private int iLimitedTicket = 60 * 1000;

    public void active() {
        lLiveTicket = System.currentTimeMillis();
    }

    public boolean isActive(long lNow) {
        return lLiveTicket > lNow -iLimitedTicket;
    }
}
