package com.china.fortune.data;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.file.FileUtils;
import com.china.fortune.file.IniFileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringUtils;

public class ResourceUtils {
	static public String sRoot = PathUtils.getCurrentDataPath(true) + "res" + File.separator;

	static public String getRootPath() {
		return sRoot;
	}

	static public String getPath(String sFile) {
		return sRoot + sFile;
	}

	static public String loadFile(String sFile) {
		return FileUtils.readSmallFile(sRoot + sFile, "utf-8");
	}

	static public void saveFile(String sFile, String sJson) {
		FileUtils.writeSmallFile(sRoot + sFile, sJson, "utf-8");
	}

	static public byte[] getBytes(String sFile) {
		return FileUtils.readSmallFile(sRoot + sFile);
	}

	static public int loadFromIni(Class<?> cls) {
		String sClsName = cls.getSimpleName();
		ArrayList<String2Struct> lsData = new ArrayList<String2Struct>();
        IniFileUtils.toArrayList(getPath(sClsName) + ".ini", lsData);
		int iCount = 0;
		for (String2Struct s2s : lsData) {
			Log.log(sClsName + ":" + s2s.s1 + " " + s2s.s2);
			if (StringUtils.length(s2s.s1) > 0) {
				try {
					Field field = cls.getField(s2s.s1);
					if (field != null && (field.getModifiers() & Modifier.STATIC) != 0) {
						field.set(null, s2s.s2);
						iCount++;
					}
				} catch (Exception e) {
					Log.logError("miss " + e.getMessage());
				}
			}
		}
		return iCount;
	}

	static public void loadIniFileToHashMap(String sFile, HashMap<String, String> lsData) {
		IniFileUtils.toHashMap(getPath(sFile), lsData);
	}

	static public ArrayList<String2Struct> loadIniFileToArrayList(String sFile) {
		return IniFileUtils.toArrayList(getPath(sFile));
	}
}
