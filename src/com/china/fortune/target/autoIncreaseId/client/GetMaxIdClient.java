package com.china.fortune.target.autoIncreaseId.client;

import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.target.autoIncreaseId.action.GetAutoIncreaseIdAction;

public class GetMaxIdClient {
	private ServerAccess httpConnection = null;
	private String sSqlUrl = ActionToUrl.toUrl(GetAutoIncreaseIdAction.class);
	public String sTable;
	public String sId;

	public GetMaxIdClient() {
		httpConnection = new ServerAccess("127.0.0.1", 4999);
	}
	
	public GetMaxIdClient(int iPort) {
		httpConnection = new ServerAccess("127.0.0.1", iPort);
	}
	
	public GetMaxIdClient(String sServer, int iPort) {
		httpConnection = new ServerAccess(sServer, iPort);
	}
	
	public void loadMaxId(Class<?> cls, String id) {
		sTable = cls.getSimpleName();
		sId = id;
	}
	
	public void loadMaxId(String table, String id) {
		sTable = table;
		sId = id;
	}

	public int getMaxId(String table, String field) {
		int id = -1;
		UrlBuilder ub = new UrlBuilder(sSqlUrl);
		ub.add("table", table);
		ub.add("id", field);
		for (int i = 0; i < 2; i++) {
			JSONObject data = httpConnection.httpGetAndParseData(ub.toString());
			if (data != null) {
				id = data.optInt("id");
				break;
			}
		}
		return id;
	}
	
	public int getMaxId() {
		int id = -1;
		UrlBuilder ub = new UrlBuilder(sSqlUrl);
		ub.add("table", sTable);
		ub.add("id", sId);
		for (int i = 0; i < 2; i++) {
			JSONObject data = httpConnection.httpGetAndParseData(ub.toString());
			if (data != null) {
				id = data.optInt("id");
				break;
			}
		}
		return id;
	}
}
