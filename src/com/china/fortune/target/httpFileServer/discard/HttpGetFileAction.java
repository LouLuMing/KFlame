package com.china.fortune.target.httpFileServer.discard;

import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.UrlParam;

public class HttpGetFileAction {
	private String sGetResource;
	
	public HttpGetFileAction(String sGet) {
		sGetResource = sGet;
	}
	
	public String getGetResource() {
		return sGetResource;
	}
	
	public boolean checkHttpGet(HttpRequest hReq) {
		return true;
	}
	
	public String getFileName(HttpRequest hReq) {
		return UrlParam.getResource(hReq.getResource());
	}
}
