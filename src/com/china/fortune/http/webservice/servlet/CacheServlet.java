package com.china.fortune.http.webservice.servlet;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;

public class CacheServlet implements ServletInterface {
	private long lExpireTime = 60000;
	private AtomicLong lActiveTime = new AtomicLong(0);
	private AtomicReference<HttpResponse> hrCache = new AtomicReference<HttpResponse>();
	private ServletInterface siHost = null;
	
	public CacheServlet(ServletInterface si) {
		siHost = si;
	}
	
	public void setExpire(long l) {
		lExpireTime = l;
	}

	public long getExpire() {
		return lExpireTime;
	}
	
	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes,
			Object objForThread) {
		if (lExpireTime > 0) {
			RunStatus rs = RunStatus.isOK;
			long lLimitTime = System.currentTimeMillis() - lExpireTime;
			if (lLimitTime >= lActiveTime.get()) {
				synchronized (this) {
					if (lLimitTime >= lActiveTime.get()) {
						lActiveTime.set(System.currentTimeMillis());
						rs = siHost.doAction(hReq, hRes, objForThread);
						if (rs == RunStatus.isOK) {
							hrCache.set(hRes);
						}
					} else {
						hRes.copy(hrCache.get());
					}
				}
			} else {
				hRes.copy(hrCache.get());
			}
			return rs;
		} else {
			return siHost.doAction(hReq, hRes, objForThread);
		}
	}

	@Override
	public ServletInterface getHost() {
		return siHost;
	}
}
