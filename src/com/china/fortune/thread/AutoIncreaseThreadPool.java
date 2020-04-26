package com.china.fortune.thread;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

public abstract class AutoIncreaseThreadPool {
	protected AtomicInteger iTotalThreadCount = new AtomicInteger(0);
	protected int iMinThreads = Runtime.getRuntime().availableProcessors();
	protected int iMaxThreads = Runtime.getRuntime().availableProcessors() * 2 + 1;

	protected int iThreadSleep = ConstData.iThreadSleepTime;

	public void setSleepTime(int sleep) {
		iThreadSleep = sleep;
	}

	abstract protected Object onCreate();

	abstract protected void doAction(Object objForThread);

	abstract protected boolean haveThingsToDo(Object objForThread);

	abstract protected void onDestroy(Object objForThread);

	protected boolean bRunning = true;

	public void start() {
		bRunning = true;
		if (iMinThreads > iMaxThreads) {
			iMinThreads = iMaxThreads;
		}
		for (int i = 0; i < iMinThreads; i++) {
			addNewThread();
		}
	}

	public void start(int iMin) {
		iMinThreads = iMin;
		start();
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
		while (iTotalThreadCount.get() != 0) {
			ThreadUtils.sleep(ConstData.iThreadSleepTime);
		}
	}

	public boolean isAllStop() {
		return iTotalThreadCount.get() == 0;
	}

	public int getWorkingThreadCount() {
		return iTotalThreadCount.get();
	}

	public int getTotalThreadCount() {
		return iTotalThreadCount.get();
	}

	protected void addNewThread() {
		iTotalThreadCount.getAndIncrement();
		Thread t = new Thread() {
			@Override
			public void run() {
				Object obj = onCreate();
				doWorkInThread(obj);
				onDestroy(obj);
			}
		};
		t.start();
	}

	static final protected int iLimitQuitRequest = 50;
	static final protected int iLimitContinuousWork = 3;

	protected void doWorkInThread(Object obj) {
		boolean isNeedDecrement = true;
		int iQuitRequest = 0;
		int iContinuouslyWork = 0;
		while (bRunning) {
			if (haveThingsToDo(obj)) {
				iQuitRequest = 0;
				if (++iContinuouslyWork > iLimitContinuousWork) {
					if (iTotalThreadCount.get() < iMaxThreads) {
						addNewThread();
					}
					iContinuouslyWork = 0;
				}
				try {
					doAction(obj);
				} catch (Exception e) {
					Log.logException(e);
				} catch (Error e) {
					Log.logException(e);
				}
			} else {
				iContinuouslyWork = 0;
				if (++iQuitRequest > iLimitQuitRequest) {
					if (iTotalThreadCount.get() > iMinThreads) {
						if (iTotalThreadCount.getAndDecrement() > iMinThreads) {
							isNeedDecrement = false;
							break;
						} else {
							iTotalThreadCount.getAndIncrement();
						}
					}
				}
				ThreadUtils.sleep(iThreadSleep);
			}
		}
		if (isNeedDecrement) {
			iTotalThreadCount.getAndDecrement();
		}
	}

	public String showStatus() {
		return "ThreadPool:" + iTotalThreadCount.get();
	}
}
