package com.china.fortune.struct.error;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.ConstData;
import com.china.fortune.thread.ThreadUtils;

//can't add null object
public class PollSafeQueue<E> {
	private int iCapacity = 1024;
	private int iDefaultCapacity;
	private int iFaultTolerantCapacity;
	
	private AtomicInteger iHead = new AtomicInteger(0);
	private int iTail = 0;
	private AtomicInteger iCount = new AtomicInteger(0);
	volatile private E[] lsObj = null;
	
	public int capacity() {
		return iCapacity;
	}
	
	public void clear() {
		for (int i = 0; i < lsObj.length; i++) {
			lsObj[i] = null;
		}
		iCount.set(0);
	}
	
	@SuppressWarnings("unchecked")
	public PollSafeQueue(int iPower) {
		iCapacity = (1 << iPower);
		iDefaultCapacity = iCapacity - 1;
		iFaultTolerantCapacity = iDefaultCapacity;
		lsObj = (E[]) new Object[iCapacity];
	}

	public void setFaultTolerant(int iThread) {
		iFaultTolerantCapacity = iDefaultCapacity - iThread;
	}

	public boolean add(E e) {
		if (iCount.get() < iFaultTolerantCapacity) {
			lsObj[iTail] = e;
			iTail = (iTail + 1) & iDefaultCapacity;
			iCount.incrementAndGet();
			return true;
		}
		return false;
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
		return iCount.get();
	}

	public E poll() {
		E e = null;
		if (iCount.getAndDecrement() > 0) {
			int i = iHead.getAndIncrement() & iDefaultCapacity;
			e = lsObj[i];
			lsObj[i] = null;
//			if (e == null) {
//				Log.logClass("Object null:" + iCount.xyToIndex());
//			}
		} else {
			iCount.getAndIncrement();
		}
		return e;
	}

	// public boolean isFull() {
	// return iCount.xyToIndex() >= iCapacity;
	// }

	public boolean haveSpace() {
		return iCount.get() < iFaultTolerantCapacity;
	}
}
