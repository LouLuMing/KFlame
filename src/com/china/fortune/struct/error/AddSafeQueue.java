package com.china.fortune.struct.error;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.china.fortune.global.ConstData;
import com.china.fortune.thread.ThreadUtils;

// can't add null object
public class AddSafeQueue<E> {
	private int iCapacity = 1024;
	private int iFaultTolerantCapacity;
	private int iAllowAddCapacity;
	private int iHead = 0;
	private AtomicInteger iTail = new AtomicInteger(0);
	private AtomicInteger iCount = new AtomicInteger(0);
	private AtomicReference<E>[] lsObj = null;
	private int iDefaultCapacity;

	public AddSafeQueue(int iPower) {
		iCapacity = (1 << iPower);
		iDefaultCapacity = iCapacity - 1;
		iFaultTolerantCapacity = iDefaultCapacity;
		lsObj = new AtomicReference[iCapacity];
		for (int i = 0; i < iCapacity; i++) {
			lsObj[i] = new AtomicReference<E>();
		}
	}

	public int getCapacity() {
		return iCapacity;
	}
	
	public int getFaultTolerantCapacity() {
		return iFaultTolerantCapacity;
	}
	
	public void setFaultTolerant(int iThread) {
		iFaultTolerantCapacity = iDefaultCapacity - iThread;
		iAllowAddCapacity = iFaultTolerantCapacity - iThread;
	}

	public boolean add(E e) {
		boolean rs = false;
		if (iCount.get() < iFaultTolerantCapacity) {
			int i = iTail.getAndIncrement() & iDefaultCapacity;
			lsObj[i].set(e);
			iCount.incrementAndGet();
			rs = true;
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
		return iCount.get();
	}
	
	public void decrement(int iSub) {
		if (iCount.get() > iSub) {
			iHead = (iHead + iSub) & iDefaultCapacity;
			iCount.addAndGet(-iSub);
		}
	}
	
	public E poll() {
		E e = null;
		if (iCount.get() > 0) {
			e = lsObj[iHead].get();
			lsObj[iHead] = null;
			iHead = (iHead + 1) & iDefaultCapacity;
			iCount.getAndDecrement();
		}
		return e;
	}

	public E peek() {
		E e = null;
		if (iCount.get() > 0) {
			e = lsObj[iHead].get();
		}
		return e;
	}
	
	public void decrease() {
		lsObj[iHead] = null;
		iHead = (iHead + 1) & iDefaultCapacity;
		iCount.getAndDecrement();
	}

	public boolean haveSpace() {
		return iCount.get() < iAllowAddCapacity;
	}

}
