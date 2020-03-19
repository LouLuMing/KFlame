package com.china.fortune.os.log;

import com.china.fortune.os.common.FileLogAction;

public class FileLogSwitchAction extends FileLogAction {
	private boolean bShowLog = true;
	public FileLogSwitchAction(String sDir, String sFile) {
		setLog(sDir, sFile);
	}

	public FileLogSwitchAction() {
	}
	
	public void showLog(boolean bShow) {
		bShowLog = bShow;
		closeFile();
	}

	@Override
	public void log(String sText) {
		if (bShowLog) {
			super.log(sText);
		}
	}
}
