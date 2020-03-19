package com.china.fortune.struct;

public class ByteAppend {
	private int iCapacity;
	private int iData;
	private byte[] bData = null;
	
	public ByteAppend(int capacity) {
		iCapacity = capacity;
		iData = 0;
		bData = new byte[iCapacity];
	}
	
	public void append(byte[] pSrc, int iLen) {
		int iActualLen = iLen;
		if (pSrc.length < iLen) {
			iActualLen = pSrc.length;
		}
		System.arraycopy(pSrc, 0, bData, iData, iActualLen);
		iData += iLen;
	}
	
	public void set(int iPos, byte[] pSrc, int iStart, int iLen) {
		int iActualLen = iLen;
		if (pSrc.length < iLen) {
			iActualLen = pSrc.length;
		}
		System.arraycopy(pSrc, iStart, bData, iPos, iActualLen);
	}
	
	public void set(int iPos, byte[] pSrc, int iLen) {
		set(iPos, pSrc, 0, iLen);
	}
	
	public byte[] getData() {
		return bData;
	}
	
	public int length() {
		return iData;
	}
	
	public void setPosition(int iPos) {
		iData = iPos;
	}
}
