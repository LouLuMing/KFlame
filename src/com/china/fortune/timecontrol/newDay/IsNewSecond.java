package com.china.fortune.timecontrol.newDay;

import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.global.Log;
import com.china.fortune.thread.AutoThreadPool;
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

		AutoThreadPool threadPool = new AutoThreadPool() {
			IsNewSecond ins = new IsNewSecond();

			@Override
			protected boolean doAction(Object obj) {
				if (ins.isNew()) {
					Log.log("isNew");
				}
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

		threadPool.setThread(10, 10);
		threadPool.start();
		while (true) {
			ThreadUtils.sleep(1000);
		}
	}
}
