package com.china.fortune.struct;

import com.china.fortune.global.Log;

public class Queue<E> {
	private int iSize;
	private int iHead;
	private int iCount;
	private E[] lsObj = null;
	final private int iEnLargeSize = 1024;
	
	public Queue() {
		iSize = 1024;
		lsObj = (E[])new Object[iSize];  
	}
	
	public Queue(int i) {
		iSize = i;
		lsObj = (E[])new Object[iSize];  
	}

	private void enlarge() {
		int iTmp = iSize + iEnLargeSize;
		E[] lsTmp = (E[])new Object[iTmp];
		int iHeadCount = iSize - iHead;
		if (iHeadCount > 0) {
			for (int i = 0; i < iHeadCount; i++) {
				lsTmp[i] = lsObj[iHead + i];
			}
		}
		int iLeft = iSize - iHeadCount;
		if (iLeft > 0) {
			for (int i = 0; i < iLeft; i++) {
				lsTmp[iHeadCount + i] = lsObj[i];
			}
		}

		for (int i = 0; i < iSize; i++) {
			lsObj[i] = null;
		}
		iHead = 0;
		iSize = iTmp;
		lsObj = lsTmp;
	}

	public void add(E e) {
		iCount++;
		if (iCount > iSize) {
			enlarge();
		}

		int tail = (iHead + iCount - 1) % iSize;
		lsObj[tail] = e;
	}
	
	public int size() {
		return iCount;
	}
	
	public E poll() {
		E e = null;
		if (iCount > 0) {
			e = lsObj[iHead];
			lsObj[iHead] = null;
			iCount--;
			iHead = (iHead + 1) % iSize;
		}
		return e;
	}
	
	public boolean isFull() {
		return iCount >= iSize;
	}
	
	public boolean haveSpace() {
		return iCount < iSize;
	}
	
	public static void main(String[] args) {
		Queue<Integer> lsObj = new Queue<Integer>(10);
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
