package com.china.fortune.http.webservice;

import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ServletInterface;

public class StatisticsServlet implements ServletInterface {
	private AtomicLong alCount = new AtomicLong(0);
	private AtomicLong alCost = new AtomicLong(0);
	private ServletInterface svHost;

	public StatisticsServlet(ServletInterface st) {
		svHost = st;
	}

	public ServletInterface getHost() {
		return svHost;
	};

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		alCount.incrementAndGet();
		long lStart = System.currentTimeMillis();
		RunStatus rs = svHost.doAction(hReq, hRes, objForThread);
		alCost.addAndGet(System.currentTimeMillis() - lStart);
		return rs;
	}

	public void addStatistics(String sUrl, StringBuilder sb) {
		sb.append(sUrl);
		sb.append('\t');
		sb.append(alCount.get());
		sb.append('\t');
		sb.append(alCost.get());
		sb.append('\n');
	}

	public void resetStatistics(long lCount, long lCost) {
		alCount.set(lCount);
		alCost.set(lCost);
	}

	public long getAccessCount() {
		return alCount.get();
	}

	public long getAccessCost() {
		return alCost.get();
	}
//
//	@Override
//	public String getResource() {
//		return svHost.getResource();
//	}
}
