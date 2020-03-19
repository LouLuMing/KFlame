package com.china.fortune.target.saveAndLoadSrc;

import com.china.fortune.os.common.OsDepend;
import com.china.fortune.target.fileBackupServer.Uploader;

//wget http://115.159.71.91:20000/myAnt.jar
//wget http://115.159.71.91:20000/ojdbc14.jar
public class UpdateJar {
	
	public static void main(String[] args) {
		String sPostUrl = "115.159.71.91";
		String sDesPath = null;
		if (OsDepend.isWin()) {
			sDesPath = "z:\\";
		} else {
			sDesPath = "/Users/zjrcsoft/OneDrive/";
		}
		Uploader obj = new Uploader();
		obj.upload(sPostUrl, 20000, sDesPath, "myAnt.jar");
	}

}
