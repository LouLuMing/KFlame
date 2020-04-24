package com.china.fortune.http.property;

import java.util.HashMap;

public final class HttpProp {
	static final public int iMaxContentLenLimited = 64 * 1024 * 1024;
	static final public int iMaxChunkedLenLimited = 1024 * 1024;
	static final public int iMaxChunkedCount = 128;

    final static public String csDefaultContentType = "application/octet-stream";

    static HashMap<String, String> mapContentType = new HashMap<String, String>(
            128);
    static HashMap<Integer, String> mapErrorCode = new HashMap<Integer, String>(
            128);

    static {
        mapContentType.put("css", "text/css");
        mapContentType.put("html", "text/html");
        mapContentType.put("txt", "text/plain");
        mapContentType.put("xml", "text/xml");

        mapContentType.put("png", "image/png");
        mapContentType.put("gif", "image/gif");
        mapContentType.put("ico", "image/x-icon");
        mapContentType.put("jpg", "image/jpeg");

        mapContentType.put("js", "application/x-javascript");
        mapContentType.put("json", "application/json");
        mapContentType.put("form", "application/x-www-form-urlencoded");

        mapContentType.put("pdf", "application/pdf");

        mapErrorCode.put(200, "OK");
        mapErrorCode.put(302, "Temporarily Moved");
        mapErrorCode.put(403, "Forbidden");
        mapErrorCode.put(404, "Not Found");
        mapErrorCode.put(500, "Internal Server Error");
    }

    static public String getContentType(String sType) {
        return mapContentType.get(sType);
    }

    static public String getError(int err) {
        String sError = mapErrorCode.get(err);
        if (sError != null) {
            return sError;
        } else {
            return "Unknown Error";
        }
    }
}
