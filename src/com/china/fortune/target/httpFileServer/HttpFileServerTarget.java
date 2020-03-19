package com.china.fortune.target.httpFileServer;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class HttpFileServerTarget implements TargetInterface {
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		Log.logClass("HttpFileServer start");

		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		HttpFileServer obj = new HttpFileServer();

		String sFolder = cfg.getChildNodeText("folder");
		if (StringAction.length(sFolder) > 0) {
			obj.setRootPath(PathUtils.getFullPath(sFolder));
		}

		XmlNode resources = cfg.getChildNode("resources");
		if (resources != null) {
			for (int i = 0; i < resources.getChildCount(); i++) {
				XmlNode resource = resources.getChildNode(i);
				if (resource != null && "resource".equals(resource.getTag())) {
					String sResource = resource.getText();
					if (sResource != null) {
						if (sResource.equals(".")) {
							obj.addHttpFileAction(new HttpFileAction(".", "/home"));
						} else {
							obj.addHttpFileAction(new HttpFileAction(sResource, "/" + sResource));
						}
					}
				}
			}
		}

		if (obj.start(iLocalPort)) {
			Log.logClass("HttpFileServer start");

			while (self == null || !self.isFinish()) {
				ThreadUtils.sleep(1000);
			}

			obj.stop();
			Log.logClass("HttpFileServer waitToStop");
		}

		Log.logClass("HttpFileServer waitToStop");

		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		// TODO Auto-generated method stub
		return null;
	}

}
