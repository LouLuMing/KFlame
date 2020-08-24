package com.china.fortune.statistics;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	private AtomicInteger aiCounter = new AtomicInteger(1);
	private AtomicInteger aiCommit = new AtomicInteger(0);

	public void set(int iMin, int iMax) {
		aiCounter.set(iMin);
		aiCommit.set(iMax - iMin + 1);
	}

	public Counter(int iMin, int iMax) {
		aiCounter.set(iMin);
		aiCommit.set(iMax - iMin + 1); 
	}
	
	public int get() {
		return aiCounter.getAndIncrement();
	}
	
	public void commit() {
		aiCommit.decrementAndGet();
	}
	
	public boolean isAllCommit() {
		return aiCommit.get() <= 0;
	}
}
