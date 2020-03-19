package com.china.fortune.common;

import java.util.concurrent.atomic.AtomicInteger;

public class PrimaryKey {
	static final private long ciSubTimes = (2016 - 1970) * 365 * 24 * 3600 * 1000;
	private AtomicInteger aiUniID = new AtomicInteger(0);
	public String createUnicode() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.currentTimeMillis() - PrimaryKey.ciSubTimes);
		sb.append(aiUniID.getAndIncrement() & 0xff);
		return sb.toString();
	}
}
