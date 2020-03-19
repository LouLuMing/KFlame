package com.china.fortune.myant;

import com.china.fortune.file.ReadLinesInteface;
import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketAction;

public class MyAntCommand {
	private String sServer = "127.0.0.1";
	private int iServer = 8000;
	private SocketAction sA = new SocketAction();

	private ReadLinesInteface defRli = null;
	
	private void initDefReadLines() {
		defRli = new ReadLinesInteface() {
			@Override
			public boolean onRead(String sLine) {
				Log.log("Recv:" + sLine);
				return true;
			}
		};
	}
	public MyAntCommand(String sIP, int iPort) {
		sServer = sIP;
		iServer = iPort;
		initDefReadLines();
	}
	
	public MyAntCommand(String sIP, int iPort, ReadLinesInteface rli) {
		sServer = sIP;
		iServer = iPort;
		if (rli != null) {
			defRli = rli;
		} else {
			initDefReadLines();
		}
	}

	private boolean sendOnly(String sText) {
		boolean rs = false;
		try {
			Log.log("Send:" + sText);
			String sSend = sText + "\r\n";
			rs = sA.sendData(sSend.getBytes("utf-8"));
		} catch (Exception e) {
		}
		return rs;
	}

	private void recvMore() {
		byte[] pRecv = new byte[1024];
		while (true) {
			try {
				int iRecv = sA.recvData(pRecv);
				if (iRecv > 2) {
					defRli.onRead(new String(pRecv, 0, iRecv - 2, "utf-8"));
				} else {
					// break;
				}
			} catch (Exception e) {
			}
		}
	}

	private boolean sendAndRecv(SocketAction sA, String sText) {
		byte[] pRecv = new byte[1024];
		boolean rs = false;
		try {
			Log.log("Send:" + sText);
			String sSend = sText + "\r\n";
			if (sA.sendData(sSend.getBytes("utf-8"))) {
				int iRecv = sA.recvData(pRecv);
				if (iRecv > 2) {
					defRli.onRead(new String(pRecv, 0, iRecv - 2, "utf-8"));
					rs = true;
				}
			}
		} catch (Exception e) {
		}
		return rs;
	}

	private boolean bIsLogin = false;

	private boolean checkLogin() {
		if (!bIsLogin) {
			if (sA.connect(sServer, iServer)) {
				if (sendAndRecv(sA, "Gato:Gundam0083")) {
					bIsLogin = true;
				}
			}
		}
		return bIsLogin;
	}

	public void close() {
		bIsLogin = false;
		sA.close();
	}

	public boolean sendCommand(String sCmd) {
		boolean rs = false;
		if (checkLogin()) {
			rs = sendAndRecv(sA, sCmd);
			if (!rs) {
				bIsLogin = false;
			}
		}
		return rs;
	}

	public boolean sendCommandAndRecvMore(String sCmd) {
		boolean rs = false;
		if (checkLogin()) {
			rs = sendOnly(sCmd);
			if (rs) {
				recvMore();
			}
		}
		return rs;
	}
}
