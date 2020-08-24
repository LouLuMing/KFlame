package com.china.fortune.restfulHttpServer.weixin.action;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.os.xml.XmlParser;
import com.china.fortune.restfulHttpServer.weixin.msgevent.MessageRouter;
import com.china.fortune.string.StringUtils;
import com.china.fortune.thirdTools.WeiXinGZH;
import com.china.fortune.xml.XmlNode;

public class MsgRouterAction extends RestfulStringServlet {
	protected String[] lsFields = { "signature", "timestamp", "nonce" };

	protected MessageRouter msgTypeRouter = new MessageRouter("MsgType");
	private String sToken = null;

	public MsgRouterAction(String token) {
		sToken = token;
		ksKey.append(lsFields);
	}

	@Override
	protected void onParamMiss(JSONObject json, String sKey) {
		Log.logClass(sKey);
	}

	@Override
	public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValues) {
		String toSigna = WeiXinGZH.toSignature(sToken, lsValues[1], lsValues[2]);
		if (StringUtils.compareTo(toSigna, lsValues[0]) == 0) {
			byte[] bBody = hReq.getByteBody();
			if (bBody != null && bBody.length > 0) {
				XmlNode recvObj = XmlParser.parse(bBody);
				if (recvObj != null) {
					DbAction dbObj = (DbAction) objForThread;
					XmlNode sendObj = msgTypeRouter.doAction(dbObj, recvObj);
					if (sendObj != null) {
						json.put("xml", sendObj);
					}
				}
			}
		} else {
			Log.logClassError(toSigna + ":" + lsValues[0]);
		}
		return RunStatus.isOK;
	}

	@Override
	public void setJsonToBody(HttpServerRequest hReq, HttpResponse hRes, JSONObject json) {
		XmlNode xmlObj = (XmlNode) json.opt("xml");
		if (xmlObj != null) {
			setHttpBody(hReq, hRes, xmlObj.createXMLNoHead(), "text/xml");
		} else {
			String echostr = UrlParam.findValue(hReq.getResource(), "echostr");
			if (StringUtils.length(echostr) > 0) {
				setHttpBody(hReq, hRes, echostr, "text/plain");
			} else {
				setHttpBody(hReq, hRes, "success", "text/plain");
			}
		}
	}
}
