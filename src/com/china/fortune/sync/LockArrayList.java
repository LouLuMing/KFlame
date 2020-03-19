package com.china.fortune.sync;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public class LockArrayList<E> extends ArrayList<E> {
	private final ReentrantLock rl = new ReentrantLock(true);
	
	public void lock() {
		rl.lock();
	}
	public void unlock() {
		rl.unlock();
	}
	
	public boolean lockAdd(E e) {
		boolean rs = false;
		rl.lock();
		rs = add(e);
		rl.lock();
		return rs;
	}
}
