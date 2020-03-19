package com.china.fortune.socket.selectorManager;

import com.china.fortune.socket.SocketChannelHelper;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept
public abstract class NioAcceptAttach extends NioRWAttach {
	protected abstract Object onAccept(SocketChannel sc);

	protected abstract boolean onRead(SocketChannel sc, Object objForClient, Object objForThread);

	@Override
	protected SelectionKey acceptSocket(SocketChannel sc) {
		SelectionKey sk = super.acceptSocket(sc);
		if (sk != null) {
			Object obj = onAccept(sc);
			sk.attach(obj);
		}
		return sk;
	}

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		SocketChannel sc = (SocketChannel) key.channel();
		Object obj = key.attachment();
		if (onRead(sc, obj, objForThread)) {
			return NioSocketActionType.OP_READ;
		} else {
			return NioSocketActionType.OP_CLOSE;
		}
	}

	@Override
	protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
		return NioSocketActionType.OP_READ;
	}
}
