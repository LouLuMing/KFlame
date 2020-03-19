package com.china.fortune.target.httpFileServer.discard;

import com.china.fortune.common.PrimaryKey;
import com.china.fortune.http.httpHead.HttpRequest;

public class HttpPostFileAction {
	private String sPostResource;
	protected PrimaryKey fileNameObj = new PrimaryKey();
	
	public HttpPostFileAction(String sPost) {
		sPostResource = sPost;
	}
	
	public String getPostResource() {
		return sPostResource;
	};
	
	public boolean checkHttpPost(HttpRequest hReq) {
		return true;
	}
	
	public String createRelativePath(HttpRequest hReq) {
		return null;
	}
	
	public String createFileName(HttpRequest hReq) {
		return fileNameObj.createUnicode();
	}
}
