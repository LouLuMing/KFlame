package com.china.fortune.myant;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringUtils;

public class MyAnt {
	static public void main(String[] args) {
		String sCfg = null;

		// FileHelper.delete("myAnt.jar");

		// Log.addShowTag(Log.sNormal);
		// Log.addShowTag(Log.sError);
		// Log.allowAllClass(false);
		// Log.addShowClasss(SaveOfflineMessage.class);
		
		if (args != null && args.length > 0) {
			sCfg = args[0];
		} else {
			sCfg = PathUtils.getCurrentDataPath(true) + "myAnt.xml";
		}
		MyAntController myAntController = new MyAntController();
		myAntController.startAndBlock(sCfg);
	}

	static public String getString(String sTarget, String sKey) {
		String sValue = null;
		String sFile = PathUtils.getCurrentDataPath(true) + "myAnt.xml";
		String sXml = FileUtils.readSmallFile(sFile, "utf-8");
		if (sXml != null) {
			int iTarget = sXml.indexOf(sTarget);
			if (iTarget < 0) {
				iTarget = 0;
			}
			sValue = StringUtils.findBetween(sXml, iTarget, sKey, sKey);
			Log.logClass(sTarget + ":" + sKey + ":" + sValue);
		} else {
			Log.logClass(sFile + ":miss");
		}
		return sValue;
	}
	
	static public int getInt(String sTarget, String sKey) {
		int iValue = -1;
		String sValue = getString(sTarget, sKey);
		if (sValue != null) {
			iValue = StringUtils.toInteger(sValue);
		}
		return iValue;
	}
}
