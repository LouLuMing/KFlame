package com.china.fortune.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.os.log.LogAction;
import com.china.fortune.struct.Queue;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.LoopThreadList;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class LockQueue<T> {
	private final ReentrantLock rl = new ReentrantLock(true);
	private Queue<T> qObj = null;
	
	public LockQueue() {
		qObj = new Queue<T>();
	}
	
	public LockQueue(int iSize) {
		qObj = new Queue<T>(iSize);
	}
	
	public void lock() {
		rl.lock();
	}
	
	public void unlock() {
		rl.unlock();
	}
	
	public boolean lockAdd(T t) {
		boolean rs = false;
		rl.lock();
		if (qObj.haveSpace()) {
			qObj.add(t);
			rs = true;
		}
		rl.unlock();
		return rs;
	}

	
	public T lockPoll() {
		T o = null;
		rl.lock();
		o = qObj.poll();
		rl.unlock();
		return o;
	}
	
	public T tryLockPoll(int iMil) {
		T o = null;
		try {
			if (rl.tryLock(iMil, TimeUnit.MILLISECONDS)) {
				o = qObj.poll();
				rl.unlock();
			}
		}
		catch (Exception e) {}
		return o;
	}
	
	public int size() {
		return qObj.size();
	}
	
	public boolean isFull() {
		return qObj.isFull();
	}
	
	public boolean haveSpace() {
		return qObj.haveSpace();
	}
	
	public static void main(String[] args) {
		Log.init(LogAction.iConsole);
		Log.setLog("Z:\\", "LockQueue");
		final int iMaxObject = 160000;
		final AtomicBoolean bRun = new AtomicBoolean(true);
		final AtomicInteger bFetchCount = new AtomicInteger(0);
		
		TimeoutAction ta = new TimeoutAction();
		ta.start();
		final LockQueue<Integer> lsObj = new LockQueue<Integer>(10);
		LoopThreadList lt = new LoopThreadList();
		final AtomicInteger iIndex = new AtomicInteger(0);
		LoopThread add = new LoopThread() {
			@Override
			protected boolean doAction() {
				if (iIndex.get() < iMaxObject) {
					if (lsObj.haveSpace()) {
						lsObj.lockAdd(iIndex.getAndIncrement());
						return false;
					}
				}
				return true;
			}
		};
		lt.add(add);
		
		for (int i = 0; i < 10; i++) {
			LoopThread poll = new LoopThread() {
				@Override
				protected boolean doAction() {
					if (lsObj.size() > 0) {
						Integer obj = lsObj.tryLockPoll(50);
						if (obj != null) {
//							Log.logClass(String.valueOf(obj));
							if (bFetchCount.incrementAndGet() >= iMaxObject) {
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
		
		Log.logClass(String.valueOf(ta.getMilliseconds()));
		lt.waitToStop();
	}
}
