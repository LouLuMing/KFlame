package com.china.fortune.timecontrol.newDay;

import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.thread.AutoIncreaseThreadPool;

public class IsNewSecond {
	private AtomicLong lLastDays = new AtomicLong(-1);

	public boolean isNew() {
		long iHour = System.currentTimeMillis() / 1000;
		boolean rs = false;
		// if (lLastDays.xyToIndex() < iHour) {
		// if (lLastDays.getAndSet(iHour) < iHour) {
		// rs = true;
		// }
		// }
		if (lLastDays.get() < iHour) {
			synchronized (this) {
				if (lLastDays.get() < iHour) {
					lLastDays.set(iHour);
					rs = true;
				}
			}
		}
		return rs;
	}

	public static void main(String[] args) {

		AutoIncreaseThreadPool threadPool = new AutoIncreaseThreadPool() {
			IsNewSecond ins = new IsNewSecond();

			@Override
			protected void doAction(Object obj) {
				if (ins.isNew()) {
					Log.log("isNew");
				}
			}

			@Override
			protected boolean haveThingsToDo(Object obj) {
				return true;
			}

			@Override
			protected void onDestroy(Object obj) {
			}

			@Override
			protected Object onCreate() {
				return null;
			}
		};

		threadPool.start(10, 10);
		while (true) {
			ThreadUtils.sleep(1000);
		}
	}
}
