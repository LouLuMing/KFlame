package com.china.fortune.timecontrol;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

// 计算速度
public class IntCountAction {
	private int[] lsCount = null;
	// 2 ^ 2 = 4
	private int iMapCount = 4; 
	private int iMapCountModulo;
	
	private AtomicLong alNow = new AtomicLong(-1);
	private AtomicInteger aiIndex = new AtomicInteger(0);
	private AtomicInteger aiLen = new AtomicInteger(0);
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	public IntCountAction(int iPower) {
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		lsCount = new int[iMapCount];
		alNow.set(System.currentTimeMillis()/1000);
	}

	public boolean addAndCheck(int iData, int iMax) {
		long iHour = System.currentTimeMillis() /1000;
		int iLen = aiLen.addAndGet((int)(iHour - alNow.get() + 1));
		alNow.set(iHour);
		int iIndex = aiIndex.getAndIncrement() & iMapCountModulo;
		if (iLen > iMapCount) {
			iLen = iMapCount;
		}
		lsCount[iIndex] = iData;
		int iCount = 0;
		for (int i = 0; i < iLen; i++) {
			iIndex = (iIndex-1) & iMapCountModulo;
			if (lsCount[iIndex] == iData) {
				iCount++;
				if (iCount >= iMax) {
					return false;
				}
			}
		}
		return true;
	}

	public int add(int iData) {
		long iHour = System.currentTimeMillis() /1000;
		int iLen = aiLen.addAndGet((int)(alNow.get() - iHour + 1));
		alNow.set(iHour);
		int iIndex = aiIndex.getAndIncrement() & iMapCountModulo;
		if (iLen > iMapCount) {
			iLen = iMapCount;
		}
		lsCount[iIndex] = iData;
		int iCount = 0;
		for (int i = 0; i < iLen; i++) {
			iIndex = (iIndex-1) & iMapCountModulo;
			if (lsCount[iIndex] == iData) {
				iCount++;
			}
		}
		return iCount;
	}
	
	public static void main(String[] args) {
		IntCountAction ica = new IntCountAction(6);
		for (int i = 0; i < 10000; i++) {
			Log.log(ica.add(1000) + "");
			ThreadUtils.sleep(500);
		}
	}
}
