package com.china.fortune.socket.intHead;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketAction;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class IntBESocketAction extends SocketAction {
	static private String sCharset = ConstData.sSocketCharset;
	static private int ciMaxDataLen = 16 * 1024 * 1024;

	static public void setCharset(String sCode) {
		sCharset = sCode;
	}

	public boolean sendString(String sData) {
		return sendString(sData, sCharset);
	}

	public boolean sendString(String sData, String sCode) {
		boolean hz = true;
		try {
			byte[] pData = sData.getBytes(sCode);
			byte[] pLen = ByteAction.intToByteBE(pData.length);

			// outputStream.write(pLen);
			// outputStream.write(pData);

			byte[] pAll = ByteAction.append(pLen, pData);
			outputStream.write(pAll);
			outputStream.flush();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			hz = false;
		}

		return hz;
	}

	public String recvString() {
		return recvString(sCharset);
	}

	public String recvStringAllowTimeout(String sCode) throws IOException {
		String sRecv = null;
		byte[] pLen = new byte[4];
		int iVal = 0;
		try {
			iVal = inputStream.read(pLen);
		} catch (SocketTimeoutException e) {
		}
		if (iVal == 4) {
			int iLeft = ByteAction.byteToIntBE(pLen);
			if (iLeft > 0 && iLeft < ciMaxDataLen) {
				int iOff = 0;
				byte[] pData = new byte[iLeft];
				while (iLeft > 0 && iVal >= 0) {
					iVal = inputStream.read(pData, iOff, iLeft);
					iOff += iVal;
					iLeft -= iVal;
				}
				if (iLeft == 0) {
					try {
						sRecv = new String(pData, sCode);
					} catch (UnsupportedEncodingException e) {
					}
				}
			}
		}
		return sRecv;
	}

	public String recvString(String sCode) {
		String sRecv = null;
		byte[] pLen = new byte[4];
		try {
			int iVal = inputStream.read(pLen);
			if (iVal == 4) {
				int iLeft = ByteAction.byteToIntBE(pLen);
				if (iLeft > 0 && iLeft < ciMaxDataLen) {
					int iOff = 0;
					byte[] pData = new byte[iLeft];
					while (iLeft > 0 && iVal >= 0) {
						iVal = inputStream.read(pData, iOff, iLeft);
						iOff += iVal;
						iLeft -= iVal;
					}

					if (iLeft == 0) {
						sRecv = new String(pData, sCode);
					}
				}
			}
		} catch (SocketTimeoutException e) {
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}

		return sRecv;
	}

	// second
	public String sendAndRecv(String sData) {
		String sRecv = null;
		if (checkConnection()) {
			if (sendString(sData)) {
				sRecv = recvString();
			}
		}
		return sRecv;
	}

	public String sendAndRecv(String sData, int iTimeOut, int iRetry) {
		String sRecv = null;
		TimeoutAction timeoutObj = new TimeoutAction();
		timeoutObj.setWaitTime(iTimeOut * 1000);
		timeoutObj.start();
		do {
			if (checkConnection()) {
				if (sendString(sData)) {
					sRecv = recvString();
					if (sRecv != null) {
						break;
					}
				}
			}
		} while (!timeoutObj.isTimeout() && iRetry-- > 0);
		return sRecv;
	}
}
