package com.china.fortune.sync;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.os.log.LogAction;
import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.LoopThreadList;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class testQueue {
	public static void main(String[] args) {
		Log.init(LogAction.iConsole);
		Log.setLog("Z:\\", "ConcurrentLinkedQueue");
		final int iMaxObject = 900000;
		final AtomicBoolean bRun = new AtomicBoolean(true);
		int iThread = 24;
		TimeoutAction ta = new TimeoutAction();
		ta.start();
		final EnConcurrentLinkedQueue <Integer> lsObj = new EnConcurrentLinkedQueue <Integer>(14);
		LoopThreadList lt = new LoopThreadList();
		final AtomicInteger iIndex = new AtomicInteger(0);
		//for (int i = 0; i < iThread; i++) {
			LoopThread add = new LoopThread() {
				@Override
				protected boolean doAction() {
					if (iIndex.get() < iMaxObject) {
						if (lsObj.haveSpace()) {
							lsObj.add(iIndex.getAndIncrement());
							return false;
						}
					}
					return true;
				}
			};
			lt.add(add);
		//}
		
		for (int i = 0; i < iThread; i++) {
			LoopThread poll = new LoopThread() {
				@Override
				protected boolean doAction() {
					if (!lsObj.isEmpty()) {
						Integer obj = lsObj.poll();
						if (obj != null) {
							//System.out.println(String.valueOf(obj));
							//Log.log(String.valueOf(obj));
							if (obj >= iMaxObject - 1) {
								bRun.set(false);
							}
							return false;
						}
					}
					return true;
				}
			};
			lt.add(poll);
		}
		lt.start();
		
		while (bRun.get()) {
			ThreadUtils.sleep(1000);
		}
		
		System.out.println(String.valueOf(ta.getMilliseconds()));
		lt.waitToStop();
	}
}
