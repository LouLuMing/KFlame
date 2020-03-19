package com.china.fortune.xml;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

public class ByteParser {
	static public int findTag(byte[] b, int iOff, int iLen) {
		int iTag = -1;
		for (int i = iOff; i < iLen; i++) {
			if (b[i] == '<') {
				for (int j = i; j < iLen; j++) {
					if (b[j] != ' ') {
						iTag = j;
						break;
					}
				}
				break;
			}
		}
		return iTag;
	}

	static public int calSigner(byte[] b, int iOff, int iLen) {
		int iSigner = 0;
		for (int k = iOff; k < iLen; k++) {
			if (b[k] == ' ' || b[k] == '>' || b[k] == '/') {
				break;
			} else {
				iSigner += b[k];
			}
		}
		return iSigner;
	}
	
	static public int findTagAndCalSigner(byte[] b, int iOff, int iLen) {
		int iSigner = 0;
		for (int i = iOff; i < iLen; i++) {
			if (b[i] == '<') {
				int j = i + 1;
				for (; j < iLen; j++) {
					if (b[j] != ' ') {
						break;
					}
				}
				for (int k = j; k < iLen; k++) {
					if (b[k] == ' ' || b[k] == '>' || b[k] == '/') {
						break;
					} else {
						iSigner += b[k];
					}
				}
				break;
			}
		}
		return iSigner;
	}

	static public int findAttrAndCalSigner(byte[] b, int iOff, int iLen) {
		int iSigner = 0;
		int j = iOff;
		for (; j < iLen; j++) {
			if (b[j] != ' ') {
				break;
			}
		}
		for (int k = j; k < iLen; k++) {
			if (b[k] == ' ' || b[k] == '>' || b[k] == '/') {
				break;
			} else {
				iSigner += b[k];
			}
		}
		return iSigner;
	}

	static public int findFirstCharNotSpace(byte[] bArray, int iOff, int iLimit) {
		int iRs = -1;
		int iIndex = iOff;
		while (iIndex < iLimit - 1) {
			if (bArray[iIndex++] == ' ') {
				if (bArray[iIndex] != ' ') {
					iRs = iIndex;
					break;
				}
			}
		}
		return iRs;
	}

	static public int findFirstCharNotSpace(byte[] bArray, int iOff) {
		return findFirstCharNotSpace(bArray, iOff, bArray.length);
	}

	static public int findFirstCharNotSpaceUntilXmlTagEnd(byte[] bArray,
			int iOff, int iLimit) {
		int iRs = -1;
		for (int iIndex = iOff; iIndex < iLimit - 1; iIndex++) {
			if (bArray[iIndex] == ' ') {
				if (bArray[iIndex + 1] != ' ') {
					iRs = iIndex + 1;
					break;
				}
			} else if (bArray[iIndex] == '>') {
				break;
			}
		}
		return iRs;
	}
	
	static public int findFirstCharNotSpaceUntilXmlTagEnd(byte[] bArray,
			int iOff) {
		return findFirstCharNotSpaceUntilXmlTagEnd(bArray, iOff, bArray.length);
	}


	static public int lastIndexOf(byte[] bData, int iOff, byte[] bCompare) {
		int iIndex = -1;
		for (int i = iOff; i >= 0; i--) {
			boolean bFound = true;
			for (int j = 0; j < bCompare.length; j++) {
				if (bData[i + j] != bCompare[j]) {
					bFound = false;
					break;
				}
			}
			if (bFound) {
				iIndex = i;
				break;
			}
		}
		return iIndex;
	}

	static public int indexOf(byte[] bData, int iOff, int iEnd, byte[] bCompare) {
		int iIndex = -1;
		for (int i = iOff; i < iEnd - bCompare.length; i++) {
			boolean bFound = true;
			for (int j = 0; j < bCompare.length; j++) {
				if (bData[i + j] != bCompare[j]) {
					bFound = false;
					break;
				}
			}
			if (bFound) {
				iIndex = i;
				break;
			}
		}
		return iIndex;
	}
	
	static public int indexOf(byte[] bData, int iOff, byte[] bCompare) {
		return indexOf(bData, iOff, bData.length, bCompare);
	}

	static public int indexOf(byte[] bData, int iOff, int iEnd, byte bCompare) {
		for (int i = iOff; i < iEnd ; i++) {
			if (bData[i] == bCompare) {
				return i;
			}
		}
		return -1;
	}

	static public int indexOf(byte[] bData, int iOff, byte bCompare) {
		return indexOf(bData, iOff, bData.length, bCompare);
	}

	static public boolean compareKey(byte[] bData, int iOff, byte[] bCompare) {
		return compareKey(bData, iOff, bData.length, bCompare);
	}

	static public boolean compareKey(byte[] bData, int iOff, int iLimited,
			byte[] bCompare) {
		boolean rs = false;
		if (iLimited > iOff + bCompare.length) {
			rs = true;
			for (int i = 0; i < bCompare.length; i++) {
				if (bData[iOff + i] != bCompare[i]) {
					rs = false;
					break;
				}
			}
		}
		return rs;
	}

	static public int findKey(ByteBuffer bb, int iOff, byte[] bCompare) {
		int iRes = -1;
		int iIndex = iOff;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		do {
			if (compareKey(bArray, iIndex, iLimited, bCompare)) {
				iRes = iIndex;
				break;
			} else {
				iIndex++;
			}
		} while (iIndex < iLimited);
		return iRes;
	}

	static public boolean compareXmlAttributeValue(ByteBuffer bb, int iOff,
			byte[] bCompare) {
		boolean rs = false;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		if (iLimited > iOff + bCompare.length + 2) {
			rs = true;
			for (int i = iOff; i < iLimited; i++) {
				if (bArray[i] == '\'' || bArray[i] == '"') {
					iOff = i + 1;
					if (iOff + bCompare.length < iLimited) {
						for (int j = 0; j < bCompare.length; j++) {
							if (bArray[j + iOff] != bCompare[j]) {
								rs = false;
								break;
							}
						}
					}
					break;
				}
			}
		}
		return rs;
	}

	static public boolean compareXmlAttribute(ByteBuffer bb, int iOff,
			byte[] bCompare) {
		boolean rs = false;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		if (iLimited > iOff + bCompare.length + 2) {
			rs = true;
			for (int i = 0; i < bCompare.length; i++) {
				if (bArray[iOff + i] != bCompare[i]) {
					rs = false;
					break;
				}
			}
			if (rs) {
				int iTail = iOff + bCompare.length;
				if (bArray[iTail] == '='
						&& (bArray[iTail + 1] == '\'' || bArray[iTail + 1] == '"')) {
				} else {
					rs = false;
				}
			}
		}
		return rs;
	}

	static public byte[] findXmlAttributeAndGetValue(ByteBuffer bb, int iOff) {
		byte[] bResult = null;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		for (int j = iOff; j < iLimited; j++) {
			if (bArray[j] == '\'' || bArray[j] == '"') {
				byte bDot = bArray[j];
				int iStart = j + 1;
				for (int i = iStart; i < iLimited; i++) {
					if (bArray[i] == bDot) {
						int iLen = i - iStart;
						if (iLen > 0) {
							bResult = new byte[iLen];
							System.arraycopy(bArray, iStart, bResult, 0, iLen);
						}
						break;
					}
				}
				break;
			}
		}
		return bResult;
	}

	static public byte[] getXmlAttributeValue(ByteBuffer bb, int iOff) {
		byte[] bResult = null;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		byte bDot = bArray[iOff];
		int iStart = iOff + 1;
		for (int i = iStart; i < iLimited; i++) {
			if (bArray[i] == bDot) {
				int iLen = i - iStart;
				if (iLen > 0) {
					bResult = new byte[iLen];
					System.arraycopy(bArray, iStart, bResult, 0, iLen);
				}
				break;
			}
		}
		return bResult;
	}

	static public String getXmlAttributeValueToString(ByteBuffer bb, int iOff) {
		String sResult = null;
		byte[] bArray = bb.array();
		int iLimited = bb.limit();
		byte bDot = bArray[iOff];
		int iStart = iOff + 1;
		for (int i = iStart; i < iLimited; i++) {
			if (bArray[i] == bDot) {
				int iLen = i - iStart;
				if (iLen > 0) {
					try {
						sResult = new String(bArray, iStart, iLen,
								ConstData.scDefaultCoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
		return sResult;
	}

	final static public int getXmlAttributeLength(byte[] bKey, byte[] bValue) {
		if (bValue != null) {
			return bKey.length + bValue.length + 3;
		} else {
			return bKey.length + 3;
		}
	}

	final static public int getXmlAttributeLength(byte[] bKey, String sValue) {
		if (sValue != null) {
			return bKey.length + sValue.length() + 3;
		} else {
			return bKey.length + 3;
		}
	}

	final static public int indexOf(byte[] bToJid, byte bFind) {
		int iAt = -1;
		for (int i = 0; i < bToJid.length; i++) {
			if (bToJid[i] == bFind) {
				iAt = i;
				break;
			}
		}
		return iAt;
	}

	final static public int lastIndexOf(byte[] bToJid, byte bFind) {
		int iAt = -1;
		for (int i = bToJid.length - 1; i >= 0; i--) {
			if (bToJid[i] == bFind) {
				iAt = i;
				break;
			}
		}
		return iAt;
	}

	final static public byte[] subByte(byte[] bToJid, int iAt) {
		byte[] sToUid = new byte[iAt];
		System.arraycopy(bToJid, 0, sToUid, 0, iAt);
		return sToUid;
	}

	final static public byte[] subByteFrom(byte[] bToJid, int iAt) {
		int iLen = bToJid.length - iAt;
		byte[] sNameSpace = new byte[iLen];
		System.arraycopy(bToJid, iAt, sNameSpace, 0, iLen);
		return sNameSpace;
	}

	final static public byte[] subByte(byte[] bToJid, int iAt, int iLen) {
		byte[] sNameSpace = new byte[iLen];
		System.arraycopy(bToJid, iAt, sNameSpace, 0, iLen);
		return sNameSpace;
	}

	public static void showTagSigner(String sMsg) {
		try {
			byte[] bBody = sMsg.getBytes("utf-8");
			int iSigner = ByteParser.findTagAndCalSigner(bBody, 0, bBody.length);
			Log.logClass(iSigner + ":" + sMsg);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		showTagSigner("<message>");
		showTagSigner("<notify >");
		showTagSigner("<gpmsg ");
		showTagSigner("<lwmsg/> ");

		byte[] bData = "1234567890abcdefg".getBytes("ascii");
		int iMethod = indexOf(bData, 0, "abc".getBytes("ascii"));
		String sLine = new String(bData, 0, iMethod, "ascii");
		Log.log(sLine);

	}
}
