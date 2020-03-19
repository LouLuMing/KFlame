package com.china.fortune.target.autoIncreaseId.action;

import java.util.HashMap;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONUtils;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.target.autoIncreaseId.entity.DateAndAutoInteger;

public class GetUnicodeAction extends RestfulStringServlet {
	private String[] lsKey = { "tag" };
	private HashMap<String, DateAndAutoInteger> mapUnicodeId = new HashMap<String, DateAndAutoInteger>();
	public GetUnicodeAction() {
		ksKey.append(lsKey);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

	public void addTag(String tag) {
		DateAndAutoInteger addi = new DateAndAutoInteger();
		mapUnicodeId.put(tag, addi);
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValue) {
		DateAndAutoInteger addi = mapUnicodeId.get(lsValue[0]);
		if (addi != null) {
			JSONObject data = new JSONObject();
			JSONUtils.put(data, "id", addi.getUnicode());
			ResultJson.fillData(json, 0, "ok", data);
		} else {
			ResultJson.fillData(json, 1, "miss", null);
		}


		return RunStatus.isOK;
	}

}
