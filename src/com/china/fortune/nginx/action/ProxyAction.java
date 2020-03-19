package com.china.fortune.nginx.action;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.nginx.proxy.ProxyHost;
import com.china.fortune.nginx.proxy.ProxyHostList;
import com.china.fortune.socket.LineSocketAction;

public class ProxyAction implements NginxInterface {
    public String sResource;
    //public String sPath;
    private ProxyHostList proxyHostList = new ProxyHostList();

    private HttpClient httpClient = new HttpClient();

    @Override
    public boolean isMatch(String sTag) {
        return sTag.startsWith(sResource);
    }

    @Override
    public NginxActionType doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
        ProxyHost ph = proxyHostList.get();
        if (ph != null) {
            Log.log(sTag + " " + ph.sPath);
            hReq.setServerIP(ph.httpRequest.getServerIP());
            hReq.setServerPort(ph.httpRequest.getServerPort());
        } else {
            Log.logError(sTag + " no ProxyHost");
        }
        return NginxActionType.NA_PROXY;
    }

//    @Override
//    public NginxActionType doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
//        boolean rs = false;
//        ProxyHost ph = proxyHostList.get();
//        if (ph != null) {
//            LineSocketAction lSA = httpClient.createLSA(ph.httpRequest);
//            if (lSA != null) {
//                if (lSA.write(hReq.bbData.array(), hReq.iHeadLength + hReq.iDataLength)) {
//                    String sLine = lSA.readLine(ConstData.sHttpCharset);
//                    if (sLine != null) {
//                        if (hRes.parseResponse(sLine)) {
//                            httpClient.parseHeader(hRes, lSA);
//                            rs = httpClient.onContentRecv(hRes, lSA);
//                        }
//                    }
//                }
//                lSA.close();
//            }
//            if (rs) {
//                Log.log(sTag + " " + ph.sPath);
//            } else {
//                Log.logError(sTag + " " + ph.sPath);
//            }
//        } else {
//            Log.logError(sTag + " no ProxyHost");
//        }
//        return NginxActionType.NA_WRITE;
//    }

    public ProxyAction(String resource, String path) {
        sResource = resource;
        proxyHostList.add(path, Long.MAX_VALUE);
    }

    public void updatePath(String path) {
        proxyHostList.updatePath(path);
    }

    public void delPath(String path) {
        ProxyHostList phlNew = proxyHostList.clone();
        phlNew.delPath(path);
        proxyHostList = phlNew;
    }

    public void addPath(String path) {
        ProxyHostList phlNew = proxyHostList.clone();
        phlNew.addPath(path);
        proxyHostList = phlNew;
    }

    public void addPathForLive(String path) {
        ProxyHostList phlNew = proxyHostList.clone();
        phlNew.addPath(path);
        proxyHostList = phlNew;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("resource", sResource);
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < proxyHostList.lsHost.size(); i++) {
            ProxyHost ph = proxyHostList.lsHost.get(i);
            jarr.put(ph.sPath);
        }
        json.put("proxy", jarr);
        return json;
    }
}
