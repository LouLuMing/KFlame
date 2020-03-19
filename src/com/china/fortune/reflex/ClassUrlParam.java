package com.china.fortune.reflex;

import com.china.fortune.global.Log;
import com.china.fortune.http.PairBuilder;
import com.china.fortune.string.StringAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ClassUrlParam {
	static public String toUrl(Class<?> c) {
		PairBuilder pb = new PairBuilder();
		pb.add(c);
		if (pb.size() > 0) {
			return pb.toString();
		} else {
			return null;
		}
	}

	static public String toUrl(Object obj) {
		PairBuilder pb = new PairBuilder();
        pb.add(obj);
		if (pb.size() > 0) {
			return pb.toString();
		} else {
			return null;
		}
	}

	static public void toObject(String sUrl, Object o) {
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			if (lsFields != null) {
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						Class<?> cType = f.getType();
						if (cType == String.class) {
							f.set(o, getString(sUrl, f.getName()));
						} else if (cType == Integer.class || cType == int.class) {
							f.setInt(o, getInt(sUrl, f.getName()));
						} else if (cType == Long.class || cType == long.class) {
							f.setLong(o, getLong(sUrl, f.getName()));
						}
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
	}

	static public long getLong(String sUrl, String sKey) {
		String sValue = StringAction.findBetweenOrEnd(sUrl, sKey + "=", "&");
		return StringAction.toLong(sValue);
	}

	static public int getInt(String sUrl, String sKey) {
		String sValue = StringAction.findBetweenOrEnd(sUrl, sKey + "=", "&");
		return StringAction.toInteger(sValue);
	}

	static public String getString(String sUrl, String sKey) {
		return StringAction.urlDecode(StringAction.findBetweenOrEnd(sUrl, sKey + "=", "&"));
	}
}
