package com.china.fortune.timecontrol;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

//计算速度
//统计访问数量，通过数组来平滑过渡
public class CountInSpanActionThreadSafe {
	private AtomicInteger[] lsCount = null;
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
		
		lsCount = new AtomicInteger[iMapCount];
		
		iLastTime = System.currentTimeMillis();
		iBaseTime = System.currentTimeMillis();
		iMapIndex.getAndSet(0);
	}
	
	public CountInSpanActionThreadSafe(int iPower, int iTimeOutPower) {
		init(iPower, iTimeOutPower);
		for (int i = 0; i < iMapCount; i++) {
			lsCount[i] = new AtomicInteger(0);
		}
	}

	
	private AtomicInteger iMapIndex = new AtomicInteger(0);
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
				if (iTimeoutCount > iMapCount - 1) {
					iTimeoutCount = iMapCount - 1;
				}
				for (int i = 0; i < iTimeoutCount; i++) {
					int iIndex = (iLastIndex + i + 2) & iMapCountModulo;
					lsCount[iIndex].set(0);
				}
			}
		}
		return iCount;
	}

	public int increase() {
		checkTimeout();
		int iNow = iMapIndex.get() & iMapCountModulo;
		lsCount[iNow].incrementAndGet();
		int iTotal = 0;
		for (AtomicInteger count : lsCount) {
			iTotal += count.get();
		}
		return iTotal;
	}
	
	public void clear() {
		for (AtomicInteger count : lsCount) {
			count.set(0);
		}
	}

	public static void main(String[] args) {
		CountInSpanActionThreadSafe cla = new CountInSpanActionThreadSafe(3, 10);
		TimeoutAction ta = new TimeoutAction();
		ta.start();
		int iCount = 0;
		while (true) {
			iCount++;
			int i = cla.increase();
			Log.log(i + ":" + iCount + ":" + ta.getMilliseconds() / iCount);
			if (i > 32) {
				ThreadUtils.sleep(1000);
			}
//			SleepHelper.sleep(100);
		}
	}
}
