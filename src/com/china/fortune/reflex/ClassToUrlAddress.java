package com.china.fortune.reflex;

import com.china.fortune.global.Log;

public class ClassToUrlAddress {
	static public String toUrlAddress(String sHead, Class<?> cls) {
		String sClassName = cls.getName();
		if (sHead != null) {
			int iLastIndex = sClassName.indexOf(sHead);
			if (iLastIndex > 0) {
				sClassName = sClassName.substring(iLastIndex + sHead.length());
				return sClassName.replace('.', '/');
			} else {
				return "/" + sClassName.replace('.', '/');
			}
		}
		return "/" + sClassName.replace('.', '/');
	}
	
	public static void main(String[] args) {
		Log.log(toUrlAddress("china", ClassToUrlAddress.class));
	}
}
