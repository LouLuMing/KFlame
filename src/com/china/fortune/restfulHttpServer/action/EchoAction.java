package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;

public class EchoAction implements ServletInterface {

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody("hello world", "text/plain");
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}

}
