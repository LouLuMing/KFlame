package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.ConstData;
import com.china.fortune.struct.FastList;
import com.china.fortune.thread.ThreadUtils;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class NioThreadPool {
    private int iDefListSize = 16 * 1024;
    protected AtomicInteger iTotalThreadCount = new AtomicInteger(0);
    protected int iMinThreads = Runtime.getRuntime().availableProcessors();
    protected int iMaxThreads = Runtime.getRuntime().availableProcessors() * 2 + 1;

    protected int iThreadSleep = ConstData.iThreadSleepTime;

    public void setSleepTime(int sleep) {
        iThreadSleep = sleep;
    }

    abstract protected Object onCreate();

    abstract protected boolean doAction(FastList<SelectionKey> qSelectedKey, Object objForThread);

    abstract protected void onDestroy(Object objForThread);

    protected boolean bRunning = true;
    private Thread tFirst = null;
    private long lFirstThreadId = 0;

    public void setMaxQueue(int iMax) {
        iDefListSize = iMax;
    }
    public void start() {
        bRunning = true;
        if (iMinThreads > iMaxThreads) {
            iMinThreads = iMaxThreads;
        }
        tFirst = addNewThread();
        lFirstThreadId = tFirst.getId();
        for (int i = 1; i < iMinThreads; i++) {
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

    public int getTotalThreadCount() {
        return iTotalThreadCount.get();
    }

    protected Thread addNewThread() {
//		Thread t = new Thread() {
//			@Override
//			public void run() {
//				Object obj = onCreate();
//				doWorkInThread(obj);
//				onDestroy(obj);
//			}
//		};
        Thread t = new Thread(() -> {
            Object obj = onCreate();
            doWorkInThread(obj);
            onDestroy(obj);
        });
        t.start();
        return t;
    }

    protected int iLimitQuitRequest = 30;
    protected int iLimitContinuousWork = 5;

    protected void doWorkInThread(Object obj) {
        boolean isNeedDecrement = true;
        int iQuitRequest = 0;
        int iContinuouslyWork = 0;
        long lThreadId = Thread.currentThread().getId();
        FastList<SelectionKey> qSelectedKey = new FastList<SelectionKey>(iDefListSize);
        while (bRunning) {
//			try {
//				doAction(obj);
//			} catch (Exception e) {
//				Log.logException(e);
//			} catch (Error e) {
//				Log.logException(e);
//			}
            if (doAction(qSelectedKey, obj)) {
                iQuitRequest = 0;
                if (++iContinuouslyWork > iLimitContinuousWork) {
                    if (iTotalThreadCount.get() < iMaxThreads) {
                        iTotalThreadCount.getAndIncrement();
                        addNewThread();
                    }
                    iContinuouslyWork = 0;
                }
            } else {
                iContinuouslyWork = 0;
                if (lFirstThreadId != lThreadId) {
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
