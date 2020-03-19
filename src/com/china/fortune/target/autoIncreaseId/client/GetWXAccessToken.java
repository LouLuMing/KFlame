package com.china.fortune.target.autoIncreaseId.client;

import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.target.autoIncreaseId.action.GetWXGZHAccessTokenAction;

public class GetWXAccessToken {
	private ServerAccess httpConnection = null;
	private String sSqlUrl = ActionToUrl.toUrl(GetWXGZHAccessTokenAction.class);
	public String sTable;
	public String sId;

	public GetWXAccessToken() {
		httpConnection = new ServerAccess("127.0.0.1", 4999);
	}

	public GetWXAccessToken(int iPort) {
		httpConnection = new ServerAccess("127.0.0.1", iPort);
	}

	public GetWXAccessToken(String sServer, int iPort) {
		httpConnection = new ServerAccess(sServer, iPort);
	}

	public String getData(String appId, String appSecret) {
		String id = null;
		UrlBuilder ub = new UrlBuilder(sSqlUrl);
		ub.add("appId", appId);
		ub.add("appSecret", appSecret);
		for (int i = 0; i < 2; i++) {
			JSONObject data = httpConnection.httpGetAndParseData(ub.toString());
			if (data != null) {
				id = data.optString("id");
				break;
			}
		}
		return id;
	}

}
