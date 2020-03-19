package com.china.fortune.target.autoIncreaseId;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class AutoIncreaseIdTarget implements TargetInterface {
	public boolean doAction(XmlNode cfg , ProcessAction self) {
		Log.log("AutoIncreaseIdTarget start");
		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		String sMySqlIP = cfg.getChildNodeText("sMySqlUrl", "127.0.0.1");
		String sMySqlUser = cfg.getChildNodeText("sMySqlUser", "root");
		String sMySqlPasswd = cfg.getChildNodeText("sMySqlPasswd", "Hgwl12345!");
		String sMySqlDBName = cfg.getChildNodeText("sMySqlDBName", "game");

		AutoIncreaseIdServer obj = new AutoIncreaseIdServer();
		obj.initMySql(sMySqlIP, sMySqlUser, sMySqlPasswd, sMySqlDBName);
		
		XmlNode tables = cfg.getChildNode("tables");
		if (tables != null) {
			for (int i = 0; i < tables.getChildCount(); i++) {
				XmlNode table = tables.getChildNode(i);
				if (table != null && "table".equals(table.getTag())) {
					String sTable = table.getText();
					String sId = table.getAttrValue("id");
					if (sTable != null && sId != null) {
						obj.addTableAndId(sTable, sId);
					}
				}
			}
		}
		
		XmlNode unicode = cfg.getChildNode("unicode");
		if (unicode != null) {
			for (int i = 0; i < unicode.getChildCount(); i++) {
				XmlNode tag = unicode.getChildNode(i);
				if (tag != null) {
					obj.addUnicodeTag(tag.getText());
				}
			}
		}
		
		XmlNode isnewday = cfg.getChildNode("isnewday");
		if (isnewday != null) {
			for (int i = 0; i < isnewday.getChildCount(); i++) {
				XmlNode tag = isnewday.getChildNode(i);
				if (tag != null) {
					obj.addNewDayTag(tag.getTag(), StringAction.toInteger(tag.getText()));
				}
			}
		}
		
		XmlNode timeout = cfg.getChildNode("timeout");
		if (timeout != null) {
			for (int i = 0; i < timeout.getChildCount(); i++) {
				XmlNode tag = timeout.getChildNode(i);
				if (tag != null) {
					obj.addTimeoutTag(tag.getTag(), StringAction.toLong(tag.getText()));
				}
			}
		}
		
		XmlNode allowIp = cfg.getChildNode("allowIp");
		if (allowIp != null) {
			for (int i = 0; i < allowIp.getChildCount(); i++) {
				XmlNode server = allowIp.getChildNode(i);
				if (server != null && "server".equals(server.getTag())) {
					String sServer = server.getText();
					obj.addAllowServlet(sServer);
				}
			}
		}

		
		obj.startAndBlock(iLocalPort);
		
		Log.log("AutoIncreaseIdTarget waitToStop");
		
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}
}
