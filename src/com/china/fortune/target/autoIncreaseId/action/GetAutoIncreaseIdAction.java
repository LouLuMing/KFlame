package com.china.fortune.target.autoIncreaseId.action;

import com.china.fortune.database.MaxIdHashMap;
import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONUtils;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;

public class GetAutoIncreaseIdAction extends RestfulStringServlet {
	private String[] lsKey = { "table", "id" };

	public GetAutoIncreaseIdAction() {
		ksKey.append(lsKey);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

	public void addTableAndId(MySqlDbAction dbObj, String table, String id) {
		maxIdMap.addTableAndId(dbObj, table, id);
	}

	private MaxIdHashMap maxIdMap = new MaxIdHashMap();

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValue) {
		int id = maxIdMap.getMaxId(lsValue[0], lsValue[1]);
		JSONObject data = new JSONObject();
		JSONUtils.put(data, "id", id);
		ResultJson.fillData(json, 0, "ok", data);

		return RunStatus.isOK;
	}

}
