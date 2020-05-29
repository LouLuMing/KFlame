package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class NioThreadPool {
    protected int iLimitQuitRequest = 30;
    protected int iLimitContinuousWork = 5;

    protected AtomicInteger iTotalThreadCount = new AtomicInteger(0);
    protected int iMinThread = 1;
    protected int iMaxThread = Runtime.getRuntime().availableProcessors();

    protected int iThreadSleep = 5;

    public void setSleepTime(int sleep) {
        iThreadSleep = sleep;
    }

    abstract protected Object onCreate();

    abstract protected void doWorkInThread(Object obj);

    abstract protected void onDestroy(Object objForThread);

    protected boolean bRunning = true;
    private Thread tFirst = null;
    protected long lFirstThreadId = 0;

    public void start() {
        bRunning = true;
        if (iMaxThread < 2) {
            iMaxThread = 2;
        }
        if (iMinThread > iMaxThread) {
            iMinThread = iMaxThread;
        }
        tFirst = addNewThread();
        lFirstThreadId = tFirst.getId();
        for (int i = 1; i < iMinThread; i++) {
            addNewThread();
        }
        Log.logClass("minThread:" + iMinThread + " maxThread:" + iMaxThread);
    }

    public void setThread(int iMin, int iMax) {
        iMinThread = iMin;
        iMaxThread = iMax;
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
        Thread t = new Thread(() -> {
            Object obj = onCreate();
            doWorkInThread(obj);
            onDestroy(obj);
        });
        t.start();
        return t;
    }


    public String showStatus() {
        return "ThreadPool:" + iTotalThreadCount.get();
    }
}
