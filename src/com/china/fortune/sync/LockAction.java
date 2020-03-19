package com.china.fortune.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LockAction  {
	private final ReentrantLock lock = new ReentrantLock(true);
	public boolean lock(int iTimeOut) {
		boolean rs = false;
		try {
			rs = lock.tryLock(iTimeOut, TimeUnit.MILLISECONDS);
		}
		catch(Exception ex) {
		}
		return rs;
	}
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		try {
			lock.unlock();
		}
		catch(Exception ex) {
		}
	}
}
