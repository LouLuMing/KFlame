package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.json.JSONObject;

public class WeiXinUserAccessToken {
	private String AppID = "wx186c3c4daa85acd8";
	private String AppSecret = "19d98b7295f6e21a675b1cc5aa316976";
	private String sWeiXinAcessToken = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private String sWeiXinUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

	public WeiXinUserAccessToken(String id, String secret) {
		AppID = id;
		AppSecret = secret;
	}

	// wxJson.optString("access_token");
	// wxJson.optString("openid");
	public JSONObject getAcessToken(String code) {
		String sUrl = String.format(sWeiXinAcessToken, AppID, AppSecret, code);
		String sRecv = HttpSendAndRecv.doGet(sUrl);
		if (sRecv != null) {
			Log.logClass(sRecv);
			return new JSONObject(sRecv);
		}
		return null;
	}

	// wxJson.optString("headimgurl");
	// wxJson.optString("nickname");
	public JSONObject getUserInfo(String access_token, String openid) {
		String sUrl = String.format(sWeiXinUserInfo, access_token, openid);
		String sRecv = HttpSendAndRecv.doGet(sUrl);
		if (sRecv != null) {
			Log.log(sRecv);
			return new JSONObject(sRecv);
		}
		return null;
	}

	public JSONObject getUserInfo(String code) {
		JSONObject json = getAcessToken(code);
		if (json != null) {
			return getUserInfo(json.optString("access_token"), json.optString("openid"));
		}
		return null;
	}

	public static void main(String[] args) {
		String AppID = "wx72fc038552180296";
		String AppSecret = "6d922b8b506bfed0f64140cb8ade001d";

		WeiXinUserAccessToken wuat = new WeiXinUserAccessToken(AppID, AppSecret);
		JSONObject json = wuat.getUserInfo("071X5IeR0YVsQ522ZYdR0mxReR0X5IeL");
	}
}
