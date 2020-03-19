package com.china.fortune.target.autoIncreaseId.action;

import java.util.HashMap;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.thirdTools.WeiXinGZH;

public class GetWXGZHAccessTokenAction extends RestfulStringServlet {
	private String[] lsKey = { "appId", "appSecret" };

	private HashMap<String, WeiXinGZH> mapAccessToken = new HashMap<String, WeiXinGZH>();

	public GetWXGZHAccessTokenAction() {
		ksKey.append(lsKey);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValue) {
		JSONObject data = new JSONObject();
		synchronized (this) {
			WeiXinGZH wx = mapAccessToken.get(lsValue[0]);
			if (wx == null) {
				wx = new WeiXinGZH(lsValue[0], lsValue[1]);
				mapAccessToken.put(lsValue[0], wx);
			}
			if (wx != null) {
				data.put("id", wx.getAccessToken());
			}
		}
		ResultJson.fillData(json, 0, "ok", data);

		return RunStatus.isOK;
	}

}
