package com.china.fortune.nginx.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;

public interface NginxInterface {
// 0 error
// 1 ok write
// 2 ok proxy
    NginxActionType doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes);
    boolean isMatch(String sTag);
    JSONObject toJSONObject();
}
