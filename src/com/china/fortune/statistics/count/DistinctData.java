package com.china.fortune.statistics.count;

import java.util.HashSet;

import com.china.fortune.common.DateAction;

public class DistinctData implements CountInterface {
	private HashSet<Integer> userIdMap = new HashSet<Integer>();

	@Override
	public void add(int id) {
		userIdMap.add(id);
	}

	@Override
	public void clear() {
		userIdMap.clear();
	}

	@Override
	public int count() {
		return userIdMap.size();
	}
}
