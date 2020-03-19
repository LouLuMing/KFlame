package com.china.fortune.sync;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public class LockHashMap<K, V> extends HashMap<K, V> {
	private final ReentrantLock rl = new ReentrantLock(true);
	
	public LockHashMap() {
		super();
	}
	
	public LockHashMap(int iSize) {
		super(iSize);
	}
	
	public void lock() {
		rl.lock();
	}
	public void unlock() {
		rl.unlock();
	}
	
	public V lockPut(K k, V v) {
		V o = null;
		rl.lock();
		o = put(k, v);
		rl.unlock();
		return o;
	}
	
	public V lockRemove(K k) {
		V o = null;
		rl.lock();
		o = remove(k);
		rl.unlock();
		return o;
	}
	
	public V lockGet(K k) {
		V o = null;
		rl.lock();
		if (containsKey(k)) {
			o = get(k);
		}
		rl.unlock();
		return o;
	}
}
