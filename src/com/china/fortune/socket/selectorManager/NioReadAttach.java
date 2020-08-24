package com.china.fortune.socket.selectorManager;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept
public abstract class NioReadAttach extends NioRWSerial {
	protected abstract boolean onRead(SocketChannel sc, Object objForClient, Object objForThread);

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		SocketChannel sc = (SocketChannel) key.channel();
		Object obj = key.attachment();
		if (onRead(sc, obj, objForThread)) {
			return NioSocketActionType.NSA_READ;
		} else {
			return NioSocketActionType.NSA_CLOSE;
		}
	}

	@Override
	protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
		return NioSocketActionType.NSA_READ;
	}

	@Override
	protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
		return NioSocketActionType.NSA_READ;
	}

	@Override
	protected void onClose(SelectionKey key) {
	}

	@Override
	protected boolean isInvalidSocket(long lNow, SelectionKey key) {
		return false;
	}
}
