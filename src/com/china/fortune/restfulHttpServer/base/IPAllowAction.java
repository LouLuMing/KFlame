package com.china.fortune.restfulHttpServer.base;

import java.util.HashSet;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.timecontrol.TimeoutSetAction;

public class IPAllowAction implements ServletInterface {
	static private TimeoutSetAction<Integer> setTemporaryAllowedIP = new TimeoutSetAction<Integer>(2, 18);

	static public void addTemporaryAllowIP(int iIP) {
		setTemporaryAllowedIP.add(iIP);
	}

	static public void addTemporaryAllowIP(String sIP) {
		setTemporaryAllowedIP.add(IPHelper.Ip2Int(sIP));
	}

	static public boolean isTemporaryAllow(int iRemoteIP) {
		if (setTemporaryAllowedIP.contains(iRemoteIP)) {
			setTemporaryAllowedIP.add(iRemoteIP);
			return true;
		} else {
			return false;
		}
	}

	private HashSet<Integer> lsAllowedIP = new HashSet<Integer>();

	public void addAllowIP(String sIP) {
		addAllowIP(IPHelper.Ip2Int(sIP));
	}

	public void addAllowIP(int iIP) {
		if (!lsAllowedIP.contains(iIP)) {
			lsAllowedIP.add(iIP);
		}
	}

	protected void noPermission(HttpServerRequest hReq, HttpResponse hRes) {
		JSONObject json = new JSONObject();
		json.put("ip", hReq.getRemoteIP());
		ResultJson.fillData(json, 1, ResultJson.sNoPermission, null);
		hRes.setBody(json.toString(), "application/json");
	}

	public boolean isAllowAccess(int iRemoteIP) {
		if (IPHelper.ciLoopIP == iRemoteIP || lsAllowedIP.contains(iRemoteIP)) {
			return true;
		} else {
			return isTemporaryAllow(iRemoteIP);
		}
//		return lsAllowedIP.contains(iRemoteIP) || isTemporaryAllow(iRemoteIP);
	}

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		if (isAllowAccess(hReq.getRemoteIP())) {
			return RunStatus.isOK;
		} else {
			noPermission(hReq, hRes);
			return RunStatus.isError;
		}
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}

}
