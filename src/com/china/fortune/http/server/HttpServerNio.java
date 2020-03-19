package com.china.fortune.http.server;

import com.china.fortune.http.httpHead.HttpResponse;

public abstract class HttpServerNio extends HttpServerNioAttach {
	protected abstract boolean service(HttpServerRequest hReq, HttpResponse hRes);

	@Override
	protected Object createObjectInThread() {
		return null;
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		return service(hReq, hRes);
	}
}
