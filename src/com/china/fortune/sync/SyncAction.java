package com.china.fortune.sync;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SyncAction  {
	private final Semaphore semObj = new Semaphore(0);
	
	public void reset() {
		int iPermits = semObj.availablePermits();
		if (iPermits > 0) {
			semObj.drainPermits();
		}
		else if (iPermits < 0) {
			semObj.release(-iPermits);
		}
	}
	
	// MILLISECONDS
	public boolean acquire(int iTimeOut) {
		boolean rs = false;
		try {
			rs = semObj.tryAcquire(1, iTimeOut, TimeUnit.MILLISECONDS);
		}
		catch(Exception ex) {
		}
		return rs;
	}
	
	public void release() {
		semObj.release();
	}
}
