package com.china.fortune.file;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;

import java.io.*;

public class FileHelper {
	static public boolean isExists(String sDir) {
		File dirFile = new File(sDir);
		if (dirFile.exists() && dirFile.isFile()) {
			return true;
		}
		return false;
	}

	static public boolean delete(String sFile) {
		File file = new File(sFile);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return true;
	}

	static public void rename(String sSrc, String sDes) {
		if (!sSrc.equals(sDes)) {
			File srcfile = new File(sSrc);
			File desfile = new File(sDes);
			if (desfile.exists()) {
				desfile.delete();
			}
			srcfile.renameTo(desfile);
		}
	}

	static public boolean copy(String sSrcPath, String sDesPath) {
		boolean hz = false;
		byte[] pData = readSmallFile(sSrcPath);
		if (pData != null) {
			hz = writeSmallFile(sDesPath, pData);
		}
		return hz;
	}

	static public boolean copy(String sSrcPath, String sDesPath, String sSrcFile, String sDesFile) {
		boolean hz = false;
		byte[] pData = readSmallFile(PathUtils.addSeparator(sSrcPath) + sSrcFile);
		if (pData != null) {
			String sDesFullFile = PathUtils.addSeparator(sDesPath);
			if (PathUtils.create(sDesFullFile)) {
				sDesFullFile += sDesFile;
				hz = writeSmallFile(sDesFullFile, pData);
			}
		}
		return hz;
	}

	static public boolean move(String sSrcPath, String sDesPath, String sFileName) {
		boolean hz = false;
		if (copy(sSrcPath, sDesPath, sFileName, sFileName)) {
			String sSrcFileName = PathUtils.addSeparator(sSrcPath) + sFileName;
			delete(sSrcFileName);
			hz = true;
		}
		return hz;
	}

	static public String readSmallFile(String sFile, String sCode) {
		String strData = null;
		if (sFile != null && sCode != null) {
			byte[] pRead = readSmallFile(sFile);
			if (pRead != null) {
				try {
					strData = new String(pRead, sCode);
				} catch (UnsupportedEncodingException e) {
					Log.logClass(e.getMessage() + ":" + sFile);
					strData = null;
				}
			}
		}
		return strData;
	}

	static public byte[] readSmallFile(File file) {
		byte[] pRead = null;
		try {
			InputStream is = new FileInputStream(file);
			if (is != null) {
				int iData = (int) file.length();
				byte[] pData = new byte[iData];
				if (is.read(pData, 0, iData) == iData) {
					pRead = pData;
				}
				is.close();
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage() + ":" + file.getAbsolutePath());
		}
		return pRead;
	}

	static public byte[] readSmallFile(String fullfilename) {
		byte[] pRead = null;
		if (fullfilename != null) {
			File file = new File(fullfilename);
			if (file.exists() && file.isFile()) {
				pRead = readSmallFile(file);
			}
		}
		return pRead;
	}

	static public boolean writeSmallFile(String fullfilename, byte[] pData, int iStart, int iLen) {
		boolean hz = false;
		if (pData != null && fullfilename != null) {
			try {
				File file = new File(fullfilename);
				if (!file.exists()) {
					file.createNewFile();
				}
				OutputStream os = new FileOutputStream(file);
				if (os != null) {
					os.write(pData, iStart, iLen);
					os.close();
					hz = true;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage() + ":" + fullfilename);
			}
		}
		return hz;
	}

	static public boolean writeSmallFile(String fullfilename, byte[] pData) {
		boolean hz = false;
		if (pData != null && fullfilename != null) {
			try {
				File file = new File(fullfilename);
				if (!file.exists()) {
					file.createNewFile();
				}
				OutputStream os = new FileOutputStream(file);
				if (os != null) {
					os.write(pData);
					os.close();
					hz = true;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage() + ":" + fullfilename);
			}
		}
		return hz;
	}

	static public boolean writeSmallFile(String fullfilename, String strData, String strCode) {
		boolean hz = false;
		if (strData != null) {
			try {
				byte[] pData = strData.getBytes(strCode);
				hz = writeSmallFile(fullfilename, pData);
			} catch (Exception e) {
				Log.logClass(e.getMessage() + ":" + fullfilename);
			}
		}
		return hz;
	}

	static public boolean writeSmallFile(String fullfilename, String strData) {
		return writeSmallFile(fullfilename, strData, ConstData.sFileCharset);
	}

	static public String getFileNameFromFullPath(String sFullPath) {
		int i = sFullPath.lastIndexOf('\\');
		if (i >= 0) {
			return sFullPath.substring(i + 1);
		} else {
			i = sFullPath.lastIndexOf('/');
			if (i >= 0) {
				return sFullPath.substring(i + 1);
			}
		}
		return sFullPath;
	}

	static public String getFileNameWithoutExtensions(String sFullPath) {
		String sFileName = getFileNameFromFullPath(sFullPath);
		int i = sFileName.lastIndexOf('.');
		if (i >= 0) {
			return sFileName.substring(0, i);
		}
		return sFileName;
	}

	static public String getFileExtension(String sFile) {
		int iDot = sFile.lastIndexOf('.');
		if (iDot > 0) {
			return sFile.substring(iDot + 1);
		}
		return null;
	}
}
