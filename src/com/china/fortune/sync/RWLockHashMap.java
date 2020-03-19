package com.china.fortune.sync;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("serial")
public class RWLockHashMap<K, V> extends HashMap<K, V> {
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
