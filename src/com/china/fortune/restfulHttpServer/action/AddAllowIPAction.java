package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.property.InterfaceRSAKey;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.secure.RSAAction;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.string.StringAction;

public class AddAllowIPAction extends RestfulStringServlet {
	private String[] lsKey = { "ip", "rsa" };
	public AddAllowIPAction() {
		ksKey.append(lsKey);
		setUrlDecode(true);
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
		String sDecr = RSAAction.decrypt(lsValues[1], InterfaceRSAKey.RSA_Data_Private);
		if (StringAction.compareTo(sDecr, lsValues[0]) == 0) {
			if (lsValues[0].indexOf('.') > 0) {
				IPAllowAction.addTemporaryAllowIP(IPHelper.Ip2Int(lsValues[0]));
			} else {
				IPAllowAction.addTemporaryAllowIP(StringAction.toInteger(lsValues[0]));
			}
			ResultJson.fillData(json, 0, "ok", null);
		} else {
			ResultJson.fillData(json, 1, "RSA验证信息错误", null);
		}
		return RunStatus.isOK;
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		ResultJson.fillData(json, 1, sKey, null);
	}

    static public void addAllowIP(String serverIP, int serverPort, String clientIntIP) {
        ServerAccess httpConnection = new ServerAccess(serverIP, serverPort);
        httpConnection.showLog(true);
        String lsValues[] = new String[2];
        lsValues[0] = clientIntIP;
        lsValues[1] = RSAAction.encrypt(lsValues[0], InterfaceRSAKey.RSA_Data_Public);
        AddAllowIPAction ap = new AddAllowIPAction();
        httpConnection.getAndShow(ap.showUrlParam(ActionToUrl.toUrl(ap.getClass()), lsValues));
    }
}
