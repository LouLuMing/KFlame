package com.china.fortune.struct.error;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.LoopThreadList;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class testSafeQueue {
	private int ciAddThread = 2;
	private int ciPollThread = 2;

	private AtomicInteger iValue = new AtomicInteger(0);
	private AtomicInteger iAddThread = new AtomicInteger(ciAddThread);
	private AtomicInteger iAddObjects = new AtomicInteger(0);
	private AtomicInteger iPollObjects = new AtomicInteger(0);

//	 private EnConcurrentLinkedQueue<Integer> lsObj = new
//	 EnConcurrentLinkedQueue<Integer>(
//	 10);
	private SafeQueue<Integer> lsObj = new SafeQueue<Integer>(10);

	public boolean safeQueue() {
		final int iMaxObject = 190000;
		TimeoutAction ta = new TimeoutAction();
		ta.start();
		lsObj.setFaultTolerant(ciAddThread + ciPollThread);

		LoopThreadList lt = new LoopThreadList();
		for (int i = 0; i < ciAddThread; i++) {
			LoopThread add = new LoopThread() {
				@Override
				protected boolean doAction() {
					boolean rs = true;
					if (lsObj.haveSpace()) {
						int in = iValue.getAndIncrement();
						if (in < iMaxObject) {
							iAddObjects.getAndIncrement();
							lsObj.addUntilSuccess(in);
							rs = false;
						} else {
							iAddThread.decrementAndGet();
							this.setStop();
						}
					}
					return rs;
				}
			};
			lt.add(add);
		}

		for (int i = 0; i < ciPollThread; i++) {
			LoopThread poll = new LoopThread() {
				@Override
				protected boolean doAction() {
					boolean rs = true;
					Integer obj = lsObj.poll();
					if (obj != null) {
						iPollObjects.getAndIncrement();
						rs = false;
					} else if (iAddThread.get() == 0) {
						this.setStop();
					}
					return rs;
				}
			};

			lt.add(poll);
		}
		lt.start();

		do {
			ThreadUtils.sleep(1000);
		} while (!lt.isAllStop());
		lt.waitToStop();

		System.out.println("iAdd:" + iAddObjects.get() + " iPoll:" + iPollObjects.get() + ":" + lsObj.size());
		return iAddObjects.get() == iPollObjects.get();
	}

	public static void main(String[] args) {
		// Log.init(LogAction.iFile);
		// Log.setLog("Z:\\", "AddSafeQueue");

		for (int i = 0; i < 100; i++) {
			testSafeQueue tsf = new testSafeQueue();
			if (!tsf.safeQueue()) {
				Log.logClass("Miss Object");
			} else {
				Log.logClass("All Object");
			}
		}
	}

}
