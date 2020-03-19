package com.china.fortune.socket.selectorManager;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.socket.EmptySocketControllerNoSafe;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.AutoIncreaseThreadPool;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept


public abstract class NioRWAttach {
	protected final int iSelectSleepTime = 50;
	protected EnConcurrentLinkedQueue<SelectionKey> qSelectedKey = new EnConcurrentLinkedQueue<SelectionKey>(17);
	protected Selector mSelector = null;

	protected abstract NioSocketActionType onRead(SelectionKey key, Object objForThread);

	protected abstract NioSocketActionType onWrite(SelectionKey key, Object objForThread);

	protected abstract void onClose(SocketChannel sc, Object objForClient);

	protected abstract Object createObjectInThread();

	protected abstract void destroyObjectInThread(Object objForThread);

	protected boolean allowAccept(SocketChannel sc) {
		return true;
	}

	protected boolean isInvalidSocket(long lLimited, Object objForClient) {
		return objForClient == null;
	}

	protected SocketChannel accept(SelectionKey key) {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = null;
		try {
			sc = ssc.accept();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sc;
	}

	protected SelectionKey acceptSocket(SocketChannel sc) {
		if (allowAccept(sc)) {
			int iQueue = emptySocketController.getQueueIndex();
			if (iQueue > -1) {
				SelectionKey sk = addRead(sc);
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

	protected final EmptySocketControllerNoSafe emptySocketController = new EmptySocketControllerNoSafe(6, 7,
			65536) {
		@Override
		public void onTimeout(long lLimited, SelectionKey[] lsData, int iSize) {
			for (int i = 0; i < iSize; i++) {
				SelectionKey key = lsData[i];
				if (key != null) {
					if (key.isValid() && isInvalidSocket(lLimited, key.attachment())) {
//						Log.logError("isInvalidSocket " + IPHelper.getRemoteSocketAddress((SocketChannel)key.channel()));
						Log.logError("isInvalidSocket");
						freeKeyAndSocket(key);
					}
					lsData[i] = null;
				}
			}
		}
	};

	protected void selectAction() {
		int iSel;
		if (qSelectedKey.haveSpace()) {
			try {
				iSel = mSelector.select(iSelectSleepTime);
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
								qSelectedKey.addUntilSuccess(key);
							} else if (key.isAcceptable()) {
								SocketChannel sc = accept(key);
								if (sc != null) {
									acceptSocket(sc);
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
	}

	protected class SelectThread extends Thread {
		private boolean bRunning = true;

		@Override
		public void run() {
			while (bRunning) {
				try {
					selectAction();
				} catch (Exception e) {
					Log.logException(e);
				} catch (Error e) {
					Log.logException(e);
				}
			}
		}

		public void waitToStop() {
			bRunning = false;
			try {
				this.join();
			} catch (Exception e) {
			}
		}
	}

	public void interestOps(SelectionKey key, int ops) {
		try {
			key.interestOps(ops);
		} catch (Exception e) {
		}
	}

	private SelectThread selectThread = new SelectThread();
	private AutoIncreaseThreadPool readThreaPool = new AutoIncreaseThreadPool() {
		@Override
		protected void doAction(Object objForThread) {
			SelectionKey key = qSelectedKey.poll();
			if (key != null) {
				switch (readSocket(key, objForThread)) {
				case OP_READ:
					key.interestOps(SelectionKey.OP_READ);
					break;
				case OP_WRITE:
					key.interestOps(SelectionKey.OP_WRITE);
					break;
				case OP_CLOSE:
					onClose((SocketChannel) key.channel(), key.attachment());
					freeKeyAndSocket(key);
					break;
				}
			}
		}

		@Override
		protected boolean haveThingsToDo(Object objForThread) {
			return qSelectedKey.size() > 0;
		}

		@Override
		protected void onDestroy(Object objForThread) {
            destroyObjectInThread(objForThread);
		}

		@Override
		protected Object onCreate() {
			return createObjectInThread();
		}
	};

	protected NioSocketActionType readSocket(SelectionKey key, Object objForThread) {
		if (key.isValid()) {
			if (key.isReadable()) {
				return onRead(key, objForThread);
			} else if (key.isWritable()) {
				return onWrite(key, objForThread);
			}
		}
		return NioSocketActionType.OP_CLOSE;
	}

	protected boolean openSelector() {
		boolean rs = true;
		try {
			mSelector = Selector.open();
		} catch (Exception e) {
			rs = false;
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	private void closeSelector() {
		try {
			if (mSelector != null) {
				for (SelectionKey key : mSelector.selectedKeys()) {
					freeKeyAndSocket(key);
				}
				mSelector.close();
				mSelector = null;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void freeKeyAndSocket(SocketChannel sc) {
		try {
			SelectionKey key = sc.keyFor(mSelector);
			if (key != null) {
				key.cancel();
			}
			sc.close();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	protected void cancelSocket(SelectionKey key) {
		try {
			key.cancel();
			key.attach(null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	protected void freeKeyAndSocket(SelectionKey key) {
		try {
			SocketChannel sc = (SocketChannel) key.channel();
			if (sc != null) {
				sc.close();
			}
			key.cancel();
			key.attach(null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	protected SelectionKey addNull(SocketChannel sc) {
		SelectionKey selectionKey = null;
		try {
			sc.socket().setSoLinger(true, 0);
			sc.configureBlocking(false);
			selectionKey = sc.register(mSelector, 0, null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	protected SelectionKey addWrite(SocketChannel sc) {
		SelectionKey selectionKey = null;
		try {
			sc.socket().setSoLinger(true, 0);
			sc.configureBlocking(false);
			selectionKey = sc.register(mSelector, SelectionKey.OP_WRITE, null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	protected SelectionKey addRead(SocketChannel sc) {
		SelectionKey selectionKey = null;
		try {
			sc.socket().setSoLinger(true, 0);
			sc.configureBlocking(false);
			selectionKey = sc.register(mSelector, SelectionKey.OP_READ, null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	private SelectionKey addAccept(int iPort) {
		SelectionKey selectionKey = null;
		if (iPort > 0) {
			try {
				ServerSocketChannel ssc = ServerSocketChannel.open();
				ServerSocket ss = ssc.socket();
				ss.bind(new InetSocketAddress(iPort));
				ssc.configureBlocking(false);
				selectionKey = ssc.register(mSelector, SelectionKey.OP_ACCEPT, null);
			} catch (Exception e) {
				selectionKey = null;
				Log.logClass(e.getMessage());
			}
			Log.logClass("Listen " + iPort);
		}
		return selectionKey;
	}

	protected void startThreads(int iMinThread, int iMaxThread) {
		selectThread.start();
		readThreaPool.setSleepTime(10);
		readThreaPool.start(iMinThread, iMaxThread);
		Log.logClass("minThread:" + iMinThread + " maxThread:" + iMaxThread);
	}

	public boolean openAndStart(int iPort, int iMinThread, int iMaxThread) {
		boolean rs = false;
		if (openSelector()) {
			addAccept(iPort);
			startThreads(iMinThread, iMaxThread);
			rs = true;
		}
		return rs;
	}

	public void join() {
		try {
			selectThread.join();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		stop();
	}

	public void stop() {
		selectThread.waitToStop();
		readThreaPool.waitToStop();
		qSelectedKey.clear();
		closeSelector();
	}

	public boolean openAndStart(int iPort) {
		return openAndStart(iPort, 1, Runtime.getRuntime().availableProcessors() * 4 + 1);
	}

	public boolean openAndStart(int iPort, int iMaxThread) {
		return openAndStart(iPort, 1, iMaxThread);
	}

//	protected boolean start(int iMinThread, int iMaxThread) {
//		return openAndStartThread(iMinThread, iMaxThread);
//	}

	public boolean startAndBlock(int iPort) {
		return startAndBlock(iPort, Runtime.getRuntime().availableProcessors() * 4 + 1);
	}

	public boolean startAndBlock(int iPort, int minThread, int maxThread) {
		boolean rs = false;
		if (openAndStart(iPort, minThread, maxThread)) {
			join();
			rs = true;
		}
		return rs;
	}

	public boolean startAndBlock(int iPort, int maxThread) {
		boolean rs = false;
		if (openAndStart(iPort, 1, maxThread)) {
			join();
			rs = true;
		}
		return rs;
	}

	public String doCommand(String sCmd) {
		return readThreaPool.showStatus() + " Queue:" + qSelectedKey.size();
	}
}
