package com.china.fortune.target.simpleWebTarget;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.socket.ReadLineBuffer;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class SimpleWebTarget implements TargetInterface {

	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		Log.log("WebServerTarget start");

		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		Log.hideClasss(ReadLineBuffer.class);
		SimpleWebServer obj = new SimpleWebServer();
		Log.log("WebServer start");
		if (obj.startAndBlock(iLocalPort)) {

		}
		Log.log("WebServer waitToStop");
		Log.log("WebServerTarget waitToStop");

		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		// TODO Auto-generated method stub
		return null;
	}

}
