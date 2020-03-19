package com.china.fortune.socket;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public abstract class EmptySocketController {
	private SelectionKey[][] lsArray = null;
	private int[] lsArrayIndex = null;
	private AtomicInteger iNowTicket = new AtomicInteger(0);
	// 2 ^ 2 = 4
	private int iArrayCount = 4; 
	private int iArraySize = 1024;
	private TimeoutAction ta = new TimeoutAction();
	// 2 ^ 12 = 4096
	//private int iOneTimeOut = 4096;
	private int iOneTimeOutDiv;
	private int iArrayCountModulo;

	abstract public void onTimeout(SelectionKey[] lsData, int iSize);
	
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	// iOneTimeOut = (1 << iTimeOutPower);
	// iCount = (1 << iPower);
	public EmptySocketController(int iPower, int iTimeOutPower, int iPerCount) {
		//iOneTimeOut = (1 << iTimeOutPower);
		iOneTimeOutDiv = iTimeOutPower;
		
		iArrayCount = (1 << iPower);
		iArrayCountModulo = iArrayCount - 1;
		iArraySize = iPerCount;
		
		ta.start();
		lsArray = new SelectionKey[iArrayCount][];
		lsArrayIndex = new int[iArrayCount];
		for (int i = 0; i < iArrayCount; i++) {
			lsArray[i] = new SelectionKey[iArraySize];
		}
		iNowTicket.getAndSet(ta.getMilliseconds() >> iOneTimeOutDiv);
	}
	
	public void add(SelectionKey key) {
		int iQueue = iNowTicket.get() & iArrayCountModulo;
		int iIndex = lsArrayIndex[iQueue];
		if (iIndex < iArraySize) {
			SelectionKey[] lsNow = lsArray[iQueue];
			lsNow[iIndex] = key;
			lsArrayIndex[iQueue] = iIndex + 1;
		}
	}
	
	public void checkTimeout() {
		int iNow = ta.getMilliseconds() >> iOneTimeOutDiv;
		int iLast = iNowTicket.getAndSet(iNow);
		int iTimeOutCount = (int)(iNow - iLast);
//		Log.log(iNow + ":" + iLast + ":" + iTimeOutCount);
		if (iTimeOutCount > 0) {
			if (iTimeOutCount > iArrayCount) {
				iTimeOutCount = iArrayCount;
			}
			for (int i = 0; i < iTimeOutCount; i++) {
				int iIndex = (iLast + i + 1) & iArrayCountModulo;
				int iSize = lsArrayIndex[iIndex];
				if (iSize > 0) {
					onTimeout(lsArray[iIndex], iSize);
					lsArrayIndex[iIndex] = 0;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		final EmptySocketController esc = new EmptySocketController(2, 10, 1024 * 1024) {
			@Override
			public void onTimeout(SelectionKey[] lsData, int iSize) {
				Log.log("" + iSize);
			}};
			
			LoopThread t = new LoopThread() {
				@Override
				protected boolean doAction() {
					esc.add(null);
					return true;
				}
				
			};
			t.start();
			
			LoopThread r = new LoopThread() {
				@Override
				protected boolean doAction() {
					esc.checkTimeout();
					return true;
				}
				
			};
			r.start();
			
			while (true) {
				ThreadUtils.sleep(1000);
			}
	}
}
