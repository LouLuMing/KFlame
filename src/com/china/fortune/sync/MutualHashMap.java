package com.china.fortune.sync;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MutualHashMap<K, V> {
	private final ReadWriteLock rwl = new ReentrantReadWriteLock();  
    private final Lock readLock = rwl.readLock();  
    private final Lock writeLock = rwl.writeLock();  
    
    HashMap<K, V> mainMap = null;
	HashMap<V, K> reveseMap = null;
	
	public MutualHashMap(int iSize) {
		mainMap = new HashMap<K, V>(iSize);
		reveseMap = new HashMap<V, K>(iSize);
	}
	
	public V getValue(K k) {
		V v = null;
		readLock.lock();
		if (mainMap.containsKey(k)) {
			v = mainMap.get(k);
		}
		readLock.unlock();
		return v;
	}
	
	public K getKey(V v) {
		K k = null;
		readLock.lock();
		if (reveseMap.containsKey(v)) {
			k = reveseMap.get(v);
		}
		readLock.unlock();
		return k;
	}
	
	public void add(K k, V v) {
		writeLock.lock();
		mainMap.put(k, v);
		reveseMap.put(v, k);
		writeLock.unlock();
	}
	
	public K deleteByValue(V v) {
		writeLock.lock();
		K k = reveseMap.remove(v);
		if (k != null) {
			mainMap.remove(k);
		}
		writeLock.unlock();
		return k;
	}
	
	public V deleteByKey(K k) {
		writeLock.lock();
		V v = mainMap.remove(k);
		if (k != null) {
			reveseMap.remove(v);
		}
		writeLock.unlock();
		return v;
	}
}
