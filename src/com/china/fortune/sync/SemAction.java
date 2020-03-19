package com.china.fortune.sync;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemAction  {
	private final int iSemp = 1;
	private final Semaphore semObj = new Semaphore(iSemp);
	
	public void reset() {
		int iPermits = semObj.availablePermits();
		if (iPermits > iSemp) {
			semObj.drainPermits();
			semObj.release(iSemp);
		} else if (iPermits < iSemp) {
			semObj.release(iSemp - iPermits);
		}
	}
	
	public boolean acquire(int iTimeOut) {
		return acquire(1, iTimeOut);
	}
	
	public boolean acquire(int iPermits, int iTimeOut) {
		boolean rs = false;
		try {
			rs = semObj.tryAcquire(iPermits, iTimeOut, TimeUnit.SECONDS);
		}
		catch(Exception ex) {
		}
		return rs;
	}
	
	public void release(int iPermits) {
		semObj.release(iPermits);
	}
	
	public void release() {
		semObj.release();
	}
}
