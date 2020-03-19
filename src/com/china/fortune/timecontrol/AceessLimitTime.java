package com.china.fortune.timecontrol;

import java.util.concurrent.ConcurrentHashMap;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.struct.IntObject;

// 一定时间内允许访问多少次
public class AceessLimitTime<K> extends TimeoutMapActionThreadSafe<K, IntObject> {
	private int iMaxAccessCount = 30;

	public AceessLimitTime(int iTimeOutPower, int iMaxAcess) {
		super(2, iTimeOutPower);
		iMaxAccessCount = iMaxAcess;
	}

	@Override
	public void onTimeout(ConcurrentHashMap<K, IntObject> map) {
		map.clear();
	}
	
	public void reset(K key) {
		for (int i = 0; i < iMapCount; i++) {
			IntObject ai = lsMap[i].get(key);
			if (ai != null) {
				ai.set(0);
			}
		}
	}

	public int count(K key) {
		int iTotal = 0;
		checkTimeout();
		int iNow = iMapIndex.get() & iMapCountModulo;
		if (key != null) {
			IntObject ai = lsMap[iNow].get(key);
			if (ai != null) {
				iTotal = ai.get();
			}
		}
		for (int i = 1; i < iMapCount; i++) {
			int index = (iNow + i) & iMapCountModulo;
			IntObject ai = lsMap[index].get(key);
			if (ai != null) {
				iTotal += ai.get();
			}
		}
		return iTotal;
	}
	
	public void access(K key) {
		int iNow = iMapIndex.get() & iMapCountModulo;
		if (key != null) {
			IntObject ai = lsMap[iNow].get(key);
			if (ai == null) {
				ai = new IntObject(1);
				lsMap[iNow].put(key, ai);
			} else {
				ai.incrementAndGet();
			}
		}
	}
	
	public int accessAndCount(K key) {
		int iTotal = 1;
		checkTimeout();
		int iNow = iMapIndex.get() & iMapCountModulo;
		if (key != null) {
			IntObject ai = lsMap[iNow].get(key);
			if (ai == null) {
				ai = new IntObject(1);
				lsMap[iNow].put(key, ai);
			} else {
				iTotal = ai.incrementAndGet();
			}
		}
		for (int i = 1; i < iMapCount; i++) {
			int index = (iNow + i) & iMapCountModulo;
			IntObject ai = lsMap[index].get(key);
			if (ai != null) {
				iTotal += ai.get();
			}
		}
		return iTotal;
	}

	public boolean isAllowAccess(K key) {
		return accessAndCount(key) < iMaxAccessCount;
	}

	public static void main(String[] args) {
		final AceessLimitTime<Integer> alt = new AceessLimitTime<Integer>(5, 30);
		for (int i = 0; i < 20; i++) {
			Thread t = new Thread() {
				@Override
				public void run() {
					int iLoop = 0;
					while (true) {
						int i = (iLoop++) & 0xf;
						Log.log(i + ":" + alt.isAllowAccess(i));
					}
				}
			};
			t.start();
		}
		while (true) {
			ThreadUtils.sleep(1000);
		}
	}
}
