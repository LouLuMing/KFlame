package com.china.fortune.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import com.china.fortune.global.Log;

public class ReadLineBuffer {
	private InputStream inputStream;
	final private int iBufferSize = 1024;

	private byte[] pRecv = new byte[iBufferSize];
	private int iUsed = 0;

	public void close() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void reset() {
		try {
			if (inputStream != null) {
				inputStream.reset();
				iUsed = 0;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public ReadLineBuffer(InputStream inputStream) {
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
		int iRetry = 0;
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
			} catch (SocketTimeoutException e) {
			} catch (IOException e) {
				Log.logClass(e.getMessage());
				iCopy = -1;
				break;
			}
			if (iRecv > 0) {
                iRetry = 0;
				iCopy += iRecv;
			} else {
				if (++iRetry > iMaxRetry) {
					break;
				}
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
				if (i + iOff - 1 >= 0) {
					if (pRecv[i + iOff - 1] == '\r') {
						if (i + iOff - 1 > 0) {
							strLine = newString(i + iOff - 1, sCharset);
						} else {
							strLine = "";
						}
						iUsed -= (i + iOff + 1);
						move(i + iOff + 1, iUsed);
						break;
					}
				}
			}
		}
		return strLine;
	}

	private void largeBuffer() {
		byte[] pTmp = new byte[pRecv.length + iBufferSize];
		System.arraycopy(pRecv, 0, pTmp, 0, pRecv.length);
		pRecv = pTmp;
	}

	private final int iMaxRetry = 3;

	public String readLine(String sCharset) {
		String strLine = findLine(0, iUsed, sCharset);
		int iRetry = 0;
		while (strLine == null) {
			int iRecv = 0;
			try {
				iRecv = inputStream.read(pRecv, iUsed, pRecv.length - iUsed);
			} catch (SocketTimeoutException e) {
			} catch (IOException e) {
				Log.logClass(e.getMessage());
				break;
			}
			if (iRecv > 0) {
				iRetry = 0;
				iUsed += iRecv;
				strLine = findLine(iUsed - iRecv, iRecv, sCharset);
				if (strLine == null) {
					largeBuffer();
				}
			} else if (iRecv == 0) {
                if (++iRetry > iMaxRetry) {
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
}
