package com.china.fortune.socket.selectorManager;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// accept, login
// first start open selector(call start function)
// second and accept
public abstract class NioLoginAttach extends NioRWSerial {
	protected abstract Object onLogin(SocketChannel sc, Object objForThread);
	protected abstract boolean onRead(SocketChannel sc, Object objForClient, Object objForThread);

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		boolean bOK = false;
		SocketChannel sc = (SocketChannel) key.channel();
		Object objForClient = key.attachment();
		if (objForClient != null) {
			bOK = onRead(sc, objForClient, objForThread);
		} else {
			objForClient = onLogin(sc, objForThread);
			if (objForClient != null) {
				key.attach(objForClient);
				bOK = true;
			}
		}
		if (bOK) {
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
