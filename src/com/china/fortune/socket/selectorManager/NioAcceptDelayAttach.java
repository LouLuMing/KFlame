package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.bk.NioRWAttach;
import com.china.fortune.struct.FastList;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;

// accept, allowaccept, onaccept
// do wast time in accept action
// first start open selector(call start function)
// second and accept
public abstract class NioAcceptDelayAttach extends NioRWAttach {
	protected abstract Object onAccept(SocketChannel sc, Object objForThread);
	protected abstract boolean onRead(SocketChannel sc, Object objForClient, Object objForThread);

	protected void registerDelaySocket(FastList<SelectionKey> qSelectedKey, SocketChannel sc) {
		int iQueue = emptySocketController.getQueueIndex();
		if (iQueue > -1) {
			SelectionKey sk = register(sc, 0);
			if (sk != null) {
				emptySocketController.add(sk, iQueue);
				qSelectedKey.add(sk);
			} else {
				SocketChannelHelper.close(sc);
			}
		} else {
			SocketChannelHelper.close(sc);
		}
	}

	@Override
	synchronized protected void selectAction(FastList<SelectionKey> qSelectedKey) {
		int iSel;
		try {
			iSel = mSelector.selectNow();
		} catch (Exception e) {
			Log.logException(e);
			iSel = 0;
		}
		if (iSel > 0) {
			Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
			if (selectedKeys != null) {
				emptySocketController.checkTimeout();
				for (SelectionKey key : selectedKeys) {
					if (key.isValid()) {
						if (key.isReadable() || key.isWritable()) {
							key.interestOps(0);
							qSelectedKey.add(key);
						} else if (key.isAcceptable()) {
							SocketChannel sc = accept(key);
							if (sc != null) {
								registerDelaySocket(qSelectedKey, sc);
							}
						}
					} else {
						freeKeyAndSocket(key);
					}
				}
				selectedKeys.clear();
			}
		}
	}

	@Override
	protected void readSocket(SelectionKey key, Object objForThread) {
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
			key.interestOps(SelectionKey.OP_READ);
		} else {
			onClose(key);
			freeKeyAndSocket(key);
		}
	}

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		return NioSocketActionType.NSA_READ;
	}
	
	@Override
	protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
		return NioSocketActionType.NSA_READ;
	}

}
