package com.china.fortune.common;


import java.util.Base64;

public class ByteAction {
	// BIG-ENDIAN
	// LITTLE-ENDIAN
	static final private char HEX_DIGEST_UPPER[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };
	static final private char HEX_DIGEST_LOWER[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f' };
	
	static public void set(byte[] b, byte v) {
		for (int i = 0; i < b.length; i++) {
			b[i] = v;
		}
	}

	static public int copyByte(byte[] pSrc, int iSrc, byte[] pDes, int iDes, int iLen) {
		int iSrcLeft = pSrc.length - iSrc;
		int iCopyLen = iSrcLeft;
		if (iCopyLen > iLen) {
			iCopyLen = iLen;
		}
		System.arraycopy(pSrc, iSrc, pDes, iDes, iCopyLen);
		return iCopyLen;
	}

	final static private int byteToInt(byte[] bBuf, int iOffset, int iCount, boolean bLittleEndian) {
		int iData = 0;
		if (iCount + iOffset > bBuf.length) {
			iCount = bBuf.length - iOffset;
		}
		if (bLittleEndian) {
			for (int i = 0; i < iCount; i++) {
				iData <<= 8;
				iData += (bBuf[iCount + iOffset - i - 1]) & 0xff;
			}
		} else {
			for (int i = 0; i < iCount; i++) {
				iData <<= 8;
				iData += (bBuf[i + iOffset]) & 0xff;
			}
		}
		return iData;
	}

	final static private void intToByte(int iData, byte[] bBuf, int iOffset, int iCount, boolean bLittleEndian) {
		if (iCount + iOffset > bBuf.length) {
			iCount = bBuf.length - iOffset;
		}
		if (bLittleEndian) {
			for (int i = 0; i < iCount; i++) {
				bBuf[i + iOffset] = (byte) (iData & 0xff);
				iData >>= 8;
			}
		} else {
			for (int i = 0; i < iCount; i++) {
				bBuf[iCount + iOffset - i - 1] = (byte) (iData & 0xff);
				iData >>= 8;
			}
		}
	}

	static public byte[] intToByteLE(int n) {
		byte[] b = new byte[4];
		intToByte(n, b, 0, 4, true);
		return b;
	}

	static public void intToByteLE(int n, byte[] b) {
		intToByte(n, b, 0, 4, true);
	}

	static public void intToByteLE(int n, byte[] b, int i) {
		intToByte(n, b, i, 4, true);
	}

	static public void intToByteLE(int n, byte[] b, int i, int c) {
		intToByte(n, b, i, c, true);
	}

	static public int byteToIntLE(byte[] b) {
		return byteToInt(b, 0, 4, true);
	}

	static public int byteToIntLE(byte[] b, int i) {
		return byteToInt(b, i, 4, true);
	}

	static public int byteToIntLE(byte[] b, int i, int c) {
		return byteToInt(b, i, c, true);
	}

	static public byte[] intToByteBE(int n) {
		byte[] b = new byte[4];
		intToByte(n, b, 0, 4, false);
		return b;
	}

	static public void intToByteBE(int n, byte[] b) {
		intToByte(n, b, 0, 4, false);
	}

	static public void intToByteBE(int n, byte[] b, int i) {
		intToByte(n, b, i, 4, false);
	}

	static public int byteToIntBE(byte[] b) {
		return byteToInt(b, 0, 4, false);
	}

	static public int byteToIntBE(byte[] b, int i) {
		return byteToInt(b, i, 4, false);
	}

	static public int byteToIntBE(byte[] b, int i, int c) {
		return byteToInt(b, i, c, false);
	}

	static public String toHexString(byte[] bData, int iOff, int iCount) {
		StringBuilder sb = new StringBuilder(iCount * 2 + 1);
		for (int i = iOff; i < iOff + iCount; i++) {
			sb.append(HEX_DIGEST_UPPER[((bData[i] >> 4) & 0x0f)]);
			sb.append(HEX_DIGEST_UPPER[(bData[i] & 0x0f)]);
		}
		return sb.toString();
	}

	static public String toHexStringLower(byte[] bData, int iOff, int iCount) {
		StringBuilder sb = new StringBuilder(iCount * 2 + 1);
		for (int i = iOff; i < iOff + iCount; i++) {
			sb.append(HEX_DIGEST_LOWER[((bData[i] >> 4) & 0x0f)]);
			sb.append(HEX_DIGEST_LOWER[(bData[i] & 0x0f)]);
		}
		return sb.toString();
	}
	
	static public String toHexStringWithSpace(byte[] b) {
		StringBuilder sb = new StringBuilder();
		if (b.length > 0) {
			sb.append(HEX_DIGEST_UPPER[((b[0] >> 4) & 0x0f)]);
			sb.append(HEX_DIGEST_UPPER[(b[0] & 0x0f)]);

			for (int i = 1; i < b.length; i++) {
				sb.append(' ');
				sb.append(HEX_DIGEST_UPPER[((b[i] >> 4) & 0x0f)]);
				sb.append(HEX_DIGEST_UPPER[(b[i] & 0x0f)]);
			}
		}
		return sb.toString();
	}

	static public String toHexString(byte[] bData) {
		return toHexString(bData, 0, bData.length);
	}

	static public String toHexStringLower(byte[] bData) {
		return toHexStringLower(bData, 0, bData.length);
	}

	static public String toBase64(byte[] bData) {
		return Base64.getEncoder().encodeToString(bData);
	}

	static public byte HexDigistToByte(char c) {
		byte r = 0;
		if (c >= '0' && c <= '9') {
			r = (byte) (c - '0');
		} else if (c >= 'A' && c <= 'F') {
			r = (byte) (c - 'A' + 10);
		} else if (c >= 'a' && c <= 'f') {
			r = (byte) (c - 'a' + 10);
		}
		return r;
	}

	static public byte[] fromHexString(String sHex) {
		int iHex = sHex.length() / 2;
		byte[] bHex = new byte[iHex];
		char[] lsHex = sHex.toCharArray();
		for (int i = 0; i < iHex; i++) {
			bHex[i] = (byte) (((HexDigistToByte(lsHex[2 * i]) << 4) & 0xf0) + HexDigistToByte(lsHex[2 * i + 1]));
		}
		return bHex;
	}

	static public byte[] fromBCDString(String sHex) {
		return fromHexString(sHex);
	}

	static public byte[] append(byte[] a, byte[] b) {
		byte[] r = new byte[a.length + b.length];
		System.arraycopy(a, 0, r, 0, a.length);
		System.arraycopy(b, 0, r, a.length, b.length);
		return r;
	}

	static public byte[] fromString(String s, String sCode) {
		byte[] bData = null;
		try {
			bData = s.getBytes(sCode);
		} catch (Exception e) {
		}
		return bData;
	}

	static public byte[] formLong(byte[] bBuf, long l) {
		for (int i = 0; i < 8; i++) {
			bBuf[i] = (byte) (l & 0xff);
			l >>= 8;
		}
		return bBuf;
	}

	static public byte[] formLong(long l) {
		byte[] bBuf = new byte[8];
		for (int i = 0; i < 8; i++) {
			bBuf[i] = (byte) (l & 0xff);
			l >>= 8;
		}
		return bBuf;
	}

	static public boolean startWith(byte[] bData1, byte[] bData2) {
		boolean rs = false;
		if (bData1.length >= bData2.length) {
			rs = true;
			for (int i = 0; i < bData2.length; i++) {
				if (bData1[i] != bData2[i]) {
					rs = false;
					break;
				}
			}
		}
		return rs;
	}

	static public boolean byteCompare(byte[] bData1, int iOff, byte[] bData2) {
		boolean rs = false;
		if (bData1.length >= iOff + bData2.length) {
			rs = true;
			for (int i = 0; i < bData2.length; i++) {
				if (bData1[iOff + i] != bData2[i]) {
					rs = false;
					break;
				}
			}
		}
		return rs;
	}

	public static void long2Bytes(long iData, byte[] bBuf) {
		for (int i = 0; i < 8; i++) {
			bBuf[7 - i] = (byte) (iData & 0xff);
			iData >>= 8;
		}
	}
	
	public static byte[] long2Bytes(long iData) {
		byte[] bBuf = new byte[8];
		for (int i = 0; i < 8; i++) {
			bBuf[7 - i] = (byte) (iData & 0xff);
			iData >>= 8;
		}
		return bBuf;
	}

	public static long bytes2Long(byte[] bBuf) {
		long iData = 0;
		for (int i = 0; i < 8; i++) {
			iData <<= 8;
			iData += ((int) (bBuf[i]) & 0xff);
		}
		return iData;
	}

	public static int remove(byte[] bData, byte bChar) {
		int j = 0;
		for (int i = 0; i < bData.length; i++) {
			if (i == j) {

			} else {
				if (bData[i] != bChar) {
					bData[j] = bData[i];
					j++;
				}
			}
		}
		return j;
	}
}
