package com.china.fortune.proxy;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.proxy.host.HostList;

import java.nio.channels.SelectionKey;

public class HttpProxyRequest extends HttpServerRequest {
    public SelectionKey skClient;
    public SelectionKey skChannel;
    public HostList pl;

//    public void clear() {
//        super.clear();
//        skClient = null;
//        skChannel = null;
//        pl = null;
//    }

}
