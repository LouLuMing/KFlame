package com.china.fortune.sync;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("serial")
public class RWLockArrayList<K> extends ArrayList<K> {
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
}
