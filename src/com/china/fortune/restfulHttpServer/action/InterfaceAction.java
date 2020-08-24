package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.ServletUtils;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.RestfulBaseServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.json.JSONObject;

public class InterfaceAction extends RestfulBaseServlet<Object> {
    private WebServer webServer = null;

    public InterfaceAction(WebServer ws) {
        webServer = ws;
    }

    protected void buildDoc(StringBuilder sb, String sHost, String sUrl) {
        if (sHost != null) {
            sb.append(sHost);
        }
        sb.append(sUrl);
        sb.append('\n');
    }


    @Override
    public void setJsonToBody(HttpServerRequest hReq, HttpResponse hRes, JSONObject json) {
        StringBuilder sb = new StringBuilder();
        String sHost = hReq.getHeaderValue(HttpHeader.csHost);
        if (sHost != null) {
            sHost = "http://" + sHost;
        }
        for (String sTag : webServer.sortTag()) {
            ServletInterface si = webServer.getServlet(sTag);
            if (si != null) {
                ServletInterface host = ServletUtils.getFinalHost(si);
                String sUrl = sTag;
                if (host instanceof RestfulBaseServlet) {
                    sUrl = ((RestfulBaseServlet<?>) host).showUrlParam(sTag);
                }
                buildDoc(sb, sHost, sUrl);
            }
        }
        setHttpBody(hReq, hRes, sb.toString(), "text/plain");
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, Object obj) {
        return RunStatus.isOK;
    }

    @Override
    protected Object newObject() {
        return null;
    }

}
