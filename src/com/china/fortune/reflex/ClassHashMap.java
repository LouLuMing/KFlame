package com.china.fortune.reflex;

import com.china.fortune.string.StringAction;

import java.util.HashMap;

public class ClassHashMap extends ClassData <HashMap<String, String>>{

	@Override
	String getString(HashMap<String, String> data, String sKey) {
		return data.get(sKey);
	}

	@Override
	long getLong(HashMap<String, String> data, String sKey) {
		return StringAction.toLong(data.get(sKey));
	}

	@Override
	int getInt(HashMap<String, String> data, String sKey) {
		return StringAction.toInteger(data.get(sKey));
	}

	@Override
	void setString(HashMap<String, String> data, String sKey, String sValue) {
		data.put(sKey, sValue);
	}

	@Override
	void setLong(HashMap<String, String> data, String sKey, long lValue) {
		data.put(sKey, String.valueOf(lValue));
	}

	@Override
	void setInt(HashMap<String, String> data, String sKey, int iValue) {
		data.put(sKey, String.valueOf(iValue));
	}

	static private ClassHashMap chm = new ClassHashMap();
	static public HashMap<String, String> toMap(Object o) {
		HashMap<String, String> hm = new HashMap<String, String>();
		chm.toData(o, hm);
		return hm;
	}
}
