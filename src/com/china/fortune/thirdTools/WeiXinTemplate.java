package com.china.fortune.thirdTools;

import com.china.fortune.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeiXinTemplate {
	private class WeiXinData {
		public String key;
		public String value;
		public String color;
	}

	private class MiniProgram {
		public String appid;
		public String pagepath;
	}

	private ArrayList<WeiXinData> lsData = new ArrayList<WeiXinData>();
	private String touser;
	private String template_id;
	private String url;
	private MiniProgram mp = new MiniProgram();

	public void init(String to, String id, String u) {
		touser = to;
		template_id = id;
		url = u;
	}
	
	public void setMiniProgram(String appid, String pagepath) {
		mp.appid = appid;
		mp.pagepath = pagepath;
	}
	
	public void addData(String key, String value) {
		WeiXinData wxd = new WeiXinData();
		wxd.key = key;
		wxd.value = value;
		lsData.add(wxd);
	}
	
	public void addData(String key, String value, String color) {
		WeiXinData wxd = new WeiXinData();
		wxd.key = key;
		wxd.value = value;
		wxd.color = color;
		lsData.add(wxd);
	}

	public String toString() {
		JSONObject json = new JSONObject();

		json.put("touser", touser);
		json.put("template_id", template_id);
		json.put("url", url);

		if (mp.appid != null) {
			JSONObject miniprogram = new JSONObject();
			json.put("miniprogram", miniprogram);
			miniprogram.put("appid", mp.appid);
			miniprogram.put("pagepath", mp.pagepath);
		}
		
		JSONObject data = new JSONObject();
		json.put("data", data);

		for (WeiXinData wxd : lsData) {
			JSONObject item = new JSONObject();
			item.put("value", wxd.value);
			item.put("color", wxd.color);
			data.put(wxd.key, item);
		}

		return json.toString();
	}

	public static void main(String[] args) {
		String openid = "ofWZ6uAEzm8w00M2i2XwoNpP1ciU";
		String tmpId = "wHAbtan8Q8OCWWqL02HQ--DrR4AATUnsqHpbMjHnWvM";
		WeiXinTemplate wt = new WeiXinTemplate();
		wt.init(openid, tmpId, "http://m.baidu.com");
		HashMap<String, String> mapValues = new HashMap<String, String>();
		mapValues.put("KEY1", "KEY1");
		mapValues.put("KEY2", "KEY2");
		mapValues.put("KEY3", "KEY3");
		for (Map.Entry<String, String> item : mapValues.entrySet()) {
			wt.addData(item.getKey(), item.getValue());
		}

		WeiXinGZH wxg = new WeiXinGZH("wx0824b15ef98cac0d",  "412f3c2526372ddfdfeaec6f0deae234");
		wxg.sendMsgTemplate(wt.toString());

	}
}
