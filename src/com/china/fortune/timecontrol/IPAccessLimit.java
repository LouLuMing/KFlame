package com.china.fortune.timecontrol;

import java.util.concurrent.ConcurrentHashMap;

//控制单次访问的间隔
public class IPAccessLimit {
	final private Object oValue = new Object();
	
	private TimeoutMapActionThreadSafe<Integer, Object> tmats = null;

	// 2 ^ (2 + 7) = 512, 0.5 second frequency
	public IPAccessLimit() {
		tmats = new TimeoutMapActionThreadSafe<Integer, Object>(2, 7) {
			@Override
			public void onTimeout(ConcurrentHashMap<Integer, Object> map) {
				map.clear();
			}
		};
	}
	
	public IPAccessLimit(int iTimeout) {
		tmats = new TimeoutMapActionThreadSafe<Integer, Object>(2, iTimeout) {
			@Override
			public void onTimeout(ConcurrentHashMap<Integer, Object> map) {
				map.clear();
			}
		};
	}
	
	public boolean isAllowedAccess(int ip) {
		tmats.checkTimeout();
		Object o = tmats.get(ip);
		if (o == null) {
			tmats.add(ip, oValue);
			return true;
		} else {
			return false;
		}
	}
}
