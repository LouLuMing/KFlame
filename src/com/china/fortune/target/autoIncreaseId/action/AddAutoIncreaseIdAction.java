package com.china.fortune.target.autoIncreaseId.action;

import com.china.fortune.database.MaxIdHashMap;
import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;

public class AddAutoIncreaseIdAction extends RestfulStringServlet {
	private String[] lsKey = { "table", "id" };

	private WebServer self = null;
	public AddAutoIncreaseIdAction(WebServer ws) {
		ksKey.append(lsKey);
		self = ws;
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
		GetAutoIncreaseIdAction actionObj = (GetAutoIncreaseIdAction) self.getServlet(GetAutoIncreaseIdAction.class);
		actionObj.addTableAndId((MySqlDbAction)objForThread, lsValue[0], lsValue[1]);
		ResultJson.fillData(json, 0, "ok", null);
		return RunStatus.isOK;
	}

}
