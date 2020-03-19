package com.china.fortune.timecontrol;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

// 计算速度
public class CountInSpanAction {
	private int[] lsCount = null;
	// 2 ^ 2 = 4
	private int iMapCount = 4; 
	private int iMapCountModulo;
	
	// 2 ^ 12 = 4096
	// private int iOneTimeOut = 4096;
	private int iOneTimeOutDiv;
	private int iOneTimeOut;
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	private void init(int iPower, int iTimeOutPower) {
		//iOneTimeOut = (1 << iTimeOutPower);
		iOneTimeOutDiv = iTimeOutPower;
		iOneTimeOut = (1 << iTimeOutPower);
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		lsCount = new int[iMapCount];
		
		iLastTime = System.currentTimeMillis();
		iBaseTime = System.currentTimeMillis();
		iMapIndex = 0;
	}
	
	public CountInSpanAction(int iPower, int iTimeOutPower) {
		init(iPower, iTimeOutPower);
		for (int i = 0; i < iMapCount; i++) {
			lsCount[i] = 0;
		}
	}

	
	private int iMapIndex = 0;
	private long iLastTime = 0;
	private long iBaseTime = 0;
	
	public int checkTimeout() {
		int iCount = 0;
		long iNowTime = System.currentTimeMillis();
		if (iNowTime - iLastTime > iOneTimeOut) {
			int iNowIndex = ((int)(iNowTime - iBaseTime)) >> iOneTimeOutDiv;		
			int iLastIndex = iMapIndex;
			iMapIndex = iNowIndex;
			int iTimeoutCount = iNowIndex - iLastIndex;
			if (iTimeoutCount > 0) {
				iLastTime += (iTimeoutCount << iOneTimeOutDiv);
				if (iTimeoutCount > iMapCount - 1) {
					iTimeoutCount = iMapCount - 1;
				}
				for (int i = 0; i < iTimeoutCount; i++) {
					int iIndex = (iLastIndex + i + 2) & iMapCountModulo;
					lsCount[iIndex] = 0;
				}
			}
		}
		return iCount;
	}

	public int increase() {
		checkTimeout();
		int iNow = iMapIndex & iMapCountModulo;
		lsCount[iNow]++;
		int iTotal = 0;
		for (int i = 0; i < lsCount.length; i++) {
			iTotal += lsCount[i];
		}
		return iTotal;
	}
	
	public void clear() {
		for (int i = 0; i < lsCount.length; i++) {
			lsCount[i] = 0;
		}
	}

	public static void main(String[] args) {
		CountInSpanAction cla = new CountInSpanAction(6, 10);
		while (true) {
			int i = cla.increase();
			Log.log("" + i);
			if (i > 60) {
				ThreadUtils.sleep(2500);
			}
			ThreadUtils.sleep(200);
		}
	}
}
