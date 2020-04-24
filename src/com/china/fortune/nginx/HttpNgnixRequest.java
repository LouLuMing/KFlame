package com.china.fortune.nginx;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.nginx.proxy.ProxyList;

import java.nio.channels.SelectionKey;

public class HttpNgnixRequest extends HttpServerRequest {
    public SelectionKey skClient;
    public SelectionKey skChannel;
    public ProxyList pl;

//    public void clear() {
//        super.clear();
//        skClient = null;
//        skChannel = null;
//        pl = null;
//    }

}
