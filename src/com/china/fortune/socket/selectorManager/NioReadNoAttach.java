package com.china.fortune.socket.selectorManager;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept
public abstract class NioReadNoAttach extends NioRWAttach {
	abstract protected boolean onRead(SocketChannel sc);

	@Override
	protected boolean isInvalidSocket(long lLimited, Object objForClient) {
		return false;
	}
	
	protected Object createObjectInThread() {
		return null;
	};

	protected void destroyObjectInThread(Object objForThread) {
	};
	
	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		SocketChannel sc = (SocketChannel) key.channel();
		if (onRead(sc)) {
			return NioSocketActionType.OP_READ;
		} else {
			return NioSocketActionType.OP_CLOSE;
		}
	}
	
	@Override
	protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
		return NioSocketActionType.OP_READ;
	}
	
	@Override
	protected void onClose(SelectionKey key, Object objForClient) {
	}
}
