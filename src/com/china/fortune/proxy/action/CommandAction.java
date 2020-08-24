package com.china.fortune.proxy.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.proxy.property.NgnixResource;
import com.china.fortune.proxy.servlet.RefreshCacheServlet;
import com.china.fortune.proxy.servlet.ShowPathServlet;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.proxy.servlet.AddPathServlet;
import com.china.fortune.proxy.servlet.DelPathServlet;
import com.china.fortune.string.StringUtils;

public class CommandAction {
    // /proxy/add
    // /proxy/del
    // /proxy/cache
    // /proxy/show

    private AddPathServlet addPathServlet = new AddPathServlet();
    private DelPathServlet delPathServlet = new DelPathServlet();
    private ShowPathServlet showPathServlet = new ShowPathServlet();
    private RefreshCacheServlet refreshCacheServlet = new RefreshCacheServlet();
    private int iPrevHead = 0;
    public CommandAction() {
        iPrevHead = NgnixResource.sPrevHead.length();
    }

    public boolean isMatch(String sTag) {
        return sTag.startsWith(NgnixResource.sPrevHead);
    }

    public void doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
        if (StringUtils.compareTo(sTag, iPrevHead, NgnixResource.sAddUrl)) {
            addPathServlet.doAction(hReq, hRes, null);
        } else if (StringUtils.compareTo(sTag, iPrevHead, NgnixResource.sDelUrl)) {
            delPathServlet.doAction(hReq, hRes, null);
        } else if (StringUtils.compareTo(sTag, iPrevHead, NgnixResource.sCacheUrl)) {
            refreshCacheServlet.doAction(hReq, hRes, null);
        } else if (StringUtils.compareTo(sTag, iPrevHead, NgnixResource.sShowUrl)) {
            showPathServlet.doAction(hReq, hRes, null);
        } else if (StringUtils.compareTo(sTag, iPrevHead, NgnixResource.sEchoUrl)) {
            hRes.setResponse(200);
            hRes.setBody(hReq.getByteBody());
        } else {
            JSONObject json = new JSONObject();
            ResultJson.fillError(json, "miss " + sTag);
            hRes.setBody(json.toString(), HttpProp.getContentType("json"));
        }

//        if (sTag.startsWith(NgnixResource.sAddUrl)) {
//            addPathServlet.doAction(hReq, hRes, null);
//        } else if (sTag.startsWith(NgnixResource.sDelUrl)) {
//            delPathServlet.doAction(hReq, hRes, null);
//        } else if (sTag.startsWith(NgnixResource.sShowUrl)) {
//            showPathServlet.doAction(hReq, hRes, null);
//        } else {
//            JSONObject json = new JSONObject();
//            ResultJson.fillError(json, "miss " + sTag);
//            hRes.setBody(json.toString(), HttpProp.getContentType("json"));
//        }
    }

}
