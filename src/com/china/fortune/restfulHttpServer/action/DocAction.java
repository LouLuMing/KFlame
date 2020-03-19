package com.china.fortune.restfulHttpServer.action;

import com.china.fortune.data.ResourceUtils;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.webservice.WebServer;

import java.util.ArrayList;
import java.util.HashMap;

public class DocAction extends InterfaceAction {
	private HashMap<String, String> keyNames = new HashMap<String, String>();
	private HashMap<String, String> actionIntroduce = new HashMap<String, String>();
	
	public DocAction(WebServer webServer) {
		super(webServer);
		ResourceUtils.loadIniFileToHashMap("keyNames.ini", keyNames);
		ResourceUtils.loadIniFileToHashMap("actionIntroduce.ini", actionIntroduce);
	}

	protected void appendUrl(StringBuilder sb, String sHost, String sUrl) {
		if (sHost != null) {
			sb.append(sHost);
		}
		sb.append(sUrl);
		sb.append('\n');
	}

	@Override
	protected void buildDoc(StringBuilder sb, String sHost, String sUrl) {
		if (keyNames.size() > 0 && actionIntroduce.size() > 0) {
			String sResource = UrlParam.getResource(sUrl);
			ArrayList<String> lsParam = UrlParam.findKeys(sUrl);
			String sTag = actionIntroduce.get(sResource);
			if (sTag != null) {
				sb.append(actionIntroduce.get(sResource));

				sb.append('\n');
				appendUrl(sb, sHost, sUrl);

				for (String sParam : lsParam) {
					sTag = keyNames.get(sParam);
					if (sTag != null) {
						sb.append(sParam);
						sb.append(':');
						sb.append(sTag);
						sb.append('\n');
					}
				}
				sb.append('\n');
			}
		} else {
			appendUrl(sb, sHost, sUrl);
		}
	}
	
}
