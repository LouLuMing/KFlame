package com.china.fortune.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;

public class SocketAction {
	static public final int CONNECT_ERR = 0;
	static public final int SEND_ERR = 1;
	static public final int RECV_ERR = 2;
	static public final int SENDRECV_OK = 3;

	protected String serverIP;
	protected int serverPort;
	protected Socket mSocket = null;
	protected OutputStream outputStream = null;
	protected InputStream inputStream = null;

	protected int iConnectTimeOut = 5 * 1000;
	protected int iRecvTimeOut = 5 * 1000;
	protected final int iOneSendLen = 4 * 1024;

	public boolean attach(Socket s) {
		boolean rs = false;
		if (s != null && s.isConnected()) {
			close();
			try {
				mSocket = s;
				mSocket.setSoTimeout(iRecvTimeOut);
				if (mSocket != null) {
					outputStream = mSocket.getOutputStream();
					inputStream = mSocket.getInputStream();
					rs = true;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
				closeInputStream();
				closeOutputStream();
				mSocket = null;
			}
		}
		return rs;
	}

	public void setSoLinger(boolean on, int linger) {
		try {
			if (mSocket != null) {
				mSocket.setSoLinger(on, linger);
			}
		} catch (Exception e) {
		}
	}

	private void closeInputStream() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			inputStream = null;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
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

	public void dettach() {
		if (mSocket != null) {
			closeInputStream();
			closeOutputStream();
		}
		mSocket = null;
	}

	public void close() {
		if (mSocket != null) {
			closeInputStream();
			closeOutputStream();
			try {
				mSocket.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			mSocket = null;
		}
	}

	public boolean connect() {
		boolean hz = false;
		if (serverIP != null) {
			close();
			InetSocketAddress socketAddress = new InetSocketAddress(serverIP,
					serverPort);
			try {
				mSocket = new Socket();
				mSocket.setSoTimeout(iRecvTimeOut);
				mSocket.connect(socketAddress, iConnectTimeOut);
				if (mSocket != null) {
					outputStream = mSocket.getOutputStream();
					inputStream = mSocket.getInputStream();
					hz = true;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
				mSocket = null;
				hz = false;
			}
		}
		return hz;
	}

	public boolean isConnected() {
		if (mSocket == null) {
			return false;
		} else {
			return mSocket.isConnected();
		}
	}

	public boolean checkConnection() {
		boolean hz = true;
		if (!isConnected()) {
			hz = connect();
		}
		return hz;
	}

	// /// Millisecond
	public void setTimeOut(int iConnect, int iRecv) {
		if (iConnect > 0 && iConnect < 180 * 1000) {
			iConnectTimeOut = iConnect;
		}
		if (iRecv > 0 && iRecv < 180 * 1000) {
			iRecvTimeOut = iRecv;
		}
	}

	public boolean connect(String ip, int port) {
		serverIP = ip;
		serverPort = port;
		return connect();
	}

	public void setServer(String ip, int port) {
		serverIP = ip;
		serverPort = port;
	}

	public void clearData() {
		if (inputStream != null) {
			try {
				inputStream.reset();
			} catch (IOException e) {
				Log.logClass(e.getMessage());
			}
		}
	}

	public boolean sendDataNoFlush(byte[] pData, int iData) {
		boolean hz = false;
		try {
			outputStream.write(pData, 0, iData);
			hz = true;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			close();
		}
		return hz;
	}

	public boolean sendDataNoFlush(String sData) {
		byte[] pData = StringUtils.getBytes(sData, "utf-8");
		return sendDataNoFlush(pData, pData.length);
	}

	public boolean sendDataNoFlush(byte[] pData) {
		return sendDataNoFlush(pData, pData.length);
	}

	public boolean sendData(byte[] pData, int iData) {
		boolean hz = false;
		try {
//			int iLeft = iData;
//			if (iLeft > pData.size) {
//				iLeft = pData.size;
//			}
//
//			int iiOff = 0;
//			while (iLeft > 0) {
//				int iActual = iLeft;
//				if (iActual > iOneSendLen)
//					iActual = iOneSendLen;
//
//				outputStream.write(pData, iiOff, iActual);
//				iiOff += iActual;
//				iLeft -= iActual;
//			}
//			outputStream.flush();
			outputStream.write(pData, 0, iData);
			outputStream.flush();
			hz = true;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
			close();
		}
		return hz;
	}

	public boolean sendData(String sData) {
		byte[] pData = StringUtils.getBytes(sData, "utf-8");
		return sendData(pData, pData.length);
	}

	public boolean sendData(byte[] pData) {
		return sendData(pData, pData.length);
	}

	public int recvData(byte[] pRecv, int iOff, int iLen) {
		int iVal = 0;
		try {
			iVal = inputStream.read(pRecv, iOff, iLen);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}

		return iVal;
	}

	public int recvData(byte[] pRecv) {
		int iVal = 0;
		try {
			iVal = inputStream.read(pRecv);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}

		return iVal;
	}

}
