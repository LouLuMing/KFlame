package com.china.fortune.target.readServer;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class ReadServerTarget  implements TargetInterface {
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		Log.log("ReadServerTarget start");
		
		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		int iThread = StringAction.toInteger(cfg.getChildNodeText("ithread"));
		ReadServer obj = new ReadServer();
		if (obj.openAndStart(iLocalPort)) {
			Log.log("ReadServerTarget init");
			obj.join();
			obj.stop();
			Log.log("ReadServerTarget deinit");
		}
		
		Log.log("ReadServerTarget waitToStop");
		
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}
}
