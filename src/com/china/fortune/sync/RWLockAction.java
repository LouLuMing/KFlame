package com.china.fortune.sync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

public class RWLockAction {
	private final ReadWriteLock rwl = new ReentrantReadWriteLock();  
    private final Lock readLock = rwl.readLock();  
    private final Lock writeLock = rwl.writeLock();
    
    public void lockRead() {
    	readLock.lock();
    }
    
    public void unlockRead() {
    	readLock.unlock();
    }
    
    public void lockWrite() {
    	writeLock.lock();
    }
    
    public void unlockWrite() {
    	writeLock.unlock();
    }
    
    public static void main(String[] args) {
    	final RWLockAction rwl = new RWLockAction();
    	for (int i = 0; i < 5; i++) {
	    	Thread t = new Thread() {
	    		@Override
	    	    public void run() {
	    			rwl.lockRead();
	    			Log.logClass("rwl.lockRead() In");
	    			ThreadUtils.sleep(5000);
	    			Log.logClass("rwl.lockRead()");
	    			rwl.unlockRead();
	    		}
	    	};
	    	t.start();
    	}
    	Thread t = new Thread() {
    		@Override
    	    public void run() {
    			rwl.lockWrite();
    			Log.logClass("rwl.lockWrite()");
    			rwl.unlockWrite();
    		}
    	};
    	t.start();
    	
    }
}
