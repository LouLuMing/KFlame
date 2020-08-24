package com.china.fortune.http.httpHead;

import com.china.fortune.file.FileUtils;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;

import java.io.File;

public class HttpResponse extends HttpHeader {
	static final public int ciCode = 200;
	static final public String csReason = "OK";

	static public boolean isMoved(int iCode) {
		return iCode == 301 || iCode == 302 || iCode == 303 || iCode == 307;
	}
	
	public boolean isMoved() {
		return iCode == 301 || iCode == 302 || iCode == 303 || iCode == 307;
	}

	private String sVersion = csVersion;
	private int iCode = 200;
	private String sReason = csReason;
	public void copy(HttpResponse hr) {
		super.copy(hr);
		if (hr != null) {
			sVersion = hr.sVersion;
			iCode = hr.iCode;
			sReason = hr.sReason;
		}
	}

	public HttpResponse() {}

	public HttpResponse(int code) {
		iCode = code;
		sReason = HttpProp.getError(code);
	}

	public HttpResponse(int code, String reason) {
		iCode = code;
		sReason = reason;
	}

	public void clear() {
		iCode = ciCode;
		sReason = csReason;
		super.clear();
	}

	public void setResponse(int code) {
		if (iCode != code) {
			iCode = code;
			sReason = HttpProp.getError(code);
		}
	}
	
	public void setResponse(int code, String reason) {
		iCode = code;
		sReason = reason;
	}

	public boolean parseResponse(String sResponse) {
		String[] lsText = StringUtils.split(sResponse, ' ', 3);
		if (lsText != null && lsText.length > 1) {
			sVersion = lsText[0];
			iCode = StringUtils.toInteger(lsText[1]);
			if (lsText.length > 2) {
				sReason = lsText[2];
			}
			return true;
		} else {
			return false;
		}
	}

	public String getProtocolVersion() {
		return sVersion;
	}

	public int getStatusCode() {
		return iCode;
	}

	public String getReason() {
		return sReason;
	}

	public FastList<Header> getHeader() {
		return super.lsHeader;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sVersion);
		sb.append(' ');
		sb.append(iCode);
		sb.append(' ');
		sb.append(sReason);
		sb.append(csEnter);
		sb.append(super.toString());
		sb.append(csEnter);
		return sb.toString();
	}

	public boolean putFile(String sFileName, byte[] bData) {
		if (bData != null) {
			String sContentType = HttpProp.getContentTypeByFile(sFileName);
			if (sContentType == null) {
				sContentType = HttpProp.csDefaultContentType;
			}
			addHeader(HttpHeader.csContentDisposition, "filename=" + sFileName);
			addHeader(HttpHeader.csContentType, sContentType);
			setBodyGZip(bData);
			return true;
		} else {
			setBody(null);
			setResponse(404);
			return false;
		}
	}

	public boolean putFile(String sFileName, byte[] bData, String sContentType) {
		if (bData != null) {
			addHeader(HttpHeader.csContentDisposition, "filename=" + sFileName);
			addHeader(HttpHeader.csContentType, sContentType);
			setBodyGZip(bData);
			return true;
		} else {
			setResponse(404);
			return false;
		}
	}

	public boolean putFile(String sFileName) {
		if (sFileName != null) {
			File file = new File(sFileName);
			if (file.exists() && file.isFile()) {
				byte[] bData = FileUtils.readSmallFile(file);
				if (bData != null) {
					addHeader(HttpHeader.csEtag, String.valueOf(file.lastModified()));
					String sShortFileName = FileUtils.getSimpleName(sFileName);
					return putFile(sShortFileName, bData);
				}
			}
		}
		return false;
	}

	public void setFileHeader(String sFileName) {
		String sShortFileName = FileUtils.getSimpleName(sFileName);
		String sContentType = HttpProp.getContentTypeByFile(sShortFileName);
		if (sContentType == null) {
			sContentType = HttpProp.csDefaultContentType;
		}
		addHeader(csContentDisposition, "filename=" + sShortFileName);
		addHeader(csContentType, sContentType);
	}
}
