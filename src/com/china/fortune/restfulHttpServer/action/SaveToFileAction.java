package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulBaseServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.DataSaveInterface;
import com.china.fortune.restfulHttpServer.ResultJson;

public class SaveToFileAction extends RestfulBaseServlet<Object> {
	private DataSaveInterface dataSaveInterface = null;

	public SaveToFileAction(DataSaveInterface ds) {
		dataSaveInterface = ds;
	}
	
	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, Object obj) {
		dataSaveInterface.saveToFile();
		ResultJson.fillOK(json);
		return RunStatus.isOK;
	}

	@Override
	protected Object newObject() {
		return null;
	}
}
