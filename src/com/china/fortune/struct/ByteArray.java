package com.china.fortune.struct;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.china.fortune.global.ConstData;

public class ByteArray {
	private byte[] bBuffer = null;

	public ByteArray(byte[] b) {
		bBuffer = b;
	}

	public ByteArray(String s) {
		try {
			bBuffer = s.getBytes(ConstData.scDefaultCoding);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other != null) {
			return Arrays.equals(bBuffer, ((ByteArray) other).bBuffer);
		}
		return false;
	}

	public boolean equals(byte[] bData) {
		return Arrays.equals(bBuffer, bData);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bBuffer);
	}

	public byte[] toBytes() {
		return bBuffer;
	}

	public int compare(ByteArray ba) {
		int iMin = bBuffer.length < ba.bBuffer.length ? bBuffer.length
				: ba.bBuffer.length;
		for (int i = 0; i < iMin; i++) {
			int iRs = bBuffer[i] - ba.bBuffer[i];
			if (iRs != 0) {
				return iRs;
			}
		}
		return bBuffer.length - ba.bBuffer.length;
	}

	public int compare(byte[] bData) {
		if (bData != null) {
			int iMin = bBuffer.length < bData.length ? bBuffer.length
					: bData.length;
			for (int i = 0; i < iMin; i++) {
				int iRs = bBuffer[i] - bData[i];
				if (iRs != 0) {
					return iRs;
				}
			}
			return bBuffer.length - bData.length;
		}
		return 1;
	}

	@Override
	public String toString() {
		String sResult = null;
		try {
			if (bBuffer != null) {
				sResult = new String(bBuffer, ConstData.scDefaultCoding);
			}
		} catch (UnsupportedEncodingException e) {
		}
		return sResult;
	}
}
