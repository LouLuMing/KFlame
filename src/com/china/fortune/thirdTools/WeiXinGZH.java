package com.china.fortune.thirdTools;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.http.HttpUtils;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.http.httpHead.HttpFormData;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.secure.Digest;
import com.china.fortune.string.FastJSONParser;
import com.china.fortune.string.StringUtils;
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

		String sRecv = HttpUtils.get(sb.toString());
		Log.logClass(sRecv);
		access_token = FastJSONParser.getStringValue(sRecv, "access_token");
		if (access_token != null) {
			int expires_in = StringUtils.toInteger(FastJSONParser.getValue(sRecv, "expires_in"));
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
		String sJson = FileUtils.readSmallFile("weixin.json", "utf-8");
		String sRecv = HttpUtils.post(sUrl, sJson);
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
		String sRecv = HttpUtils.post(sUrl, json.toString());
		Log.logClass(sRecv);
		JSONObject recvObj = new JSONObject(sRecv);
		return recvObj.optInt("errcode") == 0;
	}

	public boolean sendMsgTemplate(String sJson) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getAccessToken();
		String sRecv = HttpUtils.post(sUrl, sJson);
		Log.logClass(sRecv);
		JSONObject recvObj = new JSONObject(sRecv);
		return recvObj.optInt("errcode") == 0;
	}

	public boolean createMenu(String sMenu) {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getAccessToken();
		String sRecv = HttpUtils.post(sUrl, sMenu);
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
		String sRecv = HttpUtils.post(sUrl, json.toString());
		Log.logClass(sRecv);
		return sRecv;
	}

	public String getJsapiTicket() {
		String sUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + getAccessToken();
		String sRecv = HttpUtils.get(sUrl);
		Log.logClass(sRecv);
		return FastJSONParser.getStringValue(sRecv, "ticket");
	}

	static public String toSignature(String token, String timestamp, String nonce) {
		String tmp = null;
		if (StringUtils.compareTo(token, timestamp) > 0) {
			tmp = token;
			token = timestamp;
			timestamp = tmp;
		}
		StringBuilder sb = new StringBuilder();
		if (StringUtils.compareTo(token, nonce) > 0) {
			sb.append(nonce);
			sb.append(token);
			sb.append(timestamp);
		} else if (StringUtils.compareTo(timestamp, nonce) > 0) {
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
				if (StringUtils.compareTo(item.optString("media_id"), MediaId) == 0) {
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
		String sRecv = HttpUtils.post(sUrl, json.toString());
	}

	public String getMedia(String mediaId) {
		UrlBuilder ub = new UrlBuilder("https://api.weixin.qq.com/cgi-bin/media/get");
		ub.add("access_token", getAccessToken());
		ub.add("media_id", mediaId);
		return ub.toString();
	}

	public String postMedia(String sFile) {
		UrlBuilder ub = new UrlBuilder("https://api.weixin.qq.com/cgi-bin/media/upload");
		ub.add("access_token", getAccessToken());
		ub.add("type", "image");
		HttpFormData hfr = new HttpFormData(ub.toString());
		hfr.addFileBlock(sFile);
		hfr.addEndLine();
		HttpClient hc = new HttpClient();
		HttpResponse hs = hc.execute(hfr);
		if (hs != null) {
			Log.logNoDate(hs.toString());
			Log.logNoDate(hs.getBody());
		}
		return null;
	}
	public static void main(String[] args) {
//		WeiXinGZH wt = new WeiXinGZH("wx2cfc382e4b9762b3", "d77399321cdf2a565264d83113a9ba86");
		WeiXinGZH wt = new WeiXinGZH("wx221881c7731050ac", "579d430f4c62a76d84961dd67e7da0ee");
		//wt.batchgetMaterial("image", 0, 99);
		//wt.postMedia("z:\\1.png");
		//String sRecv = wt.getMedia("5vh7MnOyCg-YWJPDwfkzkOHFyq7BMwwjUk9BRlGBlwN7xrUHrxdWWc3IDee6w5bp");
		String sRecv = wt.getMedia("1237378768e7q8e7r8qwesafdasdfasdfaxss111");
		Log.logNoDate(sRecv);
		//Log.logNoDate(wt.getMedia("1231"));
		//wt.createWXAQRCode("/hello", 250);
		// wt.getMaterial(ResFolder.getFile("data.json"),
		// "qCmt6J0M3C-CtoLIJTKTtUhKmJ2Ww-VsX6GA3TiGRf8");
		// Log.logClass(wt.getAccessToken());
		//
		// Log.logClass(toSignature("hgwl", "1524793605", "567378027"));
		// Log.logClass(wt.getJsapiTicket());
		//wt.batchgetMaterial("news", 0, 10);
		// Log.logClass(toJSSignature("sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg",
		// "Wm3WZYTPz0wzccnW", "1414587457", "http://mp.weixin.qq.com?params=value"));
	}
}
