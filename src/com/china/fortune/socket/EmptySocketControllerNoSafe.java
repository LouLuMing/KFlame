package com.china.fortune.socket;

import java.nio.channels.SelectionKey;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public abstract class EmptySocketControllerNoSafe {
	private SelectionKey[][] lsArray = null;
	private int[] lsArrayIndex = null;
	private int iNowTicket = 0;
	// 2 ^ 2 = 4
	private int iArrayCount = 4; 
	private int iArraySize = 1024;
	private TimeoutAction ta = new TimeoutAction();
	// 2 ^ 12 = 4096
	private int iOneTimeOut;
	private int iOneTimeOutDiv;
	private int iArrayCountModulo;

	private int iHalfTimeout;
	abstract public void onTimeout(long lLimited, SelectionKey[] lsData, int iSize);
	
	// iCount = (1 << iPower); = iArrayCout
	// iOneTimeOut = (1 << iTimeOutPower);
	// Timeout = (iCount-1, iCount) * iOneTimeOut;
	// Mem = iCount * iPerCount;
	
	public EmptySocketControllerNoSafe(int iSizePower, int iTimeOutPower, int iPerCount) {
		iOneTimeOut = (1 << iTimeOutPower);
		iOneTimeOutDiv = iTimeOutPower;
		
		iArrayCount = (1 << iSizePower);
		iArrayCountModulo = iArrayCount - 1;
		iArraySize = iPerCount;

		iHalfTimeout = 1 << (iTimeOutPower + iSizePower - 1);

		lsArray = new SelectionKey[iArrayCount][];
		lsArrayIndex = new int[iArrayCount];
		for (int i = 0; i < iArrayCount; i++) {
			lsArray[i] = new SelectionKey[iArraySize];
		}
		
		ta.start();
		iNowTicket = 0;
	}
	
	public void add(SelectionKey key) {
		int iQueue = iNowTicket & iArrayCountModulo;
		int iIndex = lsArrayIndex[iQueue];
		if (iIndex < iArraySize) {
			SelectionKey[] lsNow = lsArray[iQueue];
			lsNow[iIndex] = key;
			lsArrayIndex[iQueue] = iIndex + 1;
		}
	}
	
	public int getQueueIndex() {
		int iQueue = iNowTicket & iArrayCountModulo;
		int iIndex = lsArrayIndex[iQueue];
		if (iIndex < iArraySize) {
			return iQueue;
		}
		return -1;
	}
	
	public void add(SelectionKey key, int iQueue) {
		int iIndex = lsArrayIndex[iQueue];
		SelectionKey[] lsNow = lsArray[iQueue];
		lsNow[iIndex] = key;
		lsArrayIndex[iQueue] = iIndex + 1;
	}
	
	public boolean haveSpace() {
		int iQueue = iNowTicket & iArrayCountModulo;
		int iIndex = lsArrayIndex[iQueue];
		if (iIndex < iArraySize) {
			return true;
		}
		return false;
	}
	
	public void checkTimeout() {
		int iMilSeconds = ta.getMilliseconds();
		if (iMilSeconds > iOneTimeOut) {
			long lLimited = ta.addStartTime(iMilSeconds) - iHalfTimeout;
			int iTimeOutCount = iMilSeconds >> iOneTimeOutDiv;
			if (iTimeOutCount > iArrayCount) {
				iTimeOutCount = iArrayCount;
			}
			for (int i = 0; i < iTimeOutCount; i++) {
				int iIndex = (iNowTicket + i + 1) & iArrayCountModulo;
				int iSize = lsArrayIndex[iIndex];
				if (iSize > 0) {
					onTimeout(lLimited, lsArray[iIndex], iSize);
					lsArrayIndex[iIndex] = 0;
				}
			}
			iNowTicket += iTimeOutCount;
		}
	}
	
	public static void main(String[] args) {
		final EmptySocketControllerNoSafe esc = new EmptySocketControllerNoSafe(5, 8, 1024 * 128) {
			@Override
			public void onTimeout(long lLimited, SelectionKey[] lsData, int iSize) {
				Log.log("" + iSize);
			}};
			
			Log.log("Start");
			LoopThread t = new LoopThread() {
				@Override
				protected boolean doAction() {
					esc.checkTimeout();
					esc.add(null);
					return true;
				}
				
			};
			t.start();
			
//			LoopThread r = new LoopThread() {
//				@Override
//				protected boolean doCrawler() {
//					esc.checkTimeout();
//					return true;
//				}
//				
//			};
//			r.start();
			
			while (true) {
				ThreadUtils.sleep(1000);
			}
	}
}
