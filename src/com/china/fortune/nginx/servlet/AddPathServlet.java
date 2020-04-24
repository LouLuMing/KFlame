package com.china.fortune.nginx.servlet;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.nginx.ProxyManager;
import com.china.fortune.nginx.proxy.Proxy;
import com.china.fortune.nginx.proxy.ProxyList;
import com.china.fortune.restfulHttpServer.ResultJson;

public class AddPathServlet extends RestfulStringServlet {
    private ProxyManager actionManager;
    private String[] lsKey = { "resource", "path" };
    public AddPathServlet(ProxyManager am) {
        actionManager = am;
        ksKey.append(lsKey);
        setUrlDecode(true);
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        ProxyList nif = actionManager.get(lsValues[0]);
        if (nif != null) {
            String sPath = lsValues[1];
            if (sPath.contains("://localhost")) {
                sPath = sPath.replace("://localhost", "://" + hReq.getRmoteStringIP());
            }
            Proxy py = nif.get(sPath);
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
