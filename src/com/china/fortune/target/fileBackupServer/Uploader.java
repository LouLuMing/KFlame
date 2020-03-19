package com.china.fortune.target.fileBackupServer;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;

public class Uploader {
	private String sUrlPath = "/home";

	public Uploader() {
	}

	public Uploader(String sPath) {
		if (sPath.startsWith("/")) {
			sUrlPath = sPath;
		} else {
			sUrlPath = "/" + sPath;
		}
	}

	public void upload(String sServer, int iPort, String sPath, String jFile) {
		String sDesPath = sPath + jFile;
		if (FileHelper.isExists(sDesPath)) {
			String sPostUrl = "http://" + sServer + ":" + iPort + sUrlPath + "?file=" + jFile;
			String sResource = HttpSendAndRecv.postFile(sPostUrl, sDesPath);
			if (sResource != null) {
				Log.log("OK:" + "http://" + sServer + ":" + iPort + sResource);
			} else {
				Log.logError("Error:" + sPostUrl);
			}
		} else {
			Log.logError("Error:" + sDesPath);
		}
	}

}
