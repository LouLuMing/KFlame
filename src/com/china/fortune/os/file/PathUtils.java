package com.china.fortune.os.file;

import java.io.File;
import java.util.ArrayList;

import com.china.fortune.file.FileHelper;
import com.china.fortune.file.SerachFileInterface;
import com.china.fortune.global.Log;

public class PathUtils {
	static public synchronized String getCurrentDataPath(boolean bSeparatorEndian) {
		String sRootPath = System.getProperty("user.dir");
		if (sRootPath.endsWith(".")) {
			sRootPath = sRootPath.substring(0, sRootPath.length() - 1);
		}
		if (bSeparatorEndian) {
			if (sRootPath.charAt(sRootPath.length() - 1) != File.separatorChar) {
				sRootPath += File.separatorChar;
			}
		} else {
			if (sRootPath.charAt(sRootPath.length() - 1) == File.separatorChar) {
				sRootPath = sRootPath.substring(0, sRootPath.length() - 1);
			}
		}
		Log.logClass(sRootPath);
		return sRootPath;
	}

	static public boolean isExists(String sDir) {
		File dirFile = new File(sDir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			return true;
		}
		return false;
	}

	static public boolean setCurrentDataPath(String sDir) {
		File dirFile = new File(sDir);
		if (dirFile.exists() && dirFile.isDirectory()) {
			System.setProperty("user.dir", sDir);
			return true;
		}
		return false;
	}

	static public String getParentPath(String sfullPath, boolean bSeparatorEndian) {
		if (sfullPath.length() > 1) {
			char[] pChar = sfullPath.toCharArray();
			for (int i = pChar.length - 2; i > 0; i--) {
				// if (pChar[i] == File.separatorChar) {
				if (pChar[i] == '/' || pChar[i] == '\\') {
					if (bSeparatorEndian) {
						sfullPath = sfullPath.substring(0, i + 1);
					} else {
						sfullPath = sfullPath.substring(0, i);
					}
					break;
				}
			}
		}
		return sfullPath;
	}

	static public boolean create(String fullDir) {
		File f = new File(fullDir);
		if (f.exists()) {
			if (!f.isDirectory()) {
				f.delete();
			} else {
				return true;
			}
		}
		return f.mkdirs();
	}

	static public String addSeparator(String strDir) {
		if (!strDir.endsWith(File.separator)) {
			return strDir + File.separator;
		}
		return strDir;
	}

	static public String delSeparator(String strDir) {
		if (strDir.endsWith(File.separator)) {
			return strDir.substring(0, strDir.length() - 1);
		}
		return strDir;
	}

	static public boolean delete(String sDir, boolean bDelSelf) {
		boolean flag = true;

		File dirFile = new File(sDir);

		if (dirFile.exists() && dirFile.isDirectory()) {
			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					flag = FileHelper.delete(files[i].getAbsolutePath());
				} else {
					flag = delete(files[i].getAbsolutePath(), true);
				}
			}
		}
		if (bDelSelf) {
			flag = dirFile.delete();
		}
		return flag;
	}

	static public String getFullPath(String sBaseDir) {
		if (sBaseDir.indexOf(File.separatorChar) < 0) {
			return PathUtils.getCurrentDataPath(true) + sBaseDir;
		} else {
			String sPath = sBaseDir;
			if (sPath.startsWith(".")) {
				String sCurDir = PathUtils.getCurrentDataPath(false);
				do {
					if (sPath.startsWith("..")) {
						sPath = sPath.substring(2);
						sCurDir = PathUtils.getParentPath(sCurDir, false);
					} else if (sPath.startsWith(".")) {
						sPath = sPath.substring(1);
					} else {
						sPath = sCurDir + sPath;
						break;
					}
					if (sPath.startsWith(File.separator)) {
						sPath = sPath.substring(1);
						sCurDir = PathUtils.addSeparator(sCurDir);
					}
				} while (true);
			}
			return sPath;
		}
	}

	static public boolean move(String sSrcFileName, String sDesFileName) {
		File srcDir = new File(sSrcFileName);
		File destDir = new File(sDesFileName);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		if (srcDir.isDirectory() && destDir.isDirectory()) {
			File[] srcFiles = srcDir.listFiles();
			for (int i = 0; i < srcFiles.length; i++) {
				if (srcFiles[i].isFile()) {
					FileHelper.copy(sSrcFileName, sDesFileName, srcFiles[i].getName(), srcFiles[i].getName());
				} else if (srcFiles[i].isDirectory()) {
					String sSubSrc = PathUtils.addSeparator(sSrcFileName) + srcFiles[i].getName() + File.separator;
					String sSubDes = PathUtils.addSeparator(sDesFileName) + srcFiles[i].getName() + File.separator;
					move(sSubSrc, sSubDes);
				}
			}
			delete(sSrcFileName, true);
			return true;
		}
		return false;
	}

	static public boolean serachDir(String sDir, SerachFileInterface lsner) {
		boolean bNext = true;
		File dir = new File(sDir);
		if (dir.isDirectory()) {
			File[] lsFiles = dir.listFiles();
			if (lsFiles != null) {
				for (File file : lsFiles) {
					if (file.isDirectory()) {
						bNext = serachDir(file.getAbsolutePath(), lsner);
					} else {
						bNext = lsner.onFile(file);
					}
					if (!bNext) {
						break;
					}
				}
			}
		}
		return bNext;
	}

	static public ArrayList<String> getAllFileName(String sDir) {
		ArrayList<String> lsFileName = new ArrayList<String>();
		File dir = new File(sDir);
		if (dir.isDirectory()) {
			File[] lsFiles = dir.listFiles();
			if (lsFiles != null) {
				for (File file : lsFiles) {
					if (file.isFile()) {
						lsFileName.add(file.getName());
					}
				}
			}
		}
		return lsFileName;
	}

	static public void getAllFile(String sDir, ArrayList<String> lsFileName) {
		File dir = new File(sDir);
		if (dir.isDirectory()) {
			File[] lsFiles = dir.listFiles();
			if (lsFiles != null) {
				for (File file : lsFiles) {
					if (file.isFile()) {
						lsFileName.add(file.getAbsolutePath());
					} else {
						getAllFile(file.getAbsolutePath(), lsFileName);
					}
				}
			}
		}
	}

	static public ArrayList<String> getAllFile(String sDir, String sTag) {
		ArrayList<String> lsFileName = new ArrayList<String>();
		File dir = new File(sDir);
		if (dir.isDirectory()) {
			File[] lsFiles = dir.listFiles();
			if (lsFiles != null) {
				for (File file : lsFiles) {
					if (file.isFile()) {
						String sFileName = file.getName();
						if (sFileName.startsWith(sTag)) {
							lsFileName.add(sFileName);
						}
					}
				}
			}
		}
		return lsFileName;
	}

    static public void syncFiles(String sSrcPath, String sDesPath) {
        ArrayList<String> lsDesFileName = new ArrayList<String>();
        PathUtils.getAllFile(sDesPath, lsDesFileName);
        for (String sDesFile : lsDesFileName) {
            String sSrcFile = sDesFile.replace(sDesPath, sSrcPath);
            File fDes = new File(sDesFile);
            File fSrc = new File(sSrcFile);
            if (fDes.exists() && fSrc.exists()) {
				if (fDes.lastModified() < fSrc.lastModified()) {
					FileHelper.copy(sSrcFile, sDesFile);
				}
			}
        }
    }

	public static void main(String[] args) {
		Log.log(System.getProperty("user.dir"));
		Log.log(getCurrentDataPath(true));
		Log.log(getCurrentDataPath(false));
		Log.log(getFullPath("."));
		Log.log(getFullPath(".\\"));
		Log.log(getFullPath(".\\.\\"));
		Log.log(getFullPath(".\\helo"));
		Log.log(getFullPath("..\\helo\\"));
	}
}
