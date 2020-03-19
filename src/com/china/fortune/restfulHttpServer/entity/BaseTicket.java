package com.china.fortune.restfulHttpServer.entity;

import com.china.fortune.global.ConstData;

public class BaseTicket {
	public long ticket;
	
	public BaseTicket() {
		ticket = System.currentTimeMillis() / ConstData.ciPerSecond;
	}
}
