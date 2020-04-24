package com.china.fortune.proxy.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.proxy.ProxyManager;
import com.china.fortune.proxy.property.NgnixResource;
import com.china.fortune.proxy.servlet.ShowPathServlet;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.proxy.servlet.AddPathServlet;
import com.china.fortune.proxy.servlet.DelPathServlet;
import com.china.fortune.string.StringAction;

public class CommandAction {
    // /xginx/add
    // /xginx/del
    // /xginx/show

    private AddPathServlet addPathServlet;
    private DelPathServlet delPathServlet;
    private ShowPathServlet showPathServlet;
    private int iPrevHead = 0;
    public CommandAction(ProxyManager am) {
        addPathServlet = new AddPathServlet(am);
        delPathServlet = new DelPathServlet(am);
        showPathServlet = new ShowPathServlet(am);
        iPrevHead = NgnixResource.sPrevHead.length();
    }

    public boolean isMatch(String sTag) {
        return sTag.startsWith(NgnixResource.sPrevHead);
    }

    public void doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
        if (StringAction.compareTo(sTag, iPrevHead, NgnixResource.sAddUrl)) {
            addPathServlet.doAction(hReq, hRes, null);
        } else if (StringAction.compareTo(sTag, iPrevHead, NgnixResource.sDelUrl)) {
            delPathServlet.doAction(hReq, hRes, null);
        } else if (StringAction.compareTo(sTag, iPrevHead, NgnixResource.sShowUrl)) {
            showPathServlet.doAction(hReq, hRes, null);
        } else if (StringAction.compareTo(sTag, iPrevHead, NgnixResource.sEchoUrl)) {
            hRes.setResponse(200);
            hRes.setBody(hReq.getByteBody());
        }else {
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
