package com.china.fortune.http.webservice.servlet;

import com.china.fortune.global.Log;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;

public abstract class RestfulPostServlet extends RestfulBaseServlet<JSONObject> {
	protected CheckKeys ksKey = new CheckKeys();

	abstract protected void onParamError(JSONObject json, String sMissKey);

	protected JSONObject unPack(HttpServerRequest hReq) {
		JSONObject obj = newObject();
		obj.parseJSONObject(hReq.getBody());
		return obj;
	}

	@Override
	public RunStatus unPackAndWork(HttpServerRequest hReq, JSONObject json, Object objForThread) {
		RunStatus bOK = RunStatus.isError;
		JSONObject map = unPack(hReq);
		int iNullKey = ksKey.checkNull(map);
		if (iNullKey < 0) {
			try {
				bOK = doWork(hReq, json, objForThread, map);
			} catch (Exception e) {
				Log.logException(e);
			}
		} else {
			onParamError(json, ksKey.getKey(iNullKey));
		}
		return bOK;
	}

	@Override
	protected void onFoundParam(JSONObject map, String sKey, String sValue) {
		map.put(sKey, urlDecodeValue(sValue));
	}

	@Override
	protected JSONObject newObject() {
		return new JSONObject();
	}

	@Override
	public String showUrlParam(String sUrl, JSONObject obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(sUrl);
		sb.append(":");
		if (obj != null) {
			sb.append(obj.toString());
		} else {
			sb.append(ksKey.toJson());
		}
		return sb.toString();
	}

	@Override
	public String showUrlParam(String sUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append(sUrl);
		sb.append(":");
		sb.append(ksKey.toJson());
		return sb.toString();
	}
}