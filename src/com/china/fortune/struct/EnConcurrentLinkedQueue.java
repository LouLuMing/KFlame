package com.china.fortune.struct;

import com.china.fortune.global.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

// error: in imselectormanager
// can in but can't out (one client)

public class EnConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E> {
	private AtomicInteger iCount = new AtomicInteger(0);
	private int iCapacity = 1024;

	public EnConcurrentLinkedQueue(int iPower) {
		iCapacity = (1 << iPower);
	}

	@Override
	public boolean add(E e) {
		if (e != null) {
			if (iCount.get() < iCapacity) {
				super.offer(e);
				iCount.getAndIncrement();
				return true;
			} else {
				Log.logClassError("queue is full");
			}
		}
		return false;
	}

	public void addUntilSuccess(E e) {
		if (e != null) {
			super.offer(e);
			iCount.getAndIncrement();
		}
	}

	@Override
	public E poll() {
		E out = super.poll();
		if (out != null) {
			iCount.getAndDecrement();
		}
		return out;
	}

	public void decrease() {
		if (super.poll() != null) {
			iCount.getAndDecrement();
		}
	}
	
	public void decrement(int iCount) {
		for (int i = 0; i < iCount; i++) {
			decrease();
		}
	}
	
	@Override
	public int size() {
		return iCount.get();
	}

	public int getCapacity() {
		return iCapacity;
	}
	
	public boolean haveSpace() {
		return iCount.get() < iCapacity;
	}
}
