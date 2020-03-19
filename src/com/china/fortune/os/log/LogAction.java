package com.china.fortune.os.log;

import com.china.fortune.common.DateAction;
import com.china.fortune.os.common.FileLogAction;

public class LogAction {
	static public final int iNull = 0;
	static public final int iConsole = 0x01;
	static public final int iFile = 0x02;
	static private final int iMaxLogLength = 1024 * 64;

	protected FileLogAction fileObj = new FileLogAction();

	public void clearHistory(int days) {
		fileObj.clearHistory(days);
	}
	
	private void logConsole(String sText) {
		String sMsg = DateAction.getTime() + " " + sText + "\n";
		System.out.print(sMsg);
	}

	private void logNoDateConsole(String sText) {
		String sMsg = sText + "\n";
		System.out.print(sMsg);
	}

	// 0 null
	// 1 console
	// 2 file
	private int iType = iConsole;

	public int getLogType() {
		return iType;
	}

	public void init(int type) {
		if ((iType & iFile) == iFile) {
			fileObj.closeFile();
		}
		iType = type;
		if (iType < 0 || iType > 3) {
			iType = 0;
		}
	}

	public boolean isShow() {
		return (iType != iNull);
	}

	public void setLog(String sDir, String sFile) {
		fileObj.setLog(sDir, sFile);
	}

	public void log(String sText) {
		if (sText != null) {
			// log limit
			String sMsg = null;
			if (sText.length() > iMaxLogLength) {
				sMsg = sText.substring(0, iMaxLogLength) + "...";
			} else {
				sMsg = sText;
			}
			// log end
			if ((iType & iConsole) == iConsole) {
				logConsole(sMsg);
			}
			if ((iType & iFile) == iFile) {
				fileObj.log(sMsg);
			}
		}
	}

	public void logNoDate(String sText) {
		if (sText != null) {
			// log limit
			String sMsg = null;
			if (sText.length() > iMaxLogLength) {
				sMsg = sText.substring(0, iMaxLogLength) + "...";
			} else {
				sMsg = sText;
			}
			// log end
			if ((iType & iConsole) == iConsole) {
				logNoDateConsole(sMsg);
			}
			if ((iType & iFile) == iFile) {
				fileObj.logNoDate(sMsg);
			}
		}
	}
}
