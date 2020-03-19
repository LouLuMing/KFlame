package com.china.fortune.file;

import java.io.InputStream;
import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

public class ReadEnterBuffer {
	private InputStream inputStream = null;
	final private int iBufferSize = 1024;

	private byte[] pRecv = new byte[iBufferSize];
	private int iUsed = 0;

	public void close() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			inputStream = null;
		}
	}

	public void reset() {
		if (inputStream != null) {
			try {
				inputStream.reset();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		iUsed = 0;
	}

	public ReadEnterBuffer(InputStream inputStream) {
		this.inputStream = inputStream;
		iUsed = 0;
	}

	private void move(int iSub, int iLen) {
		if (iLen > 0 && iSub > 0) {
			System.arraycopy(pRecv, iSub, pRecv, 0, iLen);
		}
	}

	public int read(byte[] pBuf, int iLen) {
		int iCopy = 0;
		if (iUsed > 0) {
			iCopy = iUsed;
			if (pBuf.length < iUsed) {
				iCopy = pBuf.length;
			}
			System.arraycopy(pRecv, 0, pBuf, 0, iCopy);
			iUsed -= iCopy;
			move(iCopy, iUsed);
		}
		while (iCopy < iLen) {
			int iRecv = 0;
			try {
				iRecv = inputStream.read(pBuf, iCopy, pBuf.length - iCopy);
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			if (iRecv > 0) {
				iCopy += iRecv;
			} else {
				break;
			}
		}
		return iCopy;
	}

	private String newString(int iLen, String sCharset) {
		String strLine = null;
		if (sCharset != null) {
			try {
				strLine = new String(pRecv, 0, iLen, sCharset);
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		} else {
			strLine = new String(pRecv, 0, iLen);
		}	
		return strLine;
	}
	
	private String findLine(int iOff, int iLen, String sCharset) {
		String strLine = null;
		for (int i = 0; i < iLen; i++) {
			if (pRecv[i + iOff] == '\n') {
				if (i + iOff > 0) {
					strLine = newString(i+iOff, sCharset);
				} else {
					strLine = "";
				}
				iUsed -= (i + iOff + 1);
				move(i + iOff + 1, iUsed);
				break;
			}
		}
		return strLine;
	}

	private void largeBuffer() {
		byte[] pTmp = new byte[pRecv.length + iBufferSize];
		if (pTmp != null) {
			System.arraycopy(pRecv, 0, pTmp, 0, pRecv.length);
			pRecv = pTmp;
		}
	}

	private int iMaxRetry = 10;
	private int iSleepTime = 50;

	public void setRetry(int iRetry) {
		iMaxRetry = iRetry;
	}

	public String readLine(String sCharset) {
		String strLine = findLine(0, iUsed, sCharset);
		int iRetry = iMaxRetry;
		while (strLine == null) {
			int iRecv = 0;
			try {
				iRecv = inputStream.read(pRecv, iUsed, pRecv.length - iUsed);
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			if (iRecv > 0) {
				iRetry = iMaxRetry;
				iUsed += iRecv;
				strLine = findLine(iUsed - iRecv, iRecv, sCharset);
				if (strLine == null) {
					largeBuffer();
				}
			} else if (iRecv == 0) {
				ThreadUtils.sleep(iSleepTime);
				if (iRetry-- < 0) {
					break;
				}
			} else {
				if (iUsed > 0) {
					strLine = newString(iUsed, sCharset);
					iUsed = 0;
				}
				break;
			}
		}
		return strLine;
	}

	public String readLine() {
		return readLine(null);
	}

	static public ArrayList<String> readLines(InputStream is, String sCharset) {
		ArrayList<String> lsOutput = new ArrayList<String>();
		ReadEnterBuffer reb = new ReadEnterBuffer(is);
		try {
			do {
				String sLine = reb.readLine("utf-8");
				if (sLine != null) {
					lsOutput.add(sLine);
				} else {
					break;
				}
			} while (true);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		} finally {
			try {
				reb.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return lsOutput;
	}

	static public void readLines(InputStream is, String sCharset, ReadLinesInteface rli) {
		ReadEnterBuffer reb = new ReadEnterBuffer(is);
		try {
			do {
				String sLine = reb.readLine(sCharset);
				if (sLine == null || !rli.onRead(sLine)) {
					break;
				}
			} while (true);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		} finally {
			try {
				reb.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}
}
