package com.china.fortune.global;

import java.lang.reflect.Method;
import java.util.HashMap;

public class CommonResource {
	static private HashMap<String, Object> mapObj = new HashMap<String, Object>();
	static private HashMap<String, String> mapOnClose = new HashMap<String, String>();

	static public void put(Class<?> cls, Object o) {
		if (cls != null && o != null) {
			mapObj.put(cls.getName(), o);
		}
	}

	static public void put(String key, Object o) {
		if (key != null && o != null) {
			mapObj.put(key, o);
		}
	}

	static public void put(Object o) {
		if (o != null) {
			mapObj.put(o.getClass().getName(), o);
		}
	}

	static public void putClearMethod(Object o, String onClose) {
		if (o != null) {
			String sClassName = o.getClass().getName();
			mapObj.put(sClassName, o);
			if (onClose != null) {
				mapOnClose.put(sClassName, onClose);
			}
		}
	}

	static public Object get(String sTag) {
		if (sTag != null) {
			Object o = mapObj.get(sTag);
			if (o == null) {
				Log.logClassError(sTag + " is Null");
			}
			return o;
		} else {
			return null;
		}
	}
	
	static public <T>T get(Class<T> c) {
		if (c != null) {
			Object o = mapObj.get(c.getName());
			if (o == null) {
				Log.logClassError(c.getName() + " is Null");
			}
			return (T)o;
		} else {
			return null;
		}
	}

	static public void clear() {
		for (Object obj : mapObj.values()) {
			Class<?> cls = obj.getClass();
			String sClose = mapOnClose.get(cls.getName());
			if (sClose != null) {
				try {
					Method mt = cls.getDeclaredMethod(sClose);
					mt.invoke(obj);
				} catch (Exception e) {
					Log.logClassError(e.getMessage());
				}
			}
		}
	}
}
