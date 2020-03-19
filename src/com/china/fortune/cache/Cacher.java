package com.china.fortune.cache;

public class Cacher {
	protected void clear(){};

	protected long lActiveTime;
	
	public void setActiveTime(long l) {
		lActiveTime = l;
	}
	
	public long getActivtTime() {
		return lActiveTime;
	}
	
	public boolean isActive(long lNow, long lMilSecond) {
		return lNow - lActiveTime < lMilSecond;
	}
	
	public boolean isActive(long lMilSecond) {
		return System.currentTimeMillis() - lActiveTime < lMilSecond;
	}
	
	public boolean isInactive(long lNow, long lMilSecond) {
		return lNow - lActiveTime > lMilSecond;
	}
	
	public boolean isInactive(long lMilSecond) {
		return System.currentTimeMillis() - lActiveTime > lMilSecond;
	}
	
	public void access() {
		lActiveTime = System.currentTimeMillis();
	}
	
	
}
