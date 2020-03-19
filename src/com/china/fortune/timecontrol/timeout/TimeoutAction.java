package com.china.fortune.timecontrol.timeout;

final public class TimeoutAction {
	private long lstartTime = System.currentTimeMillis();
	private long ltimeout = 0;

	public TimeoutAction() {
	}

	public TimeoutAction(long timeOut) {
		ltimeout = timeOut;
	}

	public void setWaitTime(long timeOut) {
		ltimeout = timeOut;
		lstartTime = System.currentTimeMillis();
	}

	public void setWaitTimeAndStart(long timeOut) {
		ltimeout = timeOut;
		lstartTime = System.currentTimeMillis();
	}

	public boolean isTimeout() {
		if ((System.currentTimeMillis() - lstartTime) > ltimeout) {
			return true;
		}
		return false;
	}

	public boolean isTimeoutAndStart() {
		if ((System.currentTimeMillis() - lstartTime) > ltimeout) {
			lstartTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public void start() {
		lstartTime = System.currentTimeMillis();
	}

	public long addStartTime(int iTime) {
		lstartTime += iTime;
		return lstartTime;
	}

	// max 2^31 -1 / 1000 / 3600 / 24 = 24 days
	public int getMilliseconds() {
		return (int) (System.currentTimeMillis() - lstartTime);
	}
}
