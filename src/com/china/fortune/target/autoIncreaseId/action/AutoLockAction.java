package com.china.fortune.target.autoIncreaseId.action;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONUtils;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.string.StringAction;
import com.china.fortune.struct.FastHashMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoLockAction extends RestfulStringServlet {
	private String[] lsKey = { "tag", "value", "nextvalue" };
	private FastHashMap<AtomicInteger> mapUnicodeId = new FastHashMap<AtomicInteger>();

	public AutoLockAction() {
		ksKey.append(lsKey);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

	public void addTag(String tag) {
		AtomicInteger ind = new AtomicInteger(0);
		mapUnicodeId.put(tag, ind);
	}

	public void initHitCache() {
		mapUnicodeId.initHitCache();
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValue) {
		AtomicInteger ind = mapUnicodeId.get(lsValue[0]);
		if (ind != null) {
			int value = StringAction.toInteger(lsValue[1]);
			int nextvalue = StringAction.toInteger(lsValue[2]);
			JSONObject data = new JSONObject();
			int id = 0;
			if (ind.compareAndSet(value, nextvalue)) {
				id = 1;
			}
			JSONUtils.put(data, "id", id);
			ResultJson.fillData(json, 0, "ok", data);
		} else {
			ResultJson.fillData(json, 1, "miss", null);
		}

		return RunStatus.isOK;
	}

}
