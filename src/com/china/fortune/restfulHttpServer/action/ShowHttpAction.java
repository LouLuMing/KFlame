package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.ActionToUrl;

public class ShowHttpAction implements ServletInterface {

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		StringBuilder sb = new StringBuilder();
		sb.append(hReq.getRmoteStringIP());
		sb.append('\n');
		sb.append(hReq.toString());
		String sRecvBody = hReq.getBody();
		if (sRecvBody != null) {
			sb.append(sRecvBody);
		}
		Log.logClass(hReq.getResource());
		hRes.setBody(sb.toString(), "text/plain");
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}

}
