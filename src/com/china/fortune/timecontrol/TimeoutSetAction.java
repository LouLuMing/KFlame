package com.china.fortune.timecontrol;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class TimeoutSetAction<K> {
	private HashSet<K>[] lsMap = null;
	// 2 ^ 2 = 4
	private int iMapCount = 4; 
	private TimeoutAction ta = new TimeoutAction();
	// 2 ^ 12 = 4096
	//private int iOneTimeOut = 4096;
	private int iOneTimeOutDiv;
	private int iMapCountModulo;

	protected void onTimeout(HashSet<K> map){
		map.clear();
	};
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	public TimeoutSetAction(int iPower, int iTimeOutPower, int iPerCount) {
		//iOneTimeOut = (1 << iTimeOutPower);
		if (iPower < 2) {
			iPower = 2;
		}
		iOneTimeOutDiv = iTimeOutPower;
		
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		ta.start();
		lsMap = new HashSet[iMapCount];
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new HashSet<K>(iPerCount);
		}
		iNowTicket.set(ta.getMilliseconds() >> iOneTimeOutDiv);
	}
	
	public TimeoutSetAction(int iPower, int iTimeOutPower) {
		//iOneTimeOut = (1 << iTimeOutPower);
		iOneTimeOutDiv = iTimeOutPower;
		
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		ta.start();
		lsMap = new HashSet[iMapCount];
		for (int i = 0; i < iMapCount; i++) {
			lsMap[i] = new HashSet<K>();
		}
		iNowTicket.set(ta.getMilliseconds() >> iOneTimeOutDiv);
	}
	
	public void remove(K key) {
		for (HashSet<K> map : lsMap) {
			if (map.remove(key)) {
				break;
			}
		}
	}
	
	private AtomicInteger iNowTicket = new AtomicInteger(0);
	
	private int checkTimeout() {
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
	
	public void add(K sId) {
		checkTimeout();
		int iNow = iNowTicket.get() & iMapCountModulo;
		lsMap[iNow].add(sId);
	}
	
	public void addNoCheckTimeout(K sId) {
		int iNow = iNowTicket.get() & iMapCountModulo;
		lsMap[iNow].add(sId);
	}
	
	public void clear() {
		for (HashSet<K> map : lsMap) {
			map.clear();
		}
	}
	
	public boolean contains(K sId) {
		checkTimeout();
		for (HashSet<K> map : lsMap) {
			if (map.contains(sId)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean notEmpty() {
		for (HashSet<K> map : lsMap) {
			if (!map.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public int size() {
		int iSize = 0;
		for (HashSet<K> map : lsMap) {
			iSize += map.size();
		}
		return iSize;
	}
	
}
