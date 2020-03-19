package com.china.fortune.timecontrol;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TimeoutMapActionThreadSafe<K, V> {
	protected ConcurrentHashMap<K, V>[] lsMap = null;
	// 2 ^ 2 = 4
	protected int iMapCount = 4; 
	protected int iMapCountModulo;
	
	// 2 ^ 12 = 4096
	//private int iOneTimeOut = 4096;
	protected int iOneTimeOutDiv;
	protected int iOneTimeOut;
	abstract protected void onTimeout(ConcurrentHashMap<K, V> map);
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	protected void init(int iPower, int iTimeOutPower) {
		//iOneTimeOut = (1 << iTimeOutPower);
		if (iPower < 2) {
			iPower = 2;
		}
		iOneTimeOutDiv = iTimeOutPower;
		iOneTimeOut = (1 << iTimeOutPower);
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		lsMap = new ConcurrentHashMap[iMapCount];
		
		iLastTime = System.currentTimeMillis();
		iBaseTime = System.currentTimeMillis();
		iMapIndex.getAndSet(0);
	}
	
	public TimeoutMapActionThreadSafe(int iPower, int iTimeOutPower, int iPerCount) {
		init(iPower, iTimeOutPower);
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new ConcurrentHashMap<K, V>(iPerCount);
		}
	}
	
	public TimeoutMapActionThreadSafe(int iPower, int iTimeOutPower) {
		init(iPower, iTimeOutPower);
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new ConcurrentHashMap<K, V>();
		}
	}
	
	public void remove(K key) {
		for (ConcurrentHashMap<K, V> map : lsMap) {
			V obj = map.remove(key);
			if (obj != null) {
				break;
			}
		}
	}
	
	protected AtomicInteger iMapIndex = new AtomicInteger(0);
	private long iLastTime = 0;
	private long iBaseTime = 0;
	public int checkTimeout() {
		int iCount = 0;
		long iNowTime = System.currentTimeMillis();
		if (iNowTime - iLastTime > iOneTimeOut) {
			int iNowIndex = ((int)(iNowTime - iBaseTime)) >> iOneTimeOutDiv;		
			int iLastIndex = iMapIndex.getAndSet(iNowIndex);
			int iTimeoutCount = iNowIndex - iLastIndex;
			if (iTimeoutCount > 0) {
				iLastTime += (iTimeoutCount << iOneTimeOutDiv);
				if (iTimeoutCount > iMapCount) {
					iTimeoutCount = iMapCount;
				}
				for (int i = 0; i < iTimeoutCount; i++) {
					int iIndex = (iLastIndex + i + 2) & iMapCountModulo;
					if (lsMap[iIndex].size() > 0) {
						iCount += lsMap[iIndex].size();
						onTimeout(lsMap[iIndex]);
					}
				}
			}
		}
		return iCount;
	}
	
	public V add(K sId, V sMsg) {
		int iNow = iMapIndex.get() & iMapCountModulo;
		return lsMap[iNow].put(sId, sMsg);
	}
	
	public void clear() {
		for (ConcurrentHashMap<K, V> map : lsMap) {
			map.clear();
		}
	}
	
	public V get(K sId) {
		V obj = null;
		int iNow = iMapIndex.get();
		for (int i = 0; i < lsMap.length; i++) {
			obj = lsMap[(i + iNow) & iMapCountModulo].get(sId);
			if (obj != null) {
				break;
			}
		}
//		for (ConcurrentHashMap<K, V> map : lsMap) {
//			obj = map.xyToIndex(sId);
//			if (obj != null) {
//				break;
//			}
//		}
		return obj;
	}
	
	public int size() {
		int iCount = 0;
		for (ConcurrentHashMap<K, V> map : lsMap) {
			iCount += map.size();
		}
		return iCount;
	}

	public HashMap<K, V> getAll() {
		HashMap<K, V> mapAll = new HashMap<K, V>();
		for (ConcurrentHashMap<K, V> map : lsMap) {
			mapAll.putAll(map);
		}
		return mapAll;
	}
}
