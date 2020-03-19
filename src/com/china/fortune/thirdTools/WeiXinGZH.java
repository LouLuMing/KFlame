package com.china.fortune.thirdTools;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.secure.Digest;
import com.china.fortune.string.FastJSONParser;
import com.china.fortune.string.StringAction;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

//java -cp myAnt.jar com.china.fortune.target.thirdTools.WeiXinGZH
public class WeiXinGZH {
	private String access_token = null;
	private TimeoutAction ta = new TimeoutAction();
	private String appID = null;
	private String appSecret = null;

	public WeiXinGZH(String id, String secret) {
		appID = id;
		appSecret = secret;
	}

	synchronized private void fetchToken() {
		String sHead = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=";
		String sTail = "&secret=";
		StringBuilder sb = new StringBuilder();
		sb.append(sHead);
		sb.append(appID);
		sb.append(sTail);
		sb.append(appSecret);

		String sRecv = HttpSendAndRecv.doGet(sb.toString());
		Log.logClass(sRecv);
		access_token = FastJSONParser.getStringValue(sRecv, "access_token");
		if (access_token != null) {
			int expires_in = StringAction.toInteger(FastJSONParser.getValue(sRecv, "expires_in"));
			if (expires_in > 0) {
				ta.setWaitTimeAndStart((expires_in - 60) * 1000);
			} else {
				ta.setWaitTimeAndStart(3600 * 1000);
			}
		}
	}

	public String getAccessToken() {
		if (ta.isTimeout() || access_token == null) {
			fetchToken();
		}
		return access_token;
	}

	public boolean createMenu() {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getAccessToken();
		String sJson = FileHelper.readSmallFile("weixin.json", "utf-8");
		String sRecv = HttpSendAndRecv.doPost(sUrl, sJson);
		Log.logClass(sRecv);
		JSONObject json = new JSONObject(sRecv);
		return json.optInt("errcode") == 0;
	}

	public boolean sendMsgCustom(String openid, String content) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + getAccessToken();
		JSONObject json = new JSONObject();
		json.put("touser", openid);
		json.put("msgtype", "text");
		JSONObject text = new JSONObject();
		text.put("content", content);
		json.put("text", text);
		String sRecv = HttpSendAndRecv.doPost(sUrl, json.toString());
		Log.logClass(sRecv);
		JSONObject recvObj = new JSONObject(sRecv);
		return recvObj.optInt("errcode") == 0;
	}

	public boolean sendMsgTemplate(String sJson) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getAccessToken();
		String sRecv = HttpSendAndRecv.doPost(sUrl, sJson);
		Log.logClass(sRecv);
		JSONObject recvObj = new JSONObject(sRecv);
		return recvObj.optInt("errcode") == 0;
	}

	public boolean createMenu(String sMenu) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getAccessToken();
		String sRecv = HttpSendAndRecv.doPost(sUrl, sMenu);
		Log.logClass(sRecv);
		JSONObject recvObj = new JSONObject(sRecv);
		return recvObj.optInt("errcode") == 0;
	}

	public String batchgetMaterial(String type, int offset, int count) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + getAccessToken();
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("offset", offset);
		json.put("count", count);
		String sRecv = HttpSendAndRecv.doPost(sUrl, json.toString());
		Log.logClass(sRecv);
		return sRecv;
	}

	public String getJsapiTicket() {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + getAccessToken();
		String sRecv = HttpSendAndRecv.doGet(sUrl);
		Log.logClass(sRecv);
		return FastJSONParser.getStringValue(sRecv, "ticket");
	}

	static public String toSignature(String token, String timestamp, String nonce) {
		String tmp = null;
		if (StringAction.compareTo(token, timestamp) > 0) {
			tmp = token;
			token = timestamp;
			timestamp = tmp;
		}
		StringBuilder sb = new StringBuilder();
		if (StringAction.compareTo(token, nonce) > 0) {
			sb.append(nonce);
			sb.append(token);
			sb.append(timestamp);
		} else if (StringAction.compareTo(timestamp, nonce) > 0) {
			sb.append(token);
			sb.append(nonce);
			sb.append(timestamp);
		} else {
			sb.append(token);
			sb.append(timestamp);
			sb.append(nonce);
		}
		String signature = Digest.getSHA(sb.toString());
		return signature;
	}

	static public String toJSSignature(String jsapi_ticket, String noncestr, String timestamp, String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("jsapi_ticket=");
		sb.append(jsapi_ticket);
		sb.append("&noncestr=");
		sb.append(noncestr);
		sb.append("&timestamp=");
		sb.append(timestamp);
		sb.append("&url=");
		sb.append(url);
		return Digest.getSHA(sb.toString());
	}

	public JSONArray getMaterial(String MediaId) {
		for (int i = 0; i < 2; i++) {
			String sRecv = batchgetMaterial("news", 0, 10);
			JSONObject json = new JSONObject(sRecv);
			if (json.optInt("errcode") == 0) {
				return getMaterial(json, MediaId);
			} else {
				fetchToken();
			}
		}
		return null;
	}

	public JSONArray getMaterial(JSONObject json, String MediaId) {
		JSONArray news = json.optJSONArray("item");
		if (news != null) {
			for (int i = 0; i < news.length(); i++) {
				JSONObject item = news.optJSONObject(i);
				if (StringAction.compareTo(item.optString("media_id"), MediaId) == 0) {
					JSONObject content = item.optJSONObject("content");
					if (content != null) {
						return content.optJSONArray("news_item");
					}
				}
			}
		}
		return null;
	}

	public void createWXAQRCode(String path, int width) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode&access_token=" + getAccessToken();
		JSONObject json = new JSONObject();
		json.put("path", path);
		json.put("width", width);
		String sRecv = HttpSendAndRecv.doPost(sUrl, json.toString());
	}

	public static void main(String[] args) {
		WeiXinGZH wt = new WeiXinGZH("wx2cfc382e4b9762b3", "d77399321cdf2a565264d83113a9ba86");
		wt.createWXAQRCode("/hello", 250);
		// wt.getMaterial(ResFolder.getFile("data.json"),
		// "qCmt6J0M3C-CtoLIJTKTtUhKmJ2Ww-VsX6GA3TiGRf8");
		// Log.logClass(wt.getAccessToken());
		// wt.batchgetMaterial("image", 0, 99);
		// Log.logClass(toSignature("hgwl", "1524793605", "567378027"));
		// Log.logClass(wt.getJsapiTicket());
		wt.batchgetMaterial("news", 0, 10);
		// Log.logClass(toJSSignature("sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg",
		// "Wm3WZYTPz0wzccnW", "1414587457", "http://mp.weixin.qq.com?params=value"));
	}
}
