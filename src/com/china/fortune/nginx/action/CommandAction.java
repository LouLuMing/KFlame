package com.china.fortune.nginx.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.nginx.servlet.ShowPathServlet;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.nginx.ActionManager;
import com.china.fortune.nginx.servlet.AddPathServlet;
import com.china.fortune.nginx.servlet.DelPathServlet;

public class CommandAction {
    public String sResource = "/nginx";

    // /xginx/add
    // /xginx/del
    // /xginx/show

    private AddPathServlet addPathServlet;
    private DelPathServlet delPathServlet;
    private ShowPathServlet showPathServlet;
    public CommandAction(ActionManager am) {
        addPathServlet = new AddPathServlet(am);
        delPathServlet = new DelPathServlet(am);
        showPathServlet = new ShowPathServlet(am);
    }

    public boolean isMatch(String sTag) {
        return sTag.startsWith(sResource);
    }

    public NginxActionType doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
        if (sTag.startsWith(sResource + "/add")) {
            addPathServlet.doAction(hReq, hRes, null);
        } else if (sTag.startsWith(sResource + "/del")) {
            delPathServlet.doAction(hReq, hRes, null);
        } else if (sTag.startsWith(sResource + "/show")) {
            showPathServlet.doAction(hReq, hRes, null);
        } else {
            JSONObject json = new JSONObject();
            ResultJson.fillError(json, "miss " + sTag);
            hRes.setBody(json.toString(), HttpProp.getContentType("json"));
        }
        return NginxActionType.NA_WRITE;
    }

}
