package com.china.fortune.restfulHttpServer.base;

import java.util.HashSet;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.timecontrol.AceessLimitTime;

public class IPFrequentAction implements ServletInterface {
	static private HashSet<Integer> lsAllowed = new HashSet<Integer>();

	static public void addAllowIP(int iIP) {
		lsAllowed.add(iIP);
	}

	static public void addAllowIP(String sIP) {
		lsAllowed.add(IPHelper.Ip2Int(sIP));
	}

	static public boolean isAllow(int iRemoteIP) {
		if (lsAllowed.contains(iRemoteIP)) {
			return true;
		} else {
			return false;
		}
	}

	// 15+2 = 17, 256second, allow 64 times

	public IPFrequentAction() {
		ipFrequent = new AceessLimitTime<Integer>(15, 64);
	}

	public IPFrequentAction(int iTimeOutPower, int iMaxAcess) {
		ipFrequent = new AceessLimitTime<Integer>(iTimeOutPower, iMaxAcess);
	}

	private AceessLimitTime<Integer> ipFrequent = null;

	protected void setErrorBody(HttpServerRequest hReq, HttpResponse hRes) {
		Log.logClass(hReq.getRmoteStringIP() + ":" + hReq.getResource());
		hRes.setBody(ResultJson.sJsonFrequentAccessLimit, "application/json");
	}

	static final private int ciLoopIP = IPHelper.Ip2Int("127.0.0.1");

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		int iRemoteIP = hReq.getRemoteIP();
		if (iRemoteIP == ciLoopIP || isAllow(iRemoteIP) || ipFrequent.isAllowAccess(iRemoteIP)) {
			return RunStatus.isOK;
		} else {
			setErrorBody(hReq, hRes);
			return RunStatus.isError;
		}
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
