package com.china.fortune.timecontrol;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

//统计两次访问的时间间隔
public class SpanInCount {
	private int[] lsCount = null;
	private int iMapCount = 4; 
	private int iMapCountModulo;
	private AtomicInteger iMapIndex = new AtomicInteger(0);
	
	// iCount = 1 << iPower;
	public SpanInCount(int iPower) {
		iMapCount = (1 << iPower);
		iMapCountModulo = iMapCount - 1;
		
		lsCount = new int[iMapCount];
		iMapIndex.set(0);
		for (int i = 0; i < iMapCount; i++) {
			lsCount[i] = 0;
		}
	}

	public int increase() {
		int iNow = iMapIndex.getAndIncrement() & iMapCountModulo;
		int iNowTicket = (int)System.currentTimeMillis();
		int iSpan = iNowTicket - lsCount[iNow];
		lsCount[iNow] = iNowTicket;
		return iSpan;
	}

	public static void main(String[] args) {
		SpanInCount cla = new SpanInCount(4);
		TimeoutAction ta = new TimeoutAction();
		ta.start();
		int count = 0;
		while (true) {
			int i = cla.increase();
			Log.log("" + ta.getMilliseconds() / (++count));
			if (i >= 0 && i < 1000) {
				ThreadUtils.sleep(100);
			}
		}
	}
}
