package com.china.fortune.timecontrol;

import java.util.concurrent.atomic.AtomicLong;

//计数器
public class CountAction {
	private long lStartTime = 0;
	private AtomicLong iCount = new AtomicLong(0);
	
	private long lLastTime = 0;
	private long lLastCount = 0;
	
	public void start() {
		iCount.set(0);
		lStartTime = 0;
	}
	
	public long getTime() {
		return (int)(System.currentTimeMillis() - lStartTime);
	}
	
	public long getCount() {
		return iCount.get();
	}
	
	public void increment() {
		iCount.getAndIncrement();
	}
	
	public void add(int delta) {
		iCount.addAndGet(delta);
	}
	
	public void setPoint() {
		lLastTime = System.currentTimeMillis();
		lLastCount = iCount.get();
	}
	
	public long getCountFromPoint() {
		return iCount.get() - lLastCount;
	}
	
	public long getTimeFromPoint() {
		return System.currentTimeMillis() - lLastTime;
	}
}
