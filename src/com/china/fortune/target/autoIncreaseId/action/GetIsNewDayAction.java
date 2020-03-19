package com.china.fortune.target.autoIncreaseId.action;

import java.util.HashMap;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONUtils;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.timecontrol.newDay.IsNewDay;

public class GetIsNewDayAction extends RestfulStringServlet {
	private String[] lsKey = { "tag" };
	private HashMap<String, IsNewDay> mapUnicodeId = new HashMap<String, IsNewDay>();

	public GetIsNewDayAction() {
		ksKey.append(lsKey);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

	public void addTag(String tag, int clock) {
		IsNewDay ind = new IsNewDay(tag);
		ind.setAlarm(clock);
		mapUnicodeId.put(tag, ind);
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValue) {
		IsNewDay ind = mapUnicodeId.get(lsValue[0]);
		if (ind != null) {
			JSONObject data = new JSONObject();

			int id = 0;
			if (ind.isNew()) {
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
