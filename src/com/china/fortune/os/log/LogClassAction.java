package com.china.fortune.os.log;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.china.fortune.string.StringAction;

public class LogClassAction extends LogAction {
	private LogAction obj = new LogAction();
	private ArrayList<String> lsTags = new ArrayList<String>();
	private boolean bShowAllTags = true;
	
	public void allowAllTags(boolean b) {
		bShowAllTags = b;
	}

	public void addShowTag(String sTag) {
		lsTags.add(sTag);
	}

	public void allowAllTag(boolean b) {
		bShowAllTags = b;
	}
	
	public void log(String sText) {
		if (obj.isShow()) {
			obj.log(sText);
		}
	}
	
	public void logTag(String sTag, String sText) {
		if (obj.isShow()) {
			if (bShowAllTags || lsTags.contains(sTag)) {
				obj.log(sTag + " " + sText);
			}
		}
	}
	
	private ArrayList<String> lsShowClassNames = new ArrayList<String>();
	private ArrayList<String> lsHideClassNames = new ArrayList<String>();
	private boolean bShowAllClasses = true;
	
	static private ArrayList<String> lsLogMethods = new ArrayList<String>();
	static {
		Method[] ms = LogClassAction.class.getDeclaredMethods();
		for (Method m : ms) {
			lsLogMethods.add(m.getName());
		}
	}

	public void allowAllClass(boolean b) {
		bShowAllClasses = b;
	}

	public void showClasss(Class<?> c) {
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

	public void hideClasss(Class<?> c) {
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

	public String getSimpleClassName(String sClassName) {
		String sSimpleClassName = sClassName;
		if (sClassName != null) {
			int i = sClassName.lastIndexOf('.');
			if (i >= 0) {
				sSimpleClassName = sClassName.substring(i + 1,
						sClassName.length() - 1);
			}
			String[] lsObj = sClassName.split("\\.");
			if (lsObj.length > 0) {
				sSimpleClassName = lsObj[lsObj.length - 1];
			}
		}
		return sSimpleClassName;
	}

	public void logClass(String sText) {
		if (obj.isShow()) {
			if (bShowAllClasses || lsShowClassNames.size() > 0) {
				for (StackTraceElement st : (new Throwable()).getStackTrace()) {
					String sFullClassName = st.getClassName();
					if (!lsLogMethods.contains(sFullClassName)) {
						String sCN = StringAction.getBefore(sFullClassName, "$");
						if (lsShowClassNames.contains(sCN)
								|| (bShowAllClasses && !lsHideClassNames.contains(sCN))) {
							String sTag = getSimpleClassName(sFullClassName)
									+ ":" + st.getMethodName();
							obj.log(sTag + " " + sText);
						}
						break;
					}
				}
			}
		}
	}
}
