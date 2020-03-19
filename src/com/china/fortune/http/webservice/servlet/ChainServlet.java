package com.china.fortune.http.webservice.servlet;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.struct.FastList;

public class ChainServlet implements ServletInterface {
	private FastList<ServletInterface> servletChain = new FastList<ServletInterface>();

	private ServletInterface siHost = null;

	public ChainServlet(ServletInterface si) {
		siHost = si;
	}

	public ServletInterface getHost() {
		return siHost;
	}

	public void addChild(ServletInterface si) {
		if (si != null) {
			servletChain.add(si);
		}
	}

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		for (int i = servletChain.size() - 1; i >= 0 ; i--) {
			ServletInterface si = servletChain.get(i);
			if (si != null) {
				RunStatus rs = si.doAction(hReq, hRes, objForThread);
				if (rs != RunStatus.isOK) {
					return rs;
				}
			}
		}
		return siHost.doAction(hReq, hRes, objForThread);
	}

}