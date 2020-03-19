package com.china.fortune.socket.selectorManager;

import com.china.fortune.socket.SocketChannelHelper;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

// accept, allowaccept, onaccept
// do wast time in accept action
// first start open selector(call start function)
// second and accept
public abstract class NioAcceptDelayAttach extends NioRWAttach {
	protected abstract Object onAccept(SocketChannel sc, Object objForThread);
	protected abstract boolean onRead(SocketChannel sc, Object objForClient, Object objForThread);
	
	@Override
	protected SelectionKey acceptSocket(SocketChannel sc) {
		if (allowAccept(sc)) {
			int iQueue = emptySocketController.getQueueIndex();
			if (iQueue > -1) {
				SelectionKey sk = addNull(sc);
				qSelectedKey.addUntilSuccess(sk);
				emptySocketController.add(sk, iQueue);
				return sk;
			} else {
				SocketChannelHelper.close(sc);
			}
		} else {
			SocketChannelHelper.close(sc);
		}
		return null;
	}

	@Override
	protected NioSocketActionType readSocket(SelectionKey key, Object objForThread) {
		boolean bOK = false;
		SocketChannel sc = (SocketChannel) key.channel();
		Object objForClient = key.attachment();
		if (objForClient != null) {
			bOK = onRead(sc, objForClient, objForThread);
		} else {
			objForClient = onAccept(sc, objForThread);
			if (objForClient != null) {
				key.attach(objForClient);
				bOK = true;
			}
		}
		if (bOK) {
			return NioSocketActionType.OP_READ;
		} else {
			return NioSocketActionType.OP_CLOSE;
		}
	}

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		return NioSocketActionType.OP_READ;
	}
	
	@Override
	protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
		return NioSocketActionType.OP_READ;
	}


}
