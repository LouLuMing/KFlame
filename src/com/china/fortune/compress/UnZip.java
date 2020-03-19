package com.china.fortune.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.china.fortune.os.file.PathUtils;

public class UnZip {
	static private void writeFile(ZipFile zipFile, ZipEntry zipEnt, String sFullName) {
		InputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fis = zipFile.getInputStream(zipEnt);
			bis = new BufferedInputStream(fis);
			fos = new FileOutputStream(sFullName);
			bos = new BufferedOutputStream(fos);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write((byte) c);
			}
		} catch (Exception e) {
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
				};
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				};
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
				};
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				};
			}
		}
	}

	static public void unzip(String zipFileName, String strPath) {
		File f = new File(zipFileName);
		strPath = PathUtils.addSeparator(strPath);
		if (f.exists()) {
			try {
				ZipFile zipFile = new ZipFile(zipFileName);
				Enumeration<? extends ZipEntry> emu = zipFile.entries();
				while (emu.hasMoreElements()) {
					ZipEntry zipEnt = (ZipEntry) emu.nextElement();
					String sFullName = strPath + zipEnt.getName();
					if (zipEnt.isDirectory()) {
						PathUtils.create(sFullName);
						continue;
					} else {
						PathUtils.create(PathUtils.getParentPath(sFullName, false));
						writeFile(zipFile, zipEnt, sFullName);
					}
				}
				zipFile.close();
			} catch (Exception e) {
			}
		}
	}
}
