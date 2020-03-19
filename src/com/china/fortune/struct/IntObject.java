package com.china.fortune.struct;

public class IntObject {
	private int iCount;
	public IntObject(int i) {
		iCount = i;
	}
	
	public int incrementAndGet() {
		return ++iCount;
	}
	
	public int get() {
		return iCount;
	}
	
	public void set(int i) {
		iCount = i;
	}

	public void add(int i) {
		iCount += i;
	}
}
