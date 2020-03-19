package com.china.fortune.target.loadClass;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassLoaderUtils;
import com.china.fortune.xml.XmlNode;

public class LoadClassAndRunMainTarget implements TargetInterface {
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		boolean rs = false;
		String sUrl = cfg.getChildNodeText("url");
		String sClass = cfg.getChildNodeText("class");
		Log.logClass(sUrl + ":" + sClass);
		if (sUrl != null && sClass != null) {
			ClassLoaderUtils loader = new ClassLoaderUtils();
			if (loader.loadJar(sUrl)) {
				rs = loader.runMain(sClass, null);
				loader.close();
			}
		}
		return rs;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}
}
