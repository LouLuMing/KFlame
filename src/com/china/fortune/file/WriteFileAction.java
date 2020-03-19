package com.china.fortune.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.reflex.ClassSerialize;
import com.china.fortune.string.StringAction;

public class WriteFileAction {
	private OutputStream fs = null;

	public boolean isOpen() {
		return fs != null;
	}

	public boolean open(String fullfilename) {
		return open(fullfilename, false);
	}
	
	public boolean open(String fullfilename, boolean bAppend) {
		boolean hz = false;
		try {
			if (fs != null) {
				fs.close();
				fs = null;
			}
			if (fs == null) {
				File f = new File(fullfilename);
				if (!f.exists()) {
					f.createNewFile();
				}
				fs = new FileOutputStream(f, bAppend);

				if (fs != null) {
					hz = true;
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
//			Log.logException(e);
		}

		return hz;
	}

	public void write(byte[] bData) {
		try {
			if (fs != null) {
				fs.write(bData, 0, bData.length);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void write(byte[] bData, int iLen) {
		try {
			if (fs != null) {
				fs.write(bData, 0, iLen);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void close() {
		try {
			if (fs != null) {
				fs.close();
				fs = null;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	private void writeHead(int iLen) {
		try {
			byte[] bData = new byte[4];
			ByteAction.intToByteLE(iLen, bData);
			fs.write(bData);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void writeString(String sData) {
		// Log.logClass(sData);
		byte[] bData = StringAction.getBytes(sData, ConstData.sFileCharset);
		writeBytes(bData);
	}

	public void writeInt(int iData) {
		// Log.logClass("" + iData);
		byte[] bData = new byte[4];
		ByteAction.intToByteLE(iData, bData);
		writeBytes(bData);
	}

	public void writeInts(int[] lsData) {
		int iSize = 0;
		if (lsData != null) {
			iSize = lsData.length;
		}
		writeInt(iSize);
		for (int i = 0; i < iSize; i++) {
			writeInt(lsData[i]);
		}
	}

	public void writeLongs(long[] lsData) {
		int iSize = 0;
		if (lsData != null) {
			iSize = lsData.length;
		}
		writeInt(iSize);
		for (int i = 0; i < iSize; i++) {
			writeLong(lsData[i]);
		}
	}

	public void writeStrings(String[] lsObj) {
		if (lsObj != null) {
			writeInt(lsObj.length);
			for (int i = 0; i < lsObj.length; i++) {
				writeString(lsObj[i]);
			}
		} else {
			writeInt(0);
		}
	}
	
	public void writeArrayListInteger(ArrayList<Object> lsObj) {
		writeInt(lsObj.size());
		for (int i = 0; i < lsObj.size(); i++) {
			writeInt((Integer) lsObj.get(i));
		}
	}

	public void writeArrayListLong(ArrayList<Object> lsObj) {
		writeInt(lsObj.size());
		for (int i = 0; i < lsObj.size(); i++) {
			writeLong((Long) lsObj.get(i));
		}
	}

	public void writeArrayListString(ArrayList<Object> lsObj) {
		writeInt(lsObj.size());
		for (int i = 0; i < lsObj.size(); i++) {
			writeString((String) lsObj.get(i));
		}
	}

	public void writeArrayListObject(ArrayList<Object> lsObj) {
		writeInt(lsObj.size());
		for (int i = 0; i < lsObj.size(); i++) {
			ClassSerialize.saveObject(this, lsObj.get(i));
		}
	}

	public void writeLong(long iData) {
		// Log.logClass("" + iData);
		byte[] bData = new byte[8];
		ByteAction.long2Bytes(iData, bData);
		writeBytes(bData);
	}

	public void writeBytes(byte[] bData) {
		try {
			int iData = 0;
			if (bData != null) {
				iData = bData.length;
			}
			writeHead(iData);
			if (iData > 0) {
				fs.write(bData, 0, iData);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
}
