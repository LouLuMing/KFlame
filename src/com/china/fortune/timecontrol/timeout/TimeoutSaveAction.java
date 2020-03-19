package com.china.fortune.timecontrol.timeout;

import com.china.fortune.data.CacheClass;
import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;

public class TimeoutSaveAction extends CacheClass {
	private long lstartTime = 0;
	private long ltimeout = 0;

	public TimeoutSaveAction(String sFileName) {
//		sSaveFileName = sFileName;
		setTag(sFileName);
		loadData();
	}

	public TimeoutSaveAction(String sFileName, long timeOut) {
		ltimeout = timeOut;
		setTag(sFileName);
		loadData();
	}

	public void setWaitTime(long timeOut) {
		ltimeout = timeOut;
	}

	public void setWaitTimeAndStart(long timeOut) {
		ltimeout = timeOut;
		lstartTime = System.currentTimeMillis();
		saveData();
	}

	public boolean isTimeout() {
		return (System.currentTimeMillis() - lstartTime) > ltimeout;
	}

	public boolean isTimeoutStart() {
		if (isTimeout()) {
			start();
			return true;
		} else {
			return false;
		}
	}
	
	public void start() {
		lstartTime = System.currentTimeMillis();
		saveData();
	}

	public void addStartTime(int iTime) {
		lstartTime += iTime;
	}

	// max 2^31 -1 / 1000 / 3600 / 24 = 24 days
	public int getMilliseconds() {
		return (int) (System.currentTimeMillis() - lstartTime);
	}

	@Override
	protected void onSave(WriteFileAction wfa) {
		wfa.writeLong(lstartTime);
	}

	@Override
	protected void onLoad(ReadFileAction rfa) {
		lstartTime = rfa.readLong();
	}
}
