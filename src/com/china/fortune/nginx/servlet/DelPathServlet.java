package com.china.fortune.nginx.servlet;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.nginx.ActionManager;
import com.china.fortune.nginx.action.NginxInterface;
import com.china.fortune.nginx.action.ProxyAction;

public class DelPathServlet extends RestfulStringServlet {
    private ActionManager actionManager;
    private String[] lsKey = { "resource", "path" };
    public DelPathServlet(ActionManager am) {
        actionManager = am;
        ksKey.append(lsKey);
    }
    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        NginxInterface nif = actionManager.get(lsValues[0]);
        if (nif != null && nif instanceof ProxyAction) {
            ((ProxyAction)nif).delPath(lsValues[1]);
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
