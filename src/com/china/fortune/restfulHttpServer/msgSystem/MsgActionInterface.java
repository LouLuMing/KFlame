package com.china.fortune.restfulHttpServer.msgSystem;

public interface MsgActionInterface<T> {
	void doAction(T data, Object objThread);
}
