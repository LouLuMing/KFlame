package com.china.fortune.timecontrol;

import java.util.concurrent.ConcurrentHashMap;

//类似速度，合并Key相同的，只计算一次
public class DistinctAceessLimitTime<K> extends TimeoutMapActionThreadSafe<K, Object> {
	public DistinctAceessLimitTime(int iTimeOutPower) {
		super(2, iTimeOutPower);
	}

	static private Object oValue = new Object();
	@Override
	public void onTimeout(ConcurrentHashMap<K, Object> map) {
		map.clear();
	}
	
	public void reset() {
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i].clear();
		}
	}
	
	public int accessAndCount(K key) {
		checkTimeout();
		int iMax = 0;
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i].put(key, oValue);
			int iNow = lsMap[i].size();
			if (iNow > iMax) {
				iMax = iNow;
			}
		}
		return iMax;
	}
}
