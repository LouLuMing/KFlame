package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.objectList.ObjectListManager;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.BaseServer;
import com.china.fortune.restfulHttpServer.ResultJson;

public class UpdateObjectListAction extends RestfulStringServlet {
	private ObjectListManager objectListManager = null;
	private String[] lsUnKey = { "key" };
	
	public UpdateObjectListAction(BaseServer self) {
		objectListManager = self.getObjectListManager();
		ksUnKey.append(lsUnKey);
	}
	
	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
		boolean rs = true;
		if (lsValues[0] != null) {
			rs = objectListManager.update((MySqlDbAction) dbObj, lsValues[0]);
		} else {
			objectListManager.update((MySqlDbAction) dbObj);
		}
		if (rs) {
			ResultJson.fillOK(json);
		} else {
			ResultJson.fillError(json, "miss " + lsValues[0]);
		}
		return RunStatus.isOK;
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey + " is miss", null);
	}

}
