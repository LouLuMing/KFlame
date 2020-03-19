package com.china.fortune.common;

import java.io.InputStream;
import java.util.ArrayList;

public class ByteArrayInputStream extends InputStream {
	private ArrayList<byte[]> lsObj = new ArrayList<byte[]>();
	private int iObj = 0;	
	private int iOffset = 0;
	
	@Override
	public void close(){
		lsObj.clear();
	}
	
	public int length() {
		int i = 0;
		for (byte[] obj : lsObj) {
			if (obj != null) {
				i += obj.length;
			}
		}
		return i;
	}
	
	@Override
	public int read(byte[] b, int off, int len) {
		int iLeft = len;
		if (iLeft + off > b.length) {
			iLeft = b.length - off;
		}
		int iDes = off;
		while (iLeft > 0) {
			if (iObj < lsObj.size()) {
				byte[] obj = lsObj.get(iObj);
				if (obj.length > iOffset) {
					int iCopy = ByteAction.copyByte(obj, iOffset, b, iDes, iLeft);
					iOffset += iCopy;
					iLeft -= iCopy;
					iDes += iCopy;
				} else {
					iObj++;
					iOffset = 0;
				}
			} else {
				break;
			}
		}
		return len - iLeft;
	}
	
	@Override
	public int read(byte[] b) {
		return read(b, 0, b.length);
	}

	@Override
	public int read() {
		int i = 0;
		while (true) {
			if (iObj < lsObj.size()) {
				byte[] obj = lsObj.get(iObj);
				if (obj.length > iOffset) {
					i = obj[iOffset];
					iOffset++;
					break;
				} else {
					iObj++;
					iOffset = 0;
				}
			} else {
				break;
			}
		}
		return i;
	}

	public void reset() {
		iObj = 0;	
		iOffset = 0;		
	}
	
	public void add(byte[] obj) {
		lsObj.add(obj);
	}
	
	public void add(byte[] obj, int ilen) {
		if (ilen > 0) {
			if (ilen < obj.length) {
				byte[] pData = new byte[ilen];
				System.arraycopy(obj, 0, pData, 0, ilen);
				lsObj.add(pData);
			} else if (ilen == obj.length) {
				lsObj.add(obj);	
			}
		}
	}
	
	public byte[] getByte() {
		byte[] pData = null;
		int iLen = length();
		if (iLen > 0) {
			pData = new byte[iLen];
			int iOffset = 0;
			for (byte[] obj : lsObj) {
				if (obj != null) {
					System.arraycopy(obj, 0, pData, iOffset, obj.length);
					iOffset += obj.length;
				}
			}
		}
		return pData;
	}
	
	public String toString(String sCharset) {
		String rs = null;
		byte[] pData = getByte();
		try {
			if (pData != null && sCharset != null) {
				rs = new String(pData, sCharset);
			}
		} catch (Exception e) {
		}
		return rs;
	}
	
	public String toString() {
		String rs = null;
		byte[] pData = getByte();
		if (pData != null) {
			rs = new String(pData);
		}
		return rs;
	}
}
