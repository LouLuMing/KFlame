package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.common.DateAction;
import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.os.file.PathUtils;

import java.util.HashMap;

public class CacheSmallFileAction implements ServletInterface {
	private String sRootPath = null;
	private HashMap<String, byte[]> cacheObj = new HashMap<String, byte[]>();

	public CacheSmallFileAction() {
		sRootPath = PathUtils.getCurrentDataPath(false);
	}

	public CacheSmallFileAction(String sRoot) {
		if (sRoot != null) {
			sRootPath = PathUtils.delSeparator(sRoot);
		}
	}

	protected String getFileName(String sName) {
		return sRootPath + sName;
	}
	
	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		String sResource = hReq.getResourceWithoutParam();
		Log.logClass(sResource);
		byte[] bData = cacheObj.get(sResource);
		if (bData == null) {
			synchronized (this) {
				String sFile = getFileName(sResource);
				bData = FileUtils.readSmallFile(sFile);
				if (bData != null) {
					cacheObj.put(sResource, bData);
				} else {
					Log.logClassError("Miss:" + sResource + ":" + sFile);
				}
			}
		}
		if (bData != null) {
			hRes.putFile(UrlParam.getUrlLastPart(sResource), bData, DateAction.getGMT());
		}
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
