package com.china.fortune.target.autoIncreaseId.entity;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.common.DateAction;

public class DateAndAutoInteger {
	private AtomicInteger aiUniID = new AtomicInteger(0);
	public final static int ciLength = 14 + 3;
	
	public String getUnicode() {
		return String.format("%s%03d", DateAction.getDateTime("yyyyMMddHHmmss"),
				(aiUniID.getAndIncrement() % 1000));
	}
}
