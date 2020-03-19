package com.china.fortune.http.webservice.servlet;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;

public interface ServletInterface {
    enum RunStatus {isOK, isError, isClose};
    RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes,
			Object objForThread);
    ServletInterface getHost();
//    String getResource();
}
