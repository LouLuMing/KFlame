package com.china.fortune.global;

import com.china.fortune.os.log.LogAction;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
	static private LogAction obj = new LogAction();

	static public void clearHistory(int days) {
		obj.clearHistory(days);
	}
	
	static public void init(int iType) {
		obj.init(iType);
	}

	static public void setLog(String sDir, String sFile) {
		obj.setLog(sDir, sFile);
	}

	static private FastList<String> lsTags = new FastList<String>();
	static private boolean bShowAllTags = true;

	static public void allowAllTags(boolean b) {
		bShowAllTags = b;
	}

	static public void addShowTag(String sTag) {
		lsTags.add(sTag);
	}

	static public void allowAllTag(boolean b) {
		bShowAllTags = b;
	}

	static public void log(String sText) {
		if (obj.isShow()) {
			obj.log(sText);
		}
	}

	static public void logNoDate(String sText) {
		if (obj.isShow()) {
			obj.logNoDate(sText);
		}
	}

	static public boolean isShow() {
		return obj.isShow();
	}

	static public void logError(String sText) {
		obj.log("Error " + sText);
	}

	public static void logTag(String sTag, String sText) {
		if (obj.isShow()) {
			if (bShowAllTags || lsTags.contains(sTag)) {
				obj.log(sTag + " " + sText);
			}
		}
	}

	static private FastList<String> lsShowClassNames = new FastList<String>();
	static private FastList<String> lsHideClassNames = new FastList<String>();
	static private boolean bShowAllClasses = true;

	// static private ArrayList<String> lsLogMethods = new ArrayList<String>();
	// static {
	// Method[] ms = Log.class.getDeclaredMethods();
	// for (Method m : ms) {
	// lsLogMethods.add(m.getName());
	// }
	// }

	static public void allowAllClass(boolean b) {
		bShowAllClasses = b;
	}

	static public void showClasss(Class<?> c) {
		String sClassName = c.getName();
		if (bShowAllClasses) {
			if (lsHideClassNames.contains(sClassName)) {
				lsHideClassNames.remove(sClassName);
			}
		} else {
			if (!lsShowClassNames.contains(sClassName)) {
				lsShowClassNames.add(sClassName);
			}
		}
	}

	static public void hideClasss(Class<?> c) {
		String sClassName = c.getName();
		if (bShowAllClasses) {
			if (!lsHideClassNames.contains(sClassName)) {
				lsHideClassNames.add(c.getName());
			}
		} else {
			if (lsShowClassNames.contains(sClassName)) {
				lsShowClassNames.remove(c.getName());
			}
		}
	}

	static public String getSimpleClassName(String sClassName) {
		String sSimpleClassName = sClassName;
		if (sClassName != null) {
			int i = sClassName.lastIndexOf('.');
			if (i >= 0) {
				sSimpleClassName = sClassName.substring(i + 1);
			}
//			String[] lsObj = sClassName.split("\\.");
//			if (lsObj.length > 0) {
//				sSimpleClassName = lsObj[lsObj.length - 1];
//			}
		}
		return sSimpleClassName;
	}

	static public void logClass(String sText) {
		if (sText != null && obj.isShow()) {
			if (bShowAllClasses || lsShowClassNames.size() > 0) {
				StackTraceElement[] lsTrace = (new Throwable()).getStackTrace();
				String sClassName = lsTrace[1].getClassName();
				sClassName = StringUtils.getBefore(sClassName, "$");
				if (lsShowClassNames.contains(sClassName) || (bShowAllClasses && !lsHideClassNames.contains(sClassName))) {
					String sTag = getSimpleClassName(sClassName) + ":" + lsTrace[1].getMethodName();
					obj.log(sTag + " " + sText);
				}
			}
		}
	}

	static public int parseLoginType(String sLogType) {
		int iLogType = LogAction.iConsole;
		if (sLogType != null) {
			if (sLogType.contains("null")) {
				iLogType = LogAction.iNull;
			} else {
				if (sLogType.contains("con")) {
					if (sLogType.contains("file")) {
						iLogType = (LogAction.iConsole | LogAction.iFile);
					} else {
						iLogType = LogAction.iConsole;
					}
				} else if (sLogType.contains("file")) {
					iLogType = LogAction.iFile;
				}
			}
		}
		return iLogType;
	}

	static public void logException(Exception e) {
		if (e != null) {
			try {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				obj.log("Error " + sw.toString());
			} catch (Exception ex) {
				obj.log("Error " + ex.getMessage());
			}
		}
	}

	static public void logException(Error e) {
		if (e != null) {
			try {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				obj.log("Error " + sw.toString());
			} catch (Exception ex) {
				obj.log("Error " + ex.getMessage());
			}
		}
	}
	
	static public void logClassError(String sText) {
		StackTraceElement[] lsTrace = (new Throwable()).getStackTrace();
		String sClassName = lsTrace[1].getClassName();
		sClassName = StringUtils.getBefore(sClassName, "$");
		if (lsShowClassNames.contains(sClassName) || (bShowAllClasses && !lsHideClassNames.contains(sClassName))) {
			String sTag = getSimpleClassName(sClassName) + ":" + lsTrace[1].getMethodName();
			obj.log("Error " + sTag + " " + sText);
		}
	}
}
