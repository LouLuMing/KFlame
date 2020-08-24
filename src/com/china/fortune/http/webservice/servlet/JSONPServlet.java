package com.china.fortune.http.webservice.servlet;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulBaseServlet;
import com.china.fortune.json.JSONObject;

public class JSONPServlet extends RestfulBaseServlet<Object> {
	private RestfulBaseServlet<?> host = null;
	private String sKey = "callback";

	public JSONPServlet(RestfulBaseServlet<?> rs) {
		host = rs;
	}

	public void setCallback(String callback) {
		sKey = callback;
	}

	@Override
	public void setJsonToBody(HttpServerRequest hReq, HttpResponse hRes, JSONObject json) {
		String sResource = hReq.getResource();
		String sCallBack = UrlParam.findValue(sResource, sKey);
		if (sCallBack == null) {
			sCallBack = sKey;
		}
		String sJsonP = sCallBack + "(" + json + ")";
		
		hRes.setBody(sJsonP, "application/json", sCharset);
		hRes.accessControlAllow();
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, Object obj) {
		return host.unPackAndWork(hReq, json, objForThread);
	}

	@Override
	protected Object newObject() {
		return null;
	}

	@Override
	public String showUrlParam(String sUrl) {
		return host.showUrlParam(sUrl);
	}
}
