package com.china.fortune.http.httpHead;

import com.china.fortune.compress.GZipCompressor;
import com.china.fortune.global.ConstData;
import com.china.fortune.struct.FastList;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.ByteParser;

import java.io.UnsupportedEncodingException;

public class HttpHeader {
	final static public byte[] fbCRLFCRLF = new byte[] { 0x0d, 0x0a, 0x0d, 0x0a };
	final static public byte[] fbCRLF = new byte[] { 0x0d, 0x0a };

	static final public String csEnter = "\r\n";
	static final public String csVersion = "HTTP/1.1";
	static final public String csContentLength = "Content-Length";
	static final public String csContentType = "Content-Type";
	static final public String csLocation = "Location";
	static final public String csAccept = "Accept";
	static final public String csHost = "Host";
	static final public String csTransferEncoding = "Transfer-Encoding";
	static final public String csContentEncoding = "Content-Encoding";
	static final public String csCharset = "charset=";
	static final public String csContentDisposition = "Content-Disposition";
	static final public String csSetCookie = "Set-Cookie";
	static final public String csCookie = "Cookie";
	static final public String csXForwardedFor = "X-Forwarded-For";

    static final public String csConnection = "Connection";
    static final public String csUpgrade = "Upgrade";
    static final public String csSecWebSocketAccept = "Sec-WebSocket-Accept";
    static final public String csSecWebSocketKey = "Sec-WebSocket-Key";

	protected byte[] bBody = null;
    protected FastList<Header> lsHeader = new FastList<Header>();

	public void copy(HttpHeader hh) {
		if (hh != null) {
			bBody = hh.getByteBody();
			lsHeader.clear();
            lsHeader = hh.lsHeader.clone();
		}
	}

	public class Header {
		public String sKey = null;
		public String sValue = null;

		public Header(String sKey, String sValue) {
			if (sKey != null) {
				this.sKey = sKey;
			}
			if (sValue != null) {
				this.sValue = sValue;
			}
		}
	}

	public void clear() {
		lsHeader.clear();
		bBody = null;
	}

	public void addHeader(String sKey, String sValue) {
		Header hd = getHeader(sKey);
		if (hd != null) {
			hd.sValue = sValue;
		} else {
			lsHeader.add(new Header(sKey, sValue));
		}
	}

	public String toString() {
		String sHeader = "";
		for (int i = 0; i < lsHeader.size(); i++) {
            Header hd = lsHeader.get(i);
			if (hd.sKey != null && hd.sValue != null) {
				sHeader += hd.sKey + ": " + hd.sValue + csEnter;
			}
		}
		return sHeader;
	}

	public void remove(String sKey) {
        for (int i = 0; i < lsHeader.size(); i++) {
            Header hd = lsHeader.get(i);
			if (hd.sKey != null && hd.sKey.compareToIgnoreCase(sKey) == 0) {
				lsHeader.remove(hd);
			}
		}
	}

	public void parseHeader(byte[] bData, int iStart, int iEnd) {
		int iPos = ByteParser.indexOf(bData, iStart, (byte)':');
		if (iPos > 0) {
			while (bData[iStart] == ' ') {
				iStart++;
			}
			int iIndex = iPos - 1;
			while (bData[iIndex] == ' ') {
				iIndex--;
			}
			if (iIndex > iStart) {
				String sKey = new String(bData, iStart, iIndex - iStart + 1);
				iIndex = iPos + 1;
				while (bData[iIndex] == ' ') {
					iIndex++;
				}
				while (bData[iEnd] == ' ') {
					iEnd--;
				}
				if (iEnd > iIndex) {
					String sValue = new String(bData, iIndex, iEnd - iIndex + 1);
					addHeader(sKey, sValue);
				}
			}
		}
	}

	public void parseHeader(String sHeader) {
		int iPos = sHeader.indexOf(':');
		if (iPos > 0) {
			addHeader(sHeader.substring(0, iPos).trim(), sHeader.substring(iPos + 1).trim());
		}
	}

	protected Header getHeader(String sKey) {
        for (int i = 0; i < lsHeader.size(); i++) {
            Header hd = lsHeader.get(i);
			if (sKey.compareToIgnoreCase(hd.sKey) == 0) {
                return hd;
			}
		}
		return null;
	}

	public String getHeaderValue(String sKey) {
		String sValue = null;
		Header hd = getHeader(sKey);
		if (hd != null) {
			sValue = hd.sValue;
		}
		return sValue;
	}

	public boolean checkHeaderValue(String sKey, String sValue) {
		String sV = getHeaderValue(sKey);
		if (sV != null) {
			return sV.compareToIgnoreCase(sValue) == 0;
		}
		return false;
	}

	public int getContentLength() {
		return StringAction.toInteger(getHeaderValue(csContentLength));
	}

	public String getContentType() {
		String sContentType = null;
		String sRType = getHeaderValue(csContentType);
		if (sRType != null) {
			String sTmp = StringAction.getBefore(sRType, ";");
			if (sTmp != null) {
				sContentType = sTmp.trim();
			}
		}
		return sContentType;
	}

	public String getCharset() {
		String sCharset = null;
		String sRType = getHeaderValue(csContentType);
		if (sRType != null) {
			String sTmp = StringAction.getAfter(sRType, csCharset);
			if (sTmp != null) {
				sCharset = StringAction.getBefore(sTmp, ";");
			}
		}
		return sCharset;
	}

	public String getBody(String sCharset) {
		String sBody = null;
		if (bBody != null) {
			sBody = StringAction.newString(bBody, sCharset);
		}
		return sBody;
	}

	public String getBody() {
		String sBody = null;
		if (bBody != null) {
			String sCharset = getCharset();
			if (sCharset == null) {
				sCharset = ConstData.sHttpCharset;
			}
			sBody = StringAction.newString(bBody, sCharset);
		}
		return sBody;
	}

	public byte[] getByteBody() {
		return bBody;
	}

	public void setBodyNoHeader(byte[] bData) {
		this.bBody = bData;
	}

	public void setContentType(String sType) {
		if (sType != null) {
			addHeader(csContentType, sType);
		}
	}

	public void setContentType(String sType, String sCode) {
		if (sType != null) {
			if (sCode != null) {
				addHeader(csContentType, sType + ";" + csCharset + sCode);
			} else {
				addHeader(csContentType, sType);
			}
		}
	}

	public void setBody(byte[] bData) {
		bBody = bData;
		if (bBody != null) {
			addHeader(csContentLength, String.valueOf(bBody.length));
		} else {
			addHeader(csContentLength, "0");
		}
	}

	static final protected int iMinGZipLength = 1024 * 4;

	public void setBodyGZip(byte[] bData) {
		if (bData != null) {
			if (bData.length > iMinGZipLength) {
				bBody = GZipCompressor.compress(bData);
				addHeader(csContentEncoding, "gzip");
				setBody(bBody);
			} else {
				bBody = bData;
				addHeader(csContentLength, String.valueOf(bBody.length));
			}
		} else {
			bBody = null;
			addHeader(csContentLength, "0");
		}
	}

	public void setBody(String sBody, String sType) {
		setBody(sBody, sType, ConstData.sHttpCharset);
	}

	public void setBodyOnly(String sBody) {
		if (sBody != null) {
			try {
				bBody = sBody.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		} else {
			bBody = null;
		}
	}

	public void setBody(String sBody, String sType, String sCode) {
		if (sBody != null) {
			try {
				byte[] pBody = sBody.getBytes(sCode);
				setContentType(sType, sCode);
				setBody(pBody);
			} catch (UnsupportedEncodingException e) {
			}
		} else {
			setBody(null);
		}
	}

	public void accessControlAllow() {
		addHeader("Access-Control-Allow-Origin", "*");
		addHeader("Access-Control-Allow-Headers",
				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
		addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, OPTIONS");
	}

//	public int getContentLength(byte[] bData, int iStart, int iEnd) {
//		int iPos = findContentLength(bData, iStart, iEnd);
//		if (iPos > 0) {
//			return filterContentLength(bData, iPos);
//		} else {
//			return -1;
//		}
//	}

	protected int getContentLength(byte[] bData, int iOff, int iEnd) {
		int iLen = 0;
		for (int i = iOff; i < iEnd - 14; i++) {
			if ((bData[i + 0] == 'c' || bData[i + 0] == 'C') && bData[i + 1] == 'o' && bData[i + 2] == 'n'
					&& bData[i + 3] == 't' && bData[i + 4] == 'e' && bData[i + 5] == 'n' && bData[i + 6] == 't'
					&& bData[i + 7] == '-' && (bData[i + 8] == 'L' || bData[i + 8] == 'l') && bData[i + 9] == 'e'
					&& bData[i + 10] == 'n' && bData[i + 11] == 'g' && bData[i + 12] == 't' && bData[i + 13] == 'h') {
				for (int j = i + 14; j <= iEnd; j++) {
					if (bData[j] >= (byte) '0' && bData[j] <= (byte) '9') {
						iLen *= 10;
						iLen += (bData[j] - '0');
					} else if (bData[j] == 0x0d) {
						break;
					}
				}
				break;
			}
		}
		return iLen;
	}
//
//	protected int findContentLength(byte[] bData, int iOff, int iEnd) {
//		int iIndex = -1;
//		for (int i = iOff; i < iEnd - 14; i++) {
//			if ((bData[i + 0] == 'c' || bData[i + 0] == 'C') && bData[i + 1] == 'o' && bData[i + 2] == 'n'
//					&& bData[i + 3] == 't' && bData[i + 4] == 'e' && bData[i + 5] == 'n' && bData[i + 6] == 't'
//					&& bData[i + 7] == '-' && (bData[i + 8] == 'L' || bData[i + 8] == 'l') && bData[i + 9] == 'e'
//					&& bData[i + 10] == 'n' && bData[i + 11] == 'g' && bData[i + 12] == 't' && bData[i + 13] == 'h') {
//				iIndex = i + 14;
//				break;
//			}
//		}
//		return iIndex;
//	}
//
//	protected int filterContentLength(byte[] ch, int iStart) {
//		int hz = 0;
//		for (int i = iStart; i < ch.length; i++) {
//			if (ch[i] >= (byte) '0' && ch[i] <= (byte) '9') {
//				hz *= 10;
//				hz += (ch[i] - '0');
//			} else if (ch[i] == 0x0d) {
//				break;
//			}
//		}
//		return hz;
//	}

	public boolean parseHttpHeader(byte[] bData, int iHeadLength) {
		boolean rs = false;
		int iMethod = ByteParser.indexOf(bData, 0, HttpHeader.fbCRLF);
		if (iMethod > 0) {
			while (iMethod + HttpHeader.fbCRLFCRLF.length < iHeadLength) {
				int iOff = iMethod + HttpHeader.fbCRLF.length;
				iMethod = ByteParser.indexOf(bData, iOff, HttpHeader.fbCRLF);
				if (iMethod > 0) {
					String sLine = StringAction.newString(bData, iOff, iMethod - iOff);
					if (sLine != null) {
						parseHeader(sLine);
					}
				}
			}
			rs = true;
		}
		return rs;
	}


}
