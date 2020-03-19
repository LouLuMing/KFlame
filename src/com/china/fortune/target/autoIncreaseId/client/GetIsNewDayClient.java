package com.china.fortune.target.autoIncreaseId.client;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.target.autoIncreaseId.action.GetIsNewDayAction;

public class GetIsNewDayClient {
	private ServerAccess httpConnection = null;
	private String sSqlUrl = ActionToUrl.toUrl(GetIsNewDayAction.class);
	
	public GetIsNewDayClient() {
		httpConnection = new ServerAccess("127.0.0.1", 4999);
	}
	
	public GetIsNewDayClient(int iPort) {
		httpConnection = new ServerAccess("127.0.0.1", iPort);
	}

	public GetIsNewDayClient(String sServer, int iPort) {
		httpConnection = new ServerAccess(sServer, iPort);
	}
	
	
	public boolean isNew(String sTag) {
		return getUnicode(sTag) == 1;
	}
	
	public int getUnicode(String sTag) {
		int id = 0;
		UrlBuilder ub = new UrlBuilder(sSqlUrl);
		ub.add("tag", sTag);
		for (int i = 0; i < 2; i++) {
			JSONObject data = httpConnection.httpGetAndParseData(ub.toString());
			if (data != null) {
				id = data.optInt("id");
				break;
			}
		}
		return id;
	}
	
	static public void main(String[] args) {
		GetIsNewDayClient gisn = new GetIsNewDayClient(4998);
		Log.log("" + gisn.isNew("clock3"));
		Log.log("" + gisn.isNew("clock3"));
		
	}
}
