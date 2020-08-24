package com.china.fortune.messageServer;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.socket.intHead.IntBESocketAction;
import com.china.fortune.string.StringUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public abstract class Client {
	private IntBESocketAction tcpConnection = new IntBESocketAction();

	private TimeoutAction taSendTcp = new TimeoutAction();
	private TimeoutAction taRecvTcp = new TimeoutAction();

	abstract protected boolean onLoop();

	abstract protected boolean onLogin();

	abstract protected String getUid();

	abstract protected String getToken();

	abstract protected void onRecv(JSONObject json);

	private boolean bShowLog = true;

	public boolean isOK(JSONObject json) {
		boolean bOK = false;
		if (json.optInt("retcode") == 0) {
			bOK = true;
		} else {
			Log.logClass(json.optString("message"));
		}
		return bOK;
	}

	public boolean isOK(String sRecv) {
		JSONObject json = new JSONObject(sRecv);
		return isOK(json);
	}

	private void ackAndWork(String sRecv) {
		JSONObject data = parseJSON(sRecv);
		if (data != null) {
			int id = data.optInt("id");
			if (id > 0) {
				tcpConnection.sendString("/game/ack?id=" + id);
				onRecv(data);
			}
		}
	}

	private Thread tcpThread = null;
	private boolean bRunning = true;

	protected void stopThread() {
		bRunning = false;
		if (tcpThread != null) {
			try {
				tcpThread.join();
			} catch (Exception e) {
			}
			tcpThread = null;
		}
	}

	public void close() {
		stopThread();
	}
	
	public JSONObject parseJSON(String sRecv) {
		JSONObject data = null;
		if (sRecv != null) {
			if (bShowLog) {
				Log.logClass(sRecv);
			}
			JSONObject json = new JSONObject(sRecv);
			if (json.optInt("retcode") == 0) {
				data = json.optJSONObject("data");
			}
		}
		return data;
	}

	private boolean loginTcp() {
		boolean bLogin = false;
		UrlBuilder rb = new UrlBuilder("/game/login");
		addToken(rb);
		String sRecv = tcpConnection.sendAndRecv(rb.toString());
		if (sRecv != null) {
			bLogin = isOK(sRecv);
		}
		return bLogin;
	}

	private boolean tcpConnectAndLogin() {
		boolean bLogin = false;
		if (tcpConnection.connect()) {
			bLogin = loginTcp();
			if (!bLogin) {
				onLogin();
				bLogin = loginTcp();
			} else {
				taSendTcp.start();
				taRecvTcp.start();
			}
		}
		return bLogin;
	}

	private boolean doAction() {
		boolean bSleep = true;
		try {
			String sRecv = tcpConnection.recvStringAllowTimeout("utf-8");
			if (sRecv != null) {
				taRecvTcp.start();
				taSendTcp.start();
				ackAndWork(sRecv);
				bSleep = false;
			} else if (taSendTcp.isTimeout()) {
				taSendTcp.start();
				tcpConnection.sendString("/game/live");
			} else if (taRecvTcp.isTimeout()) {
				tcpConnectAndLogin();
			}
		} catch (Exception e) {
			tcpConnectAndLogin();
			bSleep = false;
		}
		return bSleep;
	}

	private void startThread() {
		stopThread();
		bRunning = true;
		tcpThread = new Thread() {
			@Override
			public void run() {
				int iLoginError = 0;
				while (bRunning) {
					boolean bSleep = true;
					if (checkToken()) {
						iLoginError = 0;
						onLoop();
						bSleep = doAction();
					} else {
						iLoginError++;
					}
					if (bSleep) {
						if (iLoginError == 0) {
							ThreadUtils.sleep(500);
						} else {
							for (int i = 0; i < iLoginError && i < 20; i++) {
								for (int j = 0; bRunning && j < 3; j++) {
									ThreadUtils.sleep(500);
								}
							}
						}
					}
				}
			}
		};
		tcpThread.start();
	}

	public Client(String s, int i) {
		tcpConnection.setTimeOut(500, 500);
		taSendTcp.setWaitTime(30 * 1000);
		taRecvTcp.setWaitTime(60 * 1000);
		tcpConnection.setServer(s, i);
	}

	public void setAliveTcp(int iSecond) {
		taSendTcp.setWaitTime(iSecond * 1000);
		taRecvTcp.setWaitTime(iSecond * 2000);
	}

	protected void addToken(UrlBuilder rb) {
		rb.add("userId", getUid());
		rb.add("token", getToken());
	}

	public void start() {
		startThread();
	}

	public void showLog(boolean bShow) {
		bShowLog = bShow;
	}

	private boolean isTokenValid() {
		return StringUtils.length(getUid()) > 0 && StringUtils.length(getToken()) > 0;
	}

	private boolean checkToken() {
		boolean rs = false;
		if (isTokenValid()) {
			rs = true;
		} else {
			rs = onLogin();
		}
		return rs;
	}

}
