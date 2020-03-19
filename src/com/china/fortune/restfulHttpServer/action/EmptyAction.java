package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.ResultJson;

public class EmptyAction implements ServletInterface {

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody(ResultJson.sOK, "text/plain");
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}

}
