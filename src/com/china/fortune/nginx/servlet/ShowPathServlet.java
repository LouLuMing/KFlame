package com.china.fortune.nginx.servlet;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.nginx.ActionManager;
import com.china.fortune.nginx.action.NginxInterface;
import com.china.fortune.nginx.action.ProxyAction;
import com.china.fortune.restfulHttpServer.ResultJson;

public class ShowPathServlet extends RestfulStringServlet {
    private ActionManager actionManager;
    public ShowPathServlet(ActionManager am) {
        actionManager = am;
    }
    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < actionManager.size(); i++) {
            jarr.put(actionManager.get(i).toJSONObject());
        }
        JSONObject data = new JSONObject();
        data.put("list", jarr);
        ResultJson.fillOK(json, data);
        return RunStatus.isOK;
    }

    @Override
    protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillData(json, 1, sKey + " is miss", null);
    }
}
