package com.china.fortune.target.saveAndLoadSrc;

import com.china.fortune.compress.UnZip;
import com.china.fortune.compress.ZipCompressor;
import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringAction;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

import java.util.ArrayList;

public class SaveAndLoadSrc {
	private String sZipFilePath = PathUtils.getCurrentDataPath(true);
	private String sSrcFilePath = PathUtils.getCurrentDataPath(true) + "src";

	public SaveAndLoadSrc(String zipPath) {
		if (StringAction.length(zipPath) > 0) {
			sZipFilePath = PathUtils.addSeparator(zipPath);
			PathUtils.create(sZipFilePath);
		}
	}
	
	public SaveAndLoadSrc(String zipPath, String srcPath) {
		if (StringAction.length(zipPath) > 0) {
			sZipFilePath = PathUtils.addSeparator(zipPath);
			PathUtils.create(sZipFilePath);
		}
		if (StringAction.length(srcPath) > 0) {
			sSrcFilePath = PathUtils.addSeparator(srcPath);
		}
	}

	public boolean isInSamePath() {
		return StringAction.compareTo(sSrcFilePath, sZipFilePath) == 0;
	}
	
	
	public String saveSrc(String sPostUrl) {
		TimeoutAction ta = new TimeoutAction();
		ta.start();
        Log.log("saveSrc:" + sPostUrl);
		String sDesFile = sZipFilePath + "src.zip";
		FileHelper.delete(sDesFile);
		ZipCompressor zip = new ZipCompressor(sDesFile);
		
		zip.compress(sSrcFilePath);
        Log.log("saveSrc:compress:" + sDesFile);

		String sResource = HttpSendAndRecv.postFile(sPostUrl, sDesFile);
		if (StringAction.length(sResource) > 0) {
            Log.log("saveSrc:postFile:" + sResource + ":" + ta.getMilliseconds());
		} else {
			Log.log("saveSrc:postFile:Error:" + sPostUrl);
		}

		return sResource;
	}

	public boolean loadSrc(String sGetUrl) {
		TimeoutAction ta = new TimeoutAction();
		ta.start();

		String sDesFile = sZipFilePath + "src.zip";
		FileHelper.delete(sDesFile);
		boolean rs = HttpSendAndRecv.getFile(sGetUrl, sDesFile);
		if (rs) {
			Log.log("loadSrc:" + sGetUrl + ":" + sDesFile);
		} else {
			Log.log("loadSrc:Error:" + sGetUrl);
		}
		Log.log("loadSrc:" + ta.getMilliseconds());
		return rs;
	}

	public void zipNoFolder(String sFile) {
		String sDesFile = sZipFilePath + sFile;
		FileHelper.delete(sDesFile);
		ZipCompressor zip = new ZipCompressor(sDesFile);
		ArrayList<String> lsFiles = new ArrayList<String>();
		PathUtils.getAllFile(sSrcFilePath, lsFiles);
		zip.compress(lsFiles);
		Log.log(sDesFile);
	}

	public void zip(String sFile) {
		String sDesFile = sZipFilePath + sFile;
		FileHelper.delete(sDesFile);
		ZipCompressor zip = new ZipCompressor(sDesFile);
		zip.compress(sSrcFilePath);
		Log.log(sDesFile);
	}

	public void unZip() {
		String sDesFile = sZipFilePath + "src.zip";
		if (FileHelper.isExists(sDesFile)) {
			UnZip.unzip(sDesFile, sZipFilePath);
//			FileHelper.delete(sDesFile);
		}
	}
	
	public void replaceSrc() {
		String sDesFile = sZipFilePath + "src.zip";
		if (FileHelper.isExists(sDesFile)) {
			ZipCompressor zip = new ZipCompressor(sZipFilePath + "src.bk.zip");
			zip.compress(sSrcFilePath);
			Log.log("Save:" + sSrcFilePath + " To:" +  sZipFilePath + "src.bk.zip");
			PathUtils.delete(sSrcFilePath, true);
			String sParentFolder = PathUtils.getParentPath(sSrcFilePath, true);
			UnZip.unzip(sDesFile, sParentFolder);
			Log.log("Unzip:" + sSrcFilePath);
			FileHelper.delete(sDesFile);
		} else {
			Log.log("Miss:" + sDesFile);
		}
	}

}
