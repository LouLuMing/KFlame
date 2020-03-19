package com.china.fortune.restfulHttpServer;

import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;

public class ResultJson {
	static public String sJsonOK = null;
	static public String sJsonNologin = null;
	static public String sJsonRelogin = null;
	static public String sJsonNotFoundResource = null;
	static public String sJsonNoPermission = null;
	static public String sJsonException = null;
	static public String sJsonFrequentAccessLimit = null;
	
	static public String sOK = "ok";
	static public String sNologin = "用户未登录";
	static public String sTokenInvalid = "用户token无效";
	static public String sRelogin = "请重新登录";
	static public String sNotFoundResource = "Url资源未找到";
	static public String sException = "服务器内部错误";
	static public String sNoPermission = "您没有权限调用该接口";
	static public String sActionPause = "该接口暂时不能调用";
	static public String sFrequentAccessLimit = "请不要频繁调用接口";
	
	static public void fillRelogin(JSONObject json) {
		fillData(json, 99, sRelogin, null);
	}
	
	static public void fillFrequentAccessLimit(JSONObject json) {
		fillData(json, 1, sFrequentAccessLimit, null);
	}
	
	static public void fillOK(JSONObject json) {
		fillData(json, 0, sOK, null);
	}

	static public void fillOK(JSONObject json, JSONObject data) {
		fillData(json, 0, sOK, data);
	}

	static public void fillError(JSONObject json, String sError) {
		fillData(json, 1, sError, null);
	}
    static public void fillError(JSONObject json, int retCode, String sError) {
        fillData(json, retCode, sError, null);
    }
	static {
		JSONObject json = new JSONObject();
		fillData(json, 0, sOK, null);
		sJsonOK = json.toString();

		json = new JSONObject();
		fillData(json, 99, sNologin, null);
		sJsonNologin = json.toString();
		
		json = new JSONObject();
		fillData(json, 99, sRelogin, null);
		sJsonRelogin = json.toString();
		
		json = new JSONObject();
		fillData(json, 1, sNotFoundResource, null);
		sJsonNotFoundResource = json.toString();

		json = new JSONObject();
		fillData(json, 1, sException, null);
		sJsonException = json.toString();

		json = new JSONObject();
		fillData(json, 1, sNoPermission, null);
		sJsonNoPermission = json.toString();
		
		json = new JSONObject();
		fillData(json, 1, sFrequentAccessLimit, null);
		sJsonFrequentAccessLimit = json.toString();
	}

	static public void fillData(JSONObject json, int retcode, String message) {
		json.put("retcode", retcode);
		json.put("message", message);
	}

	static public void fillData(JSONObject json, int retcode, String message, JSONObject data) {
		json.put("retcode", retcode);
		json.put("message", message);
		if (data != null && data.size() > 0) {
			json.put("data", data);
		}
	}
	
	static public String getString(JSONObject json, String skey) {
		String sValue = null;
		if (json.optInt("retcode") == 0) {
			JSONObject data = json.optJSONObject("data");
			if (data != null) {
				sValue = data.optString(skey);
			}
		}
		return sValue;
	}
	
	static public int getInt(JSONObject json, String skey) {
		int iValue = -1;
		if (json.optInt("retcode") == 0) {
			JSONObject data = json.optJSONObject("data");
			if (data != null) {
				iValue = data.optInt(skey);
			}
		}
		return iValue;
	}
	
	static public JSONArray getList(JSONObject json) {
		JSONObject data = json.optJSONObject("data");
		if (data != null) {
			return data.optJSONArray("list");
		}
		return null;
	}
}
