package com.china.fortune.socket;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.china.fortune.global.Log;

public class LineSocketAction {
	protected String sServerIP;
	protected int sServerPort;
	protected Socket mSocket = null;
	protected OutputStream outputStream = null;
	protected ReadLineBuffer inputStream = null;

	protected int iConnectTimeOut = 5 * 1000;
	protected int iRecvTimeOut = 5 * 1000;

	public void close() {
		if (mSocket != null) {
			closeInputStream();
			closeOutputStream();
			try {
				mSocket.close();
			} catch (Exception e) {
				Log.logError(e.getMessage());
			}
			mSocket = null;
		}
	}

	public void setSoLinger(boolean on, int linger) {
		try {
			if (mSocket != null) {
				mSocket.setSoLinger(on, linger);
			}
		} catch (Exception e) {
		}
	}

	protected boolean connect() {
		boolean hz = false;
		if (sServerIP != null) {
			close();
			try {
				InetSocketAddress socketAddress = new InetSocketAddress(sServerIP, sServerPort);
				mSocket = new Socket();
				mSocket.connect(socketAddress, iConnectTimeOut);
				if (mSocket != null) {
					mSocket.setSoTimeout(iRecvTimeOut);
					outputStream = mSocket.getOutputStream();
					inputStream = new ReadLineBuffer(mSocket.getInputStream());
					hz = true;
				}
			} catch (Exception e) {
				Log.logError(e.getMessage() + " " + sServerIP + ":" + sServerPort);
				mSocket = null;
				hz = false;
			}
		}
		return hz;
	}

	public boolean isConnected() {
		if (mSocket != null) {
			return mSocket.isConnected();
		}
		return false;
	}

	public boolean checkConnection() {
		boolean hz = true;
		if (!isConnected()) {
			if (!connect()) {
				hz = false;
			}
		}
		return hz;
	}

	// /// Millisecond
	public void setConTimeout(int iConnect, int iRecv) {
		if (iConnect >= 0) {
			iConnectTimeOut = iConnect;
		}
		if (iRecv >= 0) {
			iRecvTimeOut = iRecv;
		}
	}

	public void setSoTimeout(int iRecv) {
		if (iRecv >= 0) {
			iRecvTimeOut = iRecv;
		}
		if (mSocket != null) {
			try {
				mSocket.setSoTimeout(iRecvTimeOut);
			} catch (Exception e) {
			}
		}
	}

	public boolean connect(String ip, int port) {
		sServerIP = ip;
		sServerPort = port;
		return connect();
	}

	public void setServer(String ip, int port) {
		sServerIP = ip;
		sServerPort = port;
	}

	public void clear() {
		if (inputStream != null) {
			inputStream.reset();
		}
	}

	public boolean write(String sSend, String sCharset) {
		byte[] pData = null;
		try {
			pData = sSend.getBytes(sCharset);
		} catch (UnsupportedEncodingException e) {
		}
		if (pData != null) {
			return write(pData);
		}
		return false;
	}

	public boolean writeNoFlush(String sSend, String sCharset) {
		byte[] pData = null;
		try {
			pData = sSend.getBytes(sCharset);
		} catch (UnsupportedEncodingException e) {
		}
		if (pData != null) {
			return writeNoFlush(pData, pData.length);
		}
		return false;
	}

	public boolean write(byte[] pData, int iLeft) {
		boolean hz = true;
		try {
			outputStream.write(pData, 0, iLeft);
			outputStream.flush();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			hz = false;
		}
		return hz;
	}

	public void flush() {
		try {
			outputStream.flush();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
	public boolean writeNoFlush(byte[] pData) {
		boolean hz = true;
		try {
			outputStream.write(pData, 0, pData.length);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			hz = false;
		}
		return hz;
	}

	public boolean writeNoFlush(byte[] pData, int iLeft) {
		boolean hz = true;
		try {
			outputStream.write(pData, 0, iLeft);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			hz = false;
		}
		return hz;
	}

	public boolean write(byte[] pData) {
		return write(pData, pData.length);
	}

	public int read(byte[] pRecv) {
		return inputStream.read(pRecv, pRecv.length);
	}

	public int read(byte[] pRecv, int iLen) {
		return inputStream.read(pRecv, iLen);
	}

	public String readLine(String sCharset) {
		return inputStream.readLine(sCharset);
	}

	public void attach(Socket s) {
		if (s != null) {
			mSocket = s;
			try {
				outputStream = mSocket.getOutputStream();
				inputStream = new ReadLineBuffer(mSocket.getInputStream());
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}

	public void dettach() {
		closeInputStream();
		closeOutputStream();
		mSocket = null;
	}

	private void closeInputStream() {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
	}

	private void closeOutputStream() {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
			outputStream = null;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
}
