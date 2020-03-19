package com.china.fortune.messageServer;

import com.china.fortune.os.log.FileLogSwitchAction;

public class MessageLog {
	static private FileLogSwitchAction obj = new FileLogSwitchAction();

	public void clearHistory(int days) {
		obj.clearHistory(days);
	}
	
	static {
		obj.setLog("log", "myMsg");
	}
	
	static public void showLog(boolean bShow) {
		obj.showLog(bShow);
	}

	static public void setLog(String sDir, String sFile) {
		obj.setLog(sDir, sFile);
	}

	static public void log(String sText) {
		obj.log(sText);
	}
}
