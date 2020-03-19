package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.StatisticsServlet;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ServletInterface;

public class ShowStatisticsAction implements ServletInterface {
	private WebServer webServer;

	public ShowStatisticsAction(WebServer ws) {
		webServer = ws;
	}

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		StringBuilder sb = new StringBuilder();
		for (String sTag : webServer.sortTag()) {
			ServletInterface si = webServer.getServlet(sTag);
			if (si != null && si instanceof StatisticsServlet) {
				StatisticsServlet ss = (StatisticsServlet)si;
				ss.addStatistics(sTag, sb);
			}
		}
		hRes.setBody(sb.toString(), "text/plain");
		return null;
	}

	@Override
	public ServletInterface getHost() {
		return this;
	}
}
