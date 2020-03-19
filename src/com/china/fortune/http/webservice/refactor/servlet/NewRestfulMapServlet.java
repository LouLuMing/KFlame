package com.china.fortune.http.webservice.refactor.servlet;

import com.china.fortune.global.Log;
import com.china.fortune.http.PairBuilder;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.CheckKeys;
import com.china.fortune.json.JSONObject;

import java.util.HashMap;
import java.util.Map.Entry;

public abstract class NewRestfulMapServlet extends NewRestfulBaseServlet<HashMap<String, String>> {
	protected CheckKeys ksKey = new CheckKeys();
	
	abstract protected void onParamError(JSONObject json, String sErrorMsg);

	@Override
	public RunStatus unPackAndWork(HttpServerRequest hReq, JSONObject json, Object objForThread) {
		RunStatus bOK = RunStatus.isError;
		HashMap<String, String> map = unPack(hReq);
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
	protected void onFoundParam(HashMap<String, String> map, String sKey, String sValue) {
		map.put(sKey, urlDecodeValue(sValue));
	}

	@Override
	protected HashMap<String, String> newObject() {
		return new HashMap<String, String>();
	}

    @Override
	public String showUrlParam(String sUrl, HashMap<String, String> map) {
		if (map != null) {
			PairBuilder pb = new PairBuilder();
			for (Entry<String, String> en : map.entrySet()) {
				pb.add(en.getKey(), en.getValue());
			}
			if (pb.size() > 0) {
				return sUrl + "?" + pb.toString();
			} else {
				return sUrl;
			}
		} else {
			return UrlParam.together(sUrl, ksKey.toParam());
		}
	}

	@Override
	public String showUrlParam(String sUrl) {
		return UrlParam.together(sUrl, ksKey.toParam());
	}
}