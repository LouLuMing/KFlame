package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.database.objectList.ObjectListFromDB;
import com.china.fortune.database.objectList.ObjectListManager;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.BaseServer;
import com.china.fortune.restfulHttpServer.ResultJson;

public class GetObjectListAction extends RestfulStringServlet {
	private ObjectListManager objectListManager = null;
	private String[] lsKey = { "key" };

	public GetObjectListAction(BaseServer self) {
		objectListManager = self.getObjectListManager();
		ksKey.append(lsKey);
	}
	
	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
		ObjectListFromDB ol = objectListManager.get(lsValues[0]);
		if (ol != null) {
			JSONArray jarr = ol.toJSONArray();
			JSONObject data = new JSONObject();
			data.put("list", jarr);
			ResultJson.fillData(json, 0, "ok", data);
		} else {
			ResultJson.fillData(json, 1, "no data", null);
		}
		return RunStatus.isOK;
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

}
