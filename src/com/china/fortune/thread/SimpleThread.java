package com.china.fortune.thread;

import com.china.fortune.global.Log;

public abstract class SimpleThread extends Thread {
	protected boolean bRunning = true;
	
	// true sleep
	// false no sleep
	abstract protected boolean doAction();

	protected void onClose() {};

	@Override
	public void run() {
		while (bRunning) {
			try {
				bRunning = doAction();
			} catch (Exception e) {
				Log.logException(e);
			} catch (Error e) {
				Log.logException(e);
			}
		}
		onClose();
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
