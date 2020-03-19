package com.china.fortune.struct.error;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

// iCount is not Sync, will lost data, Error
// have bugs, will lost data
public class SafeQueue<E> {
	private int iCapacity = 1024;
	private int iDefaultCapacity;
	private int iFaultTolerantCapacity;
	private AtomicInteger iHead = new AtomicInteger(0);
	private AtomicInteger iTail = new AtomicInteger(0);
	private AtomicInteger iMaybeMinCount = new AtomicInteger(0);
	private AtomicInteger iMaybeMaxCount = new AtomicInteger(0);
	private AtomicReference<E>[] lsObj = null;
	
	public SafeQueue() {
		iCapacity = (1 << 10);
		iDefaultCapacity = iCapacity - 1;
		lsObj = new AtomicReference[iCapacity];
		for (int i = 0; i < iCapacity; i++) {
			lsObj[i] = new AtomicReference<E>();
		} 
	}
	
	public SafeQueue(int iPower) {
		iCapacity = (1 << iPower);
		iDefaultCapacity = iCapacity - 1;
		lsObj = new AtomicReference[iCapacity];
		for (int i = 0; i < iCapacity; i++) {
			lsObj[i] = new AtomicReference<E>();
		}
	}
	
	private int iWorkThread = 0;
	public void setFaultTolerant(int iThread) {
		iWorkThread = iThread;
		iFaultTolerantCapacity = iDefaultCapacity - iThread;
	}
	
	public boolean add(E e) {
		boolean rs = false;
		if (iMaybeMaxCount.getAndIncrement() < iFaultTolerantCapacity) {
			int i = iTail.getAndIncrement() & iDefaultCapacity;
//			if (lsObj[i] != null) {
//				Log.logClass("" + (Integer)lsObj[i]);
//			}
			lsObj[i].set(e);
			iMaybeMinCount.getAndIncrement();
			rs = true;
		} else {
			iMaybeMaxCount.decrementAndGet();
		}
		return rs;
	}
	
	public void addUntilSuccess(E e) {
		do {
			if (add(e)) {
				break;
			} else {
				ThreadUtils.sleep(ConstData.iThreadSleepTime);
			}
		} while (true);
	}
	
	public int size() {
		return iMaybeMaxCount.get();
	}
	
	public E poll() {
		E e = null;
		if (iMaybeMinCount.getAndDecrement() > iWorkThread) {
			int i = iHead.getAndIncrement() & iDefaultCapacity;
			e = lsObj[i].getAndSet(null);
			iMaybeMaxCount.decrementAndGet();
//			if (e == null) {
//				Log.logClass("" + iHead.getAndIncrement());
//			}
//			Log.log(iMaybeMinCount.xyToIndex() + ":" + iMaybeMaxCount.xyToIndex());
		} else {
			iMaybeMinCount.incrementAndGet();
		}
		return e;
	}
	
	public boolean haveSpace() {
		return iMaybeMaxCount.get() < iDefaultCapacity;
	}

	public static void main(String[] args) {
		SafeQueue<Integer> lsObj = new SafeQueue<Integer>(10);
		for (int i = 0; i < 100; i++) {
			lsObj.add(i);
			if (i % 3 == 0) {
				Log.logClass(String.valueOf(lsObj.poll()));
			}
		}
		for (int i = 0; i < 100; i++) {
			Log.logClass(String.valueOf(lsObj.poll()));
		}

	}
}