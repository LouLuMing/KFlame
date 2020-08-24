package com.china.fortune.thread;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

public abstract class LoopThread extends Thread {
	protected boolean bRunning = true;
	static final public int iOneHourMilSecond = 60 * 60 * 1000;
	static final public int iThreadSleepTime = 50;
	// true sleep
	// false no sleep
	abstract protected boolean doAction();

	protected void onClose() {};

	private int iSleepCount = 1;
	private int iSleepSpan = iThreadSleepTime;
	
	public void setSleep(int iMSecond, int iMilSecondSpan) {
		setSleepSpan(iMilSecondSpan);
		iSleepCount = iMSecond / iSleepSpan;
	}
	
	private void setSleepSpan(int iMilSecond) {
		iSleepSpan = iMilSecond;
		if (iSleepSpan < iThreadSleepTime) {
			iSleepSpan = iThreadSleepTime;
		} else if (iSleepSpan > iOneHourMilSecond) {
			iSleepSpan = iOneHourMilSecond;
		}
	}
	
	public boolean isRunning() {
		return bRunning;
	}

	@Override
	public void run() {
		while (bRunning) {
			try {
				if (doAction()) {
					for (int i = 0; bRunning && i < iSleepCount; i++) {
						Thread.sleep(iSleepSpan);
					}
				}
			} catch (Exception e) {
				Log.logException(e);
			} catch (Error e) {
				Log.logException(e);
			}
		}
		onClose();
	}

	public void setSleep(int iMSecond) {
		iSleepCount = iMSecond / iSleepSpan;
	}

	public void setStop() {
		bRunning = false;
	}

	public void waitToStop() {
		bRunning = false;
		try {
			this.join();
		} catch (Exception e) {
		}
	}
}
