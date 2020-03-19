package com.china.fortune.statistics.count;

public class SumData implements CountInterface {
	private int iTotal = 0;

	@Override
	public void add(int id) {
		iTotal += id;
	}

	@Override
	public void clear() {
		iTotal = 0;
	}

	@Override
	public int count() {
		return iTotal;
	}
}
