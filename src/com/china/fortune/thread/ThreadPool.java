package com.china.fortune.thread;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.ConstData;

public abstract class ThreadPool {
	private AtomicInteger iTotalThreadCount = new AtomicInteger(0);
	private AtomicInteger iWorkingThreadCount = new AtomicInteger(0);
	private int iMinThreads = Runtime.getRuntime().availableProcessors();
	private int iMaxThreads = Runtime.getRuntime().availableProcessors() * 2 + 1;

	abstract protected Object onCreate();

	abstract protected void doAction(Object obj);

	abstract protected boolean haveThingsToDo(Object obj);

	abstract protected void onDestroy(Object obj);

	private boolean bRunning = true;

	public void start() {
		bRunning = true;
		for (int i = 0; i < iMinThreads; i++) {
			addNewThread();
		}
	}

	public void start(int iMin, int iMax) {
		iMinThreads = iMin;
		iMaxThreads = iMax;
		start();
	}

	public void setAllStop() {
		bRunning = false;
	}

	public void waitToStop() {
		bRunning = false;
		while (iWorkingThreadCount.get() != 0) {
			ThreadUtils.sleep(ConstData.iThreadSleepTime);
		}
	}

	public boolean isAllStop() {
		return iTotalThreadCount.get() == 0;
	}

	public int getWorkingThreadCount() {
		return iWorkingThreadCount.get();
	}

	public int getTotalThreadCount() {
		return iTotalThreadCount.get();
	}

	public void startNewThread() {
		if (iWorkingThreadCount.get() == iTotalThreadCount.get()) {
			if (iTotalThreadCount.get() < iMaxThreads) {
				addNewThread();
			}
		}
	}

	private void addNewThread() {
		iTotalThreadCount.getAndIncrement();
		Thread t = new Thread() {
			@Override
			public void run() {
				Object obj = onCreate();
				doWork(obj);
				onDestroy(obj);
			}
		};
		t.start();
	}

	static final int iMaxQuitRequest = 9;

	private void doWork(Object obj) {
		boolean isNeedDecrement = true;
		int iQuitRequest = 0;
		while (bRunning) {
			boolean bSleep = true;
			if (haveThingsToDo(obj)) {
				iQuitRequest = 0;
				iWorkingThreadCount.getAndIncrement();
				doAction(obj);
				iWorkingThreadCount.getAndDecrement();
				bSleep = false;
			} else {
				if (iQuitRequest++ > iMaxQuitRequest) {
					if (iTotalThreadCount.get() > iMinThreads) {
						if (iTotalThreadCount.getAndDecrement() > iMinThreads) {
							isNeedDecrement = false;
							break;
						} else {
							iTotalThreadCount.getAndIncrement();
						}
					}
				}
			}
			if (bSleep) {
				ThreadUtils.sleep(ConstData.iThreadSleepTime);
			}
		}
		if (isNeedDecrement) {
			iTotalThreadCount.getAndDecrement();
		}
	}

	public String showStatus() {
		return "ThreadPool:" + iTotalThreadCount.get() + " Work:" + iWorkingThreadCount.get();
	}
}
