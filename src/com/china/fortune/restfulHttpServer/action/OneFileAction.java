package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.os.file.PathUtils;

public class OneFileAction implements ServletInterface {
	private String sFile = null;

	public OneFileAction() {
		sFile = PathUtils.getCurrentDataPath(false);
	}

	public OneFileAction(String sFileName) {
		setFile(sFileName);
	}

	public void setFile(String sFileName) {
		if (sFileName != null) {
			sFileName = PathUtils.getFullPath(sFileName);
			if (sFileName != null && FileUtils.isExists(sFileName)) {
				sFile = sFileName;
			} else {
				Log.logClassError(sFileName + " is not exist");
			}
		} else {
			Log.logClassError("sFileName is null");
		}
	}

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		String sResource = hReq.getResource();
		hRes.putFile(sFile);
		Log.logClass(sResource + ":" + sFile);
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
