package com.china.fortune.proxy.servlet;

import com.china.fortune.global.CommonResource;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.proxy.ProxyManager;
import com.china.fortune.proxy.host.Host;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.restfulHttpServer.ResultJson;

public class AddPathServlet extends RestfulStringServlet {
    private String[] lsKey = { "resource", "path" };
    private String[] lsUnKey = { "cache", "gzip" };
    public AddPathServlet() {
        ksKey.append(lsKey);
        ksUnKey.append(lsUnKey);
        setUrlDecode(true);
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        ProxyManager actionManager = CommonResource.get(ProxyManager.class);
        HostList nif = actionManager.get(lsValues[0]);
        if (nif != null) {
            String sPath = lsValues[1];
            if (sPath.contains("://localhost")) {
                sPath = sPath.replace("://localhost", "://" + hReq.getRmoteStringIP());
            }
            Host py = nif.get(sPath);
            if (py == null) {
                actionManager.cloneAndAdd(lsValues[0], sPath);
            } else {
                py.setResult(true);
            }
            ResultJson.fillOK(json);
        } else {
            ResultJson.fillError(json, "miss " + lsValues[0]);
        }
        return RunStatus.isOK;
    }

    @Override
    protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillData(json, 1, sKey + " is miss", null);
    }
}
