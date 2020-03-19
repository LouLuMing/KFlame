package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.struct.FastList;

import java.io.File;

public class ResourceMapPathAction implements ServletInterface {
	private FastList<String2Struct> lsMap = new FastList<>();

	public ResourceMapPathAction() {
	}

	public void addResource(String sResource, String sPath) {
		sPath = PathUtils.getFullPath(sPath);
		if (sResource.equals("/")) {
			sPath = PathUtils.addSeparator(sPath);
		} else {
			sPath = PathUtils.delSeparator(sPath);
		}
		lsMap.add(new String2Struct(sResource, sPath));
	}

	private String getMapper(String sResource) {
		for (int i = 0; i < lsMap.size(); i++) {
			String2Struct s2s = lsMap.get(i);
			if (sResource.startsWith(s2s.s1)) {
				String sPath = sResource.substring(s2s.s1.length());
				if (File.separatorChar != '/') {
					sPath = s2s.s2 + sPath.replace('/', File.separatorChar);
				} else {
					sPath = s2s.s2 + sPath;
				}
				Log.logClass(sResource + ":" + sPath);
				return sPath;
			}
		}
		Log.logClassError(sResource + " no match");
		return null;
	}
	
	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		String sResource = hReq.getResourceWithoutParam();
		String sPath = getMapper(sResource);
		if (sPath != null) {
			hRes.putFile(sPath);
		} else {
			hRes.setResponse(404);
		}
		return RunStatus.isOK;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
