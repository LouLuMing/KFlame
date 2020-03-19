package com.china.fortune.reflex;

import java.io.File;

public class ClassToPath {
	static public String path(Class<?> cls) {
		String sClassName = cls.getName();
		return sClassName.replace('.', File.separatorChar);
	}
	
	static public String parentPath(Class<?> cls) {
		String sClassName = cls.getName();
		int iLastIndex = sClassName.lastIndexOf('.');
		if (iLastIndex > 0) {
			sClassName = sClassName.substring(0, iLastIndex + 1);
		}
		return sClassName.replace('.', File.separatorChar);
	}
	
	static public String parentPath(String sPrev, Class<?> cls) {
		return sPrev + File.separatorChar + parentPath(cls);
	}
}
