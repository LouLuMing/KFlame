package com.china.fortune.thread;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AutoThreadPool {
	protected AtomicInteger iTotalThreadCount = new AtomicInteger(0);
	protected int iMinThread = 1;
	protected int iMaxThread = Runtime.getRuntime().availableProcessors();

	protected int iThreadSleep = ConstData.iThreadSleepTime;

	public void setSleepTime(int sleep) {
		iThreadSleep = sleep;
	}

	abstract protected Object onCreate();

	abstract protected boolean doAction(Object objForThread);

	abstract protected void onDestroy(Object objForThread);

	protected boolean bRunning = true;
	protected Thread tFirst = null;
	protected long lFirstThreadId = 0;
	public void start() {
		bRunning = true;
		if (iMinThread > iMaxThread) {
			iMinThread = iMaxThread;
		}
		tFirst = addNewThread();
		lFirstThreadId = tFirst.getId();
		for (int i = 1; i < iMinThread; i++) {
			addNewThread();
		}
		iTotalThreadCount.set(iMinThread);
		Log.logClass("minThread:" + iMinThread + " maxThread:" + iMaxThread);
	}

	public void start(int iMin) {
		iMinThread = iMin;
		start();
	}

	public void setThread(int iMin, int iMax) {
		iMinThread = iMin;
		iMaxThread = iMax;
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

	public void join() {
		ThreadUtils.join(tFirst);
		while (iTotalThreadCount.get() != 0) {
			ThreadUtils.sleep(ConstData.iThreadSleepTime);
		}
	}

	public boolean isRun() {
		return bRunning;
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

	protected Thread addNewThread() {
		Thread t = new Thread(() -> {
			Object obj = onCreate();
			doWorkInThread(obj);
			onDestroy(obj);
		});
		t.start();
		return t;
	}

	protected int iLimitQuitRequest = 50;
	protected int iLimitContinuousWork = 3;

	private boolean hasFreeThread = true;
	protected void doWorkInThread(Object obj) {
		boolean isNeedDecrement = true;
		int iQuitRequest = 0;
		int iContinuouslyWork = 0;
		long lThreadId = Thread.currentThread().getId();
		while (bRunning) {
//			try {
//				doAction(obj);
//			} catch (Exception e) {
//				Log.logException(e);
//			} catch (Error e) {
//				Log.logException(e);
//			}
			if (doAction(obj)) {
				if (hasFreeThread) {
					iQuitRequest = 0;
					if (++iContinuouslyWork > iLimitContinuousWork) {
						if (iTotalThreadCount.getAndIncrement() < iMaxThread) {
							addNewThread();
						} else {
							iTotalThreadCount.getAndDecrement();
							hasFreeThread = false;
						}
						iContinuouslyWork = 0;
					}
				}
			} else {
				iContinuouslyWork = 0;
				if (lFirstThreadId != lThreadId) {
					if (++iQuitRequest > iLimitQuitRequest) {
						if (iTotalThreadCount.get() > iMinThread) {
							if (iTotalThreadCount.getAndDecrement() > iMinThread) {
								isNeedDecrement = false;
								hasFreeThread = true;
								break;
							} else {
								iTotalThreadCount.getAndIncrement();
							}
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
