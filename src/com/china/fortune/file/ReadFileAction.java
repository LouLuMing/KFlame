package com.china.fortune.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.reflex.ClassSerialize;
import com.china.fortune.string.StringAction;

public class ReadFileAction {
	static final int iMaxDataLen = 16 * 1024 * 1024;
	private InputStream inSteam = null;

	public boolean isOpen() {
		return inSteam != null;
	}
	
	public boolean open(String fullfilename) {
		boolean hz = false;
		if (fullfilename != null) {
			try {
				if (inSteam != null) {
					inSteam.close();
					inSteam = null;
				}
				if (inSteam == null) {
					File f = new File(fullfilename);
					if (f.exists()) {
						inSteam = new FileInputStream(f);
						hz = true;
					} else {
						Log.logClass("Not Exists:" + fullfilename);
					}
				}
			} catch (Exception e) {
				Log.logClassError(e.getMessage());
			}
		}
		return hz;
	}

	public boolean openTail(String fullfilename) {
		boolean hz = false;
		if (fullfilename != null) {
			try {
				if (inSteam != null) {
					inSteam.close();
					inSteam = null;
				}
				if (inSteam == null) {
					File f = new File(fullfilename);
					if (f != null) {
						int iData = (int) f.length();
						inSteam = new FileInputStream(f);
						if (inSteam != null) {
							inSteam.skip(iData);
							hz = true;
						}
					}
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return hz;
	}

	public int read(byte[] bData, int iLen) {
		int iRead = 0;
		try {
			if (inSteam != null) {
				iRead = inSteam.read(bData, 0, iLen);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return iRead;
	}

	public void close() {
		try {
			if (inSteam != null) {
				inSteam.close();
				inSteam = null;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
	
	private int readHead() {
		int iData = -1;
		byte[] bData = new byte[4];
		try {
			int iReadLen = inSteam.read(bData, 0, 4);
			if (iReadLen == 4) {
				iData = ByteAction.byteToIntLE(bData);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return iData;
	}
	
	public String readString() {
		byte[] bData = readBytes();
		String sData = StringAction.toString(bData, ConstData.sFileCharset);
		//Log.logClass(sData);
		return sData;
	}
	
	public int readInt() {
		int iData = -1;
		byte[] bData = readBytes();
		if (bData != null && bData.length == 4) {
			iData = ByteAction.byteToIntLE(bData);
		}
		//Log.logClass("" + iData)
		return iData;
	}
	
	public int[] readInts() {
		int iSize = readInt();
		int[] lsData = null;
		if (iSize > 0) {
			if (lsData == null || lsData.length > iSize) {
				lsData = new int[iSize];
			}
			for (int i = 0; i < iSize; i++) {
				lsData[i] = readInt();
			}
		}
		return lsData;
	}
	
	public long[] readLongs() {
		int iSize = readInt();
		long[] lsData = null;
		if (iSize > 0) {
			if (lsData == null || lsData.length > iSize) {
				lsData = new long[iSize];
			}
			for (int i = 0; i < iSize; i++) {
				lsData[i] = readLong();
			}
		}
		return lsData;
	}
	
	public ArrayList<Integer> readArrayListInteger() {
		ArrayList<Integer> lsObj = null;
		int iSize = readInt();
		if (iSize > 0) {
			lsObj = new ArrayList<Integer>();
			for (int i = 0; i < iSize; i++) {
				lsObj.add(readInt());
			}
		}
		return lsObj;
	}
	
	public ArrayList<Long> readArrayListLong() {
		ArrayList<Long> lsObj = null;
		int iSize = readInt();
		if (iSize > 0) {
			lsObj = new ArrayList<Long>();
			for (int i = 0; i < iSize; i++) {
				lsObj.add(readLong());
			}
		}
		return lsObj;
	}
	
	public ArrayList<Object> readArrayListObject(Class<?> cls) {
		ArrayList<Object> lsObj = new ArrayList<Object>();
		int iSize = readInt();
		for (int i = 0; i < iSize; i++) {
			try {
				Object oo = cls.newInstance();
				ClassSerialize.loadObject(this, oo);
				lsObj.add(oo);
			} catch (Exception e) {
			}
		}
		return lsObj;
	}
	
	public ArrayList<String> readArrayListString() {
		ArrayList<String> lsObj = null;
		int iSize = readInt();
		if (iSize > 0) {
			lsObj = new ArrayList<String>();
			for (int i = 0; i < iSize; i++) {
				lsObj.add(readString());
			}
		}
		return lsObj;
	}
	
	public String[] readStrings() {
		String[] lsObj = null;
		int iSize = readInt();
		if (iSize > 0) {
			lsObj = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				lsObj[i] = readString();
			}
		}
		return lsObj;
	}
	
	public long readLong() {
		long iData = 0;
		byte[] bData = readBytes();
		if (bData != null && bData.length == 8) {
			iData = ByteAction.bytes2Long(bData);
		}
		//Log.logClass("" + iData);
		return iData;
	}

	public byte[] readBytes() {
		byte[] bBytes = null;
		try {
			int iData = readHead();
			if (iData > 0 && iData < iMaxDataLen) {
				byte[] bData = new byte[iData];
				int iReadLen = inSteam.read(bData, 0, iData);
				if (iReadLen > 0) {
					bBytes = bData;
				}
			}
		} catch (Exception e) {
			bBytes = null;
			Log.logClass(e.getMessage());
		}
		return bBytes;
	}
}
