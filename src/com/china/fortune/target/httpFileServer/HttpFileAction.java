package com.china.fortune.target.httpFileServer;

import com.china.fortune.common.PrimaryKey;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.UrlParam;

public class HttpFileAction {
	private String sPostResource;
	private String sGetResource;
	protected PrimaryKey fileNameObj = new PrimaryKey();
	
	public HttpFileAction(String sGet, String sPost) {
		sPostResource = sPost;
		sGetResource = sGet;
	}
	
	public String getPostResource() {
		return sPostResource;
	};
	
	public String getGetResource() {
		return sGetResource;
	}
	
	public boolean checkHttpPost(HttpRequest hReq) {
		return true;
	}
	
	public boolean checkHttpGet(HttpRequest hReq) {
		return true;
	}
	
	public String createRelativePath(HttpRequest hReq) {
		return null;
	}
	
	public String createFileName(HttpRequest hReq) {
		return fileNameObj.createUnicode();
	}
	
	public String getFileName(HttpRequest hReq) {
		return UrlParam.getResource(hReq.getResource());
	}
}
