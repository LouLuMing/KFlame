package com.china.fortune.restfulHttpServer;

import com.china.fortune.reflex.ClassToUrlAddress;

public class ActionToUrl {
	final static private String sHeadFolder = "action";
	static public String toUrl(Class<?> c) {
		String sUrl = ClassToUrlAddress.toUrlAddress(sHeadFolder, c).toLowerCase();
		if (sUrl.length() > 6) {
			return sUrl.substring(0, sUrl.length() - 6);
		} else {
			return sUrl;
		}
	}

	static public String toParentUrl(Class<?> c) {
		String sUrl = ClassToUrlAddress.toUrlAddress(sHeadFolder, c).toLowerCase();
		int index = sUrl.lastIndexOf('/');
		if (index > 0) {
			return sUrl.substring(0, index);
		} else {
			return sUrl;
		}
	}
}
