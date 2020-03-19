package com.china.fortune.target.simpleWebTarget;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNio;
import com.china.fortune.http.server.HttpServerRequest;

public class SimpleWebServer extends HttpServerNio {
	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes) {
		StringBuilder sb = new StringBuilder();
		sb.append(hReq.toString());
		
		String sBody = hReq.getBody();
		if (sBody != null) {
			sb.append(sBody);
		}
		sBody = sb.toString();
		hRes.setBody(sBody, hReq.getContentType());

		Log.log(sBody);
		return true;
	}
	
	public static void main(String[] args) {
		SimpleWebServer ws = new SimpleWebServer();
		ws.startAndBlock(8080);
	}
}
