package com.china.fortune.target.autoIncreaseId.client;

import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.target.autoIncreaseId.action.GetUnicodeAction;

public class GetUnicodeClient {
	private ServerAccess httpConnection = null;
	private String sSqlUrl = ActionToUrl.toUrl(GetUnicodeAction.class);
	public String sTag;
	
	public GetUnicodeClient(String tag) {
		sTag = tag;
		httpConnection = new ServerAccess("127.0.0.1", 4999);
	}
	
	public GetUnicodeClient(int iPort, String tag) {
		sTag = tag;
		httpConnection = new ServerAccess("127.0.0.1", iPort);
	}

	public GetUnicodeClient(String sServer, int iPort, String tag) {
		sTag = tag;
		httpConnection = new ServerAccess(sServer, iPort);
	}
	
	public String getUnicode() {
		String id = null;
		UrlBuilder ub = new UrlBuilder(sSqlUrl);
		ub.add("tag", sTag);
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
