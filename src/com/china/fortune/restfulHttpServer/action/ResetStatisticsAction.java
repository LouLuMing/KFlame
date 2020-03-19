package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.StatisticsServlet;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;

public class ResetStatisticsAction extends RestfulStringServlet {
	private WebServer webServer = null;
	private String[] lsUnKey = { "url" };
	public ResetStatisticsAction(WebServer ws) {
		ksUnKey.append(lsUnKey);
		webServer = ws;
	}

	public void resetStatistics(String sTag) {
		ServletInterface si = webServer.getServlet(sTag);
		if (si != null && si instanceof StatisticsServlet) {
			StatisticsServlet ss = (StatisticsServlet)si;
			ss.resetStatistics(0, 0);
		}
	}

	public void resetStatistics() {
		for (int i = 0; i < webServer.getServletSize(); i++) {
			ServletInterface si = webServer.getServlet(i);
			if (si != null && si instanceof StatisticsServlet) {
				StatisticsServlet ss = (StatisticsServlet)si;
				ss.resetStatistics(0, 0);
			}
		}
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
		if (lsValues[0] != null) {
			resetStatistics(lsValues[0]);
		} else {
			resetStatistics();
		}
		ResultJson.fillOK(json);
		return RunStatus.isOK;
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

}
