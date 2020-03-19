package com.china.fortune.timecontrol.newDay;

import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.data.CacheClass;
import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;

public class IsNewDay extends CacheClass {
	private AtomicLong lLastDays = new AtomicLong(-1);
	private int iTimeZone = 8 * 3600 * 1000;

	public IsNewDay() {
		loadData();
	}
// 8 东八区
	public IsNewDay(int timeZone) {
		iTimeZone = timeZone * 3600 * 1000;
		loadData();
	}

	public IsNewDay(String sFileName) {
//		sSaveFileName = sFileName;
		setTag(sFileName);
		loadData();
	}
	
	public IsNewDay(String sFileName, int timeZone) {
//		sSaveFileName = sFileName;
		iTimeZone = timeZone * 3600 * 1000;
		setTag(sFileName);
		loadData();
	}
	
	public void setAlarm(int iHour) {
		iTimeZone -= (iHour * 3600 * 1000);
	}

	public void setAlarm(int iHour, int iMinute) {
		iTimeZone -= (iHour  * 3600 * 1000 + iMinute * 60 * 1000);
	}

	public void setAlarm(int iHour, int iMinute, int iSecond) {
		iTimeZone -= (iHour  * 3600 * 1000 + iMinute * 60 * 1000 + iSecond * 1000);
	}

	public boolean isNew() {
		int iNowD = (int) ((System.currentTimeMillis() + iTimeZone ) / (24 * 3600 * 1000));
		boolean rs = false;
		if (lLastDays.get() < iNowD) {
			if (lLastDays.getAndSet(iNowD) < iNowD) {
				saveData();
				rs = true;
			}
		}
		return rs;
	}

	@Override
	protected void onSave(WriteFileAction wfa) {
		wfa.writeLong(lLastDays.get());
	}

	@Override
	protected void onLoad(ReadFileAction rfa) {
		lLastDays.set(rfa.readLong());
	}

	public static void main(String[] args) {
		long iHour = (System.currentTimeMillis() / 3600000) + 0;
		IsNewDay ind = new IsNewDay("adf");
		Log.log(ind.isNew() + ":" + iHour + ":" + (iHour % 24));
		ind.saveData();
	}
}
