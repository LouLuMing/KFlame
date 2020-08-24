package com.china.fortune.restfulHttpServer.base;

import java.util.HashSet;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.socket.IPUtils;
import com.china.fortune.timecontrol.AceessLimitTime;

public class IPFrequentAction implements ServletInterface {
	private HashSet<Integer> lsAllowed = new HashSet<Integer>();

	public void addAllowIP(int iIP) {
		lsAllowed.add(iIP);
	}

	public void addAllowIP(String sIP) {
		lsAllowed.add(IPUtils.Ip2Int(sIP));
	}

	public boolean isAllow(int iRemoteIP) {
		return lsAllowed.contains(iRemoteIP);
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

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		int iRemoteIP = hReq.getRemoteIP();
		if (isAllow(iRemoteIP) || ipFrequent.isAllowAccess(iRemoteIP)) {
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
