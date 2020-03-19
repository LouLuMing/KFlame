package com.china.fortune.target.initWebCatalog;

import java.io.File;

import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.reflex.ClassToPath;

public class initCatalog {

	public static void createSubPath(String sProjectPath, String sSub) {
		String sActionPath = sProjectPath + sSub;
		PathUtils.create(sActionPath);
	}
	
	public static void main(String[] args) {
		String sProjectName = "miniWeb";
		String sNowPath = ClassToPath.parentPath("src", initCatalog.class);
		String sParentPath = PathUtils.getParentPath(sNowPath, true);
		Log.log(sParentPath);
		String sProjectPath = sParentPath + sProjectName + File.separatorChar;
		PathUtils.create(sProjectPath);
		
		createSubPath(sProjectPath, "doc");
		createSubPath(sProjectPath, "common");
		createSubPath(sProjectPath, "property");
		createSubPath(sProjectPath, "entity");
		createSubPath(sProjectPath, "table");
		createSubPath(sProjectPath, "action");
		createSubPath(sProjectPath, "component");
		createSubPath(sProjectPath, "schedule");
		createSubPath(sProjectPath, "maintain");
		
	}

}
