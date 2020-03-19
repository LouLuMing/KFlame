package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.os.file.PathUtils;

import java.io.File;

public class GetSmallFileAction implements ServletInterface {
	private String sRootPath = null;

	public GetSmallFileAction() {
		sRootPath = PathUtils.getCurrentDataPath(false);
	}

	public GetSmallFileAction(String sRoot) {
		setRootPath(sRoot);
	}

	public void setRootPath(String sRoot) {
		if (sRoot != null) {
			sRootPath = PathUtils.delSeparator(sRoot);
		}
	}

	protected String getFileName(String sResource) {
		String sFullPath = sRootPath + sResource;
		if (File.separatorChar != '/') {
            sFullPath = sFullPath.replace('/', File.separatorChar);
        }
		Log.logClass(sResource + ":" + sFullPath);
		return sFullPath;
	}
	
	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		String sResource = hReq.getResourceWithoutParam();
		hRes.putFile(getFileName(sResource));
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
