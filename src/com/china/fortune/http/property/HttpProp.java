package com.china.fortune.http.property;

import com.china.fortune.file.FileUtils;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.struct.FastHashMap;

public final class HttpProp {
	static final public int iMaxContentLenLimited = 64 * 1024 * 1024;
	static final public int iMaxChunkedLenLimited = 1024 * 1024;
	static final public int iMaxChunkedCount = 128;

    final static public String csDefaultContentType = "application/octet-stream";

    static FastHashMap<String> mapContentType = new FastHashMap<>();

    static String[] lsErrorCode = new String[1024];

    static public byte[] bHR304;
    static public byte[] bHR404;
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

        lsErrorCode[200] = "OK";
        lsErrorCode[302] = "Temporarily Moved";
        lsErrorCode[304] = "Not Modified";
        lsErrorCode[403] = "Forbidden";
        lsErrorCode[404] = "Not Found";
        lsErrorCode[500] = "Internal Server Error";

        mapContentType.initHitCache();

        HttpResponse hRes = new HttpResponse(304);
        hRes.setBody(null);
        bHR304 = hRes.toByte();
        hRes.setResponse(404);
        bHR404 = hRes.toByte();
    }

    static public String getContentType(String sType) {
        return mapContentType.get(sType.toLowerCase());
    }

    static public String getError(int err) {
        if (err >=0 && err < lsErrorCode.length) {
            String sError = lsErrorCode[err];
            if (sError != null) {
                return sError;
            }
        }
        return "Unknown Error";
    }

    static public String getContentTypeByFile(String sFileName) {
        String sFileExtension = FileUtils.getFileExtension(sFileName);
        if (sFileExtension != null) {
            return getContentType(sFileExtension);
        }
        return null;
    }
}
