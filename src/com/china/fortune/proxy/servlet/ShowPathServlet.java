package com.china.fortune.proxy.servlet;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.proxy.ProxyManager;
import com.china.fortune.restfulHttpServer.ResultJson;

public class ShowPathServlet extends RestfulStringServlet {
    private ProxyManager actionManager;
    private String[] lsKey = { "resource" };
    public ShowPathServlet(ProxyManager am) {
        actionManager = am;
        ksUnKey.append(lsKey);
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        JSONObject data = new JSONObject();
        String resource = lsValues[0];
        if (resource != null) {
            data.put("list", actionManager.toJSONArray(resource));
        } else {
            data.put("list", actionManager.toJSONArray());
        }
        ResultJson.fillOK(json, data);
        return RunStatus.isOK;
    }

    @Override
    protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillData(json, 1, sKey + " is miss", null);
    }
}
