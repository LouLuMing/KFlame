package com.china.fortune.timecontrol;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class TimeoutMapAction<K, V> {
	private HashMap<K, V>[] lsMap = null;
	// 2 ^ 2 = 4
	private int iMapCount = 4; 
	private TimeoutAction ta = new TimeoutAction();
	// 2 ^ 12 = 4096
	//private int iOneTimeOut = 4096;
	private int iOneTimeOutDiv;
	
	private int iMapCountModulo;
	

	protected void onTimeout(HashMap<K, V> map) {
		map.clear();
	};
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	public TimeoutMapAction(int iPower, int iTimeOutPower, int iPerCount) {
		//iOneTimeOut = (1 << iTimeOutPower);
		if (iPower < 2) {
			iPower = 2;
		}
		iOneTimeOutDiv = iTimeOutPower;
		
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		ta.start();
		lsMap = new HashMap[iMapCount];
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new HashMap<K, V>(iPerCount);
		}
		iNowTicket.set(ta.getMilliseconds() >> iOneTimeOutDiv);
	}
	
	public TimeoutMapAction(int iPower, int iTimeOutPower) {
		//iOneTimeOut = (1 << iTimeOutPower);
		iOneTimeOutDiv = iTimeOutPower;
		
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		ta.start();
		lsMap = new HashMap[iMapCount];
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new HashMap<K, V>();
		}
		iNowTicket.set(ta.getMilliseconds() >> iOneTimeOutDiv);
	}
	
	public void remove(K key) {
		for (HashMap<K, V> map : lsMap) {
			V obj = map.remove(key);
			if (obj != null) {
				break;
			}
		}
	}
	
	private AtomicInteger iNowTicket = new AtomicInteger(0);
	
	public int checkTimeout() {
		int iCount = 0;
		int iNow = ta.getMilliseconds() >> iOneTimeOutDiv;
		int iLast = iNowTicket.getAndSet(iNow);
		int iTimeOutCount = (int)(iNow - iLast);
		if (iTimeOutCount > 0) {
			if (iTimeOutCount > iMapCount) {
				iTimeOutCount = iMapCount;
			}
			for (int i = 0; i < iTimeOutCount; i++) {
				int iIndex = (iLast + i + 1) & iMapCountModulo;
				if (lsMap[iIndex].size() > 0) {
					iCount += lsMap[iIndex].size();
					onTimeout(lsMap[iIndex]);
				}
			}
		}
		
		return iCount;
	}
	
	public void add(K sId, V sMsg) {
		int iNow = iNowTicket.get() & iMapCountModulo;
		lsMap[iNow].put(sId, sMsg);
	}
	
	public void clear() {
		for (HashMap<K, V> map : lsMap) {
			map.clear();
		}
	}
	
	public V get(K sId) {
		V obj = null;
		int iNow = iNowTicket.get();
		for (int i = 0; i < lsMap.length; i++) {
			obj = lsMap[(i + iNow) & iMapCountModulo].get(sId);
			if (obj != null) {
				break;
			}
		}
//		for (HashMap<K, V> map : lsMap) {
//			obj = map.xyToIndex(sId);
//			if (obj != null) {
//				break;
//			}
//		}
		return obj;
	}
	
}
