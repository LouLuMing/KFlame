package com.china.fortune.timecontrol.timeout;

import java.util.concurrent.atomic.AtomicLong;

public class TimeoutActionThreadSafe {
	private long lTimeout = 0;
	private AtomicLong aLastTicket = new AtomicLong(System.currentTimeMillis());

	public TimeoutActionThreadSafe() {
	}

	public TimeoutActionThreadSafe(long timeOut) {
		lTimeout = timeOut;
	}

	public long getWaitTime() {
		return lTimeout;
	}

	public void setWaitTime(long timeOut) {
		lTimeout = timeOut;
		aLastTicket.set(System.currentTimeMillis());
	}

	public boolean isTimeout() {
		if (lTimeout > 0) {
			return (System.currentTimeMillis() - aLastTicket.get()) > lTimeout;
		} else {
			return false;
		}
	}

	public void start() {
		aLastTicket.set(System.currentTimeMillis());
	}

	public int getMilliseconds() {
		return (int) (System.currentTimeMillis() - aLastTicket.get());
	}

	public boolean isTimeoutAndReset() {
		if (lTimeout > 0) {
			long lNow = System.currentTimeMillis();
			int iSpan = (int) (lNow - aLastTicket.get());
			if (iSpan > lTimeout) {
				iSpan = (int) (lNow - aLastTicket.getAndSet(lNow));
				if (iSpan > lTimeout) {
					return true;
				}
			}
		}
		return false;
	}

	// max 2^31 -1 / 1000 / 3600 / 24 = 24 days
	public int getSpanIfTimeoutAndReset() {
		long iNow = System.currentTimeMillis();
		int iSpan = (int) (iNow - aLastTicket.get());
		if (iSpan > lTimeout) {
			iSpan = (int) (iNow - aLastTicket.getAndSet(iNow));
			if (iSpan > lTimeout) {
				return iSpan;
			}
		}
		return 0;
	}
	
}
