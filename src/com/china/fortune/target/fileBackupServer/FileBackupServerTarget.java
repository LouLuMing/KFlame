package com.china.fortune.target.fileBackupServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.china.fortune.common.DateAction;
import com.china.fortune.file.FileHelper;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.UrlParam;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.string.StringAction;
import com.china.fortune.target.httpFileServer.HttpFileAction;
import com.china.fortune.target.httpFileServer.HttpFileServer;
import com.china.fortune.xml.XmlNode;

public class FileBackupServerTarget implements TargetInterface {
	public class BackupHttpFileAction extends HttpFileAction {
		private String sRootPath = null;
		private String sBackupPath = null;

		private BackupHttpFileAction(String sPath, String sPost, String sGet) {
			super(sGet, sPost);
			String sBasePath = PathUtils.addSeparator(sPath);
			String sSubPath = getGetResource();
			if (sSubPath.equals(".")) {
				sRootPath = sBasePath;
			} else {
				sRootPath = sBasePath + sSubPath;	
			}
			sBackupPath = sBasePath + "backup" + File.separator;
		}
		
		public boolean checkHttpPost(HttpRequest hReq) {
			String sResource = hReq.getResource();
			String file = UrlParam.findValue(sResource, "file");
			return StringAction.length(file) > 0;
		}
		
		public String createFileName(HttpRequest hReq) {
			String sResource = hReq.getResource();
			String sSrcFile = UrlParam.findValue(sResource, "file");
			File fSrc = new File(sRootPath + File.separator + sSrcFile);
			if (fSrc.exists()) {
				clearBackup(sSrcFile);

				String sDesFile = sSrcFile + "." + DateAction.createDateTime(fSrc.lastModified(), "yyyyMMdd_HHmmss");
				FileHelper.copy(sRootPath, sBackupPath, sSrcFile, sDesFile);
			}
			return sSrcFile;
		}

		private void clearBackup(String sTag) {
			ArrayList<String> lsData = PathUtils.getAllFile(sBackupPath, sTag);
			Collections.sort(lsData, String.CASE_INSENSITIVE_ORDER);
			for (int i = 0; i < lsData.size() - 10; i++) {
				FileHelper.delete(sBackupPath + lsData.get(i));
			}
		}
	}
		
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		HttpFileServer obj = new HttpFileServer();
		
		String sFolder = cfg.getChildNodeText("folder");
		if (StringAction.length(sFolder) > 0) {
			obj.setRootPath(PathUtils.getFullPath(sFolder));
		}
		
		String sRootPath = obj.getRootPath(true);
		obj.addHttpFileAction(new BackupHttpFileAction(sRootPath, "/home", "."));
		obj.addHttpFileAction(new BackupHttpFileAction(sRootPath, "/src", "src"));
		obj.addHttpFileAction(new BackupHttpFileAction(sRootPath, "/jar", "jar"));
		obj.startAndBlock(iLocalPort);
		
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}

}
