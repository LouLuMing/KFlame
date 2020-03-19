package com.china.fortune.target.autoIncreaseId.client;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.target.autoIncreaseId.action.GetTimeoutAction;

public class GetTimeoutClient {
	private ServerAccess httpConnection = null;
	private String sSqlUrl = ActionToUrl.toUrl(GetTimeoutAction.class);
	
	public GetTimeoutClient() {
		httpConnection = new ServerAccess("127.0.0.1", 4999);
	}
	
	public GetTimeoutClient(int iPort) {
		httpConnection = new ServerAccess("127.0.0.1", iPort);
	}

	public GetTimeoutClient(String sServer, int iPort) {
		httpConnection = new ServerAccess(sServer, iPort);
	}
	
	public boolean isTimeout(String sTag) {
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
		GetTimeoutClient gisn = new GetTimeoutClient(4998);
		Log.log("" + gisn.isTimeout("min10"));
		Log.log("" + gisn.isTimeout("min10"));
		
	}
}
