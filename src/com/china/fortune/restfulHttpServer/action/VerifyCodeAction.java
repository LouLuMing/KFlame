package com.china.fortune.restfulHttpServer.action;

import java.util.concurrent.ConcurrentHashMap;

import com.china.fortune.global.Log;
import com.china.fortune.graphics.VerifyCode;
import com.china.fortune.graphics.VerifyCodeImage;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.string.StringAction;
import com.china.fortune.timecontrol.TimeoutMapActionThreadSafe;

public class VerifyCodeAction extends RestfulStringServlet {
	private String[] lsKey = { "id" };

	private TimeoutMapActionThreadSafe<String, String> tmats = new TimeoutMapActionThreadSafe<String, String>(2, 17) {
		@Override
		public void onTimeout(ConcurrentHashMap<String, String> map) {
			map.clear();
		}
	};
	
	public VerifyCodeAction() {
		ksKey.append(lsKey);
	}
	
	public boolean checkVerifyCode(String sPhone, String verifyCode) {
		tmats.checkTimeout();
		String sCode = tmats.get(sPhone);
		Log.logClass(sPhone + ":" + sCode + ":" + verifyCode);
		if (StringAction.compareToIgnoreCase(sCode, verifyCode) == 0) {
			tmats.remove(sPhone);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
		json.put("id", lsValues[0]);
		return RunStatus.isOK;
	}

	@Override
	public void setJsonToBody(HttpServerRequest hReq, HttpResponse hRes, JSONObject json) {
		String verifyCode = VerifyCode.generateVerifyCode(4);
		byte[] bBody = VerifyCodeImage.outputImage(200, 80, verifyCode);
		hRes.setBody(bBody);
		hRes.setContentType(HttpProp.getContentType("jpg"));
		String id = json.optString("id");
		if (StringAction.length(id) > 0) {
			tmats.checkTimeout();
			tmats.add(id, verifyCode);
		}
		Log.logClass(id + ":" + verifyCode);
	}
	
	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

}
