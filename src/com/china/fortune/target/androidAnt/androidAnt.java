package com.china.fortune.target.androidAnt;

import com.china.fortune.file.FileHelper;
import com.china.fortune.file.ReadLinesInteface;
import com.china.fortune.global.Log;
import com.china.fortune.os.shell.RunShell;
import com.china.fortune.string.FastXmlParser;

// all file utf-8
// channel name put in strings.xml
// <string name="channel_name">360</string>
// apkname define, build.xml
// <property name="zipalign-package" value="xxx.apk"/>
public class androidAnt {
	static private String sCmdAnt = "D:\\Ant\\bin\\ant.bat";

	public void changeValue(String sFile, String sTag, String sAttr, String sValue) {
		String sData = FileHelper.readSmallFile(sFile, "utf-8");
		sData = FastXmlParser.setAttrValue(sData, sTag, sAttr, sValue);
		FileHelper.writeSmallFile(sFile, sData, "utf-8");
	}

	public void changeElement(String sFile, String sAttr, String sValue) {
		String sData = FileHelper.readSmallFile(sFile, "utf-8");
		sData = FastXmlParser.setElement(sData, sAttr, sValue);
		FileHelper.writeSmallFile(sFile, sData, "utf-8");
	}

	public void changeManifestChannel(String sFile, String sValue) {
		String sData = FileHelper.readSmallFile(sFile, "utf-8");
		int iChannel = sData.indexOf("UMENG_CHANNEL");
		if (iChannel > 0) {
			int iKey = sData.indexOf("android:value", iChannel);
			if (iKey > 0) {
				int iLeftTagEnd = sData.indexOf('"', iKey);
				if (iLeftTagEnd > 0) {
					int iTagEnd = sData.indexOf('"', iLeftTagEnd + 1);
					if (iTagEnd > 0) {
						StringBuilder sb = new StringBuilder();
						sb.append(sData.substring(0, iLeftTagEnd + 1));
						sb.append(sValue);
						sb.append(sData.substring(iTagEnd, sData.length()));
						FileHelper.writeSmallFile(sFile, sb.toString(), "utf-8");
					}
				}
			}
		}
		
	}
	
	public void excuteAnt(String cCmd, String sPath) {
		ReadLinesInteface rli = new ReadLinesInteface() {
			@Override
			public boolean onRead(String sLine) {
				Log.log(sLine);
				return true;
			}
		};
		RunShell.winRun(cCmd, sPath, rli);
	}

	public void packetAndroid(String sPath, String sChannel) {
		String sStrings = sPath + "AndroidManifest.xml";
		String sBuilderFile = "BuildLLM.xml";
		String sBuild = sPath + sBuilderFile;
		changeManifestChannel(sStrings, sChannel);
		changeValue(sBuild, "project", "name", "yygh-" + sChannel);
		excuteAnt(sCmdAnt + " -buildfile " + sBuilderFile, sPath);
	}

	public static void main(String[] args) {
		androidAnt obj = new androidAnt();
		String sPath = "E:\\Code\\Android\\Yygh5\\ZJyygh\\";
		String sChannel[] = new String[] { "12580网站", "TengXun108", "百度应用", "360市场", "小米市场", "豌豆荚", "安智市场","huawei","机锋市场" };
//		String sChannel[] = new String[] { "机锋市场" };
		
		for (int i = 0; i < sChannel.length; i++) {
			obj.packetAndroid(sPath, sChannel[i]);
		}
	}
}
