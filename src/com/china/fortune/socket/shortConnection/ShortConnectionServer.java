package com.china.fortune.socket.shortConnection;

import java.net.Socket;

public abstract class ShortConnectionServer extends ShortConnectionServerAttach {
	abstract protected void onRead(Socket sc);
	
	@Override
	protected void onRead(Socket sc, Object obj) {
		onRead(sc);
	}
	

	@Override
	protected Object createObjectInThread() {
		return null;
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}
}
