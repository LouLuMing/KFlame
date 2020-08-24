package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.Log;
import com.china.fortune.socket.EmptySocketControllerNoSafe;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.AutoThreadPool;
import com.china.fortune.thread.ThreadUtils;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept


public abstract class NioModBK {
	private int iMaxSocket = 18;
	protected final int iSelectSleepTime = 5;
	protected EnConcurrentLinkedQueue<SelectionKey> qSelectedKey = new EnConcurrentLinkedQueue<SelectionKey>(iMaxSocket);
	protected Selector mSelector = null;

	protected abstract NioSocketActionType onRead(SelectionKey key, Object objForThread);

	protected abstract NioSocketActionType onWrite(SelectionKey key, Object objForThread);
	protected SelectionKey onAccept(SocketChannel sc) {
		return registerRead(sc);
	}
	protected abstract NioSocketActionType onConnect(SelectionKey key, Object objForThread);
	protected abstract void onClose(SelectionKey key);

	protected abstract Object createObjectInThread();

	protected abstract void destroyObjectInThread(Object objForThread);

	protected boolean allowAccept(SocketChannel sc) {
		return true;
	}

	protected boolean isInvalidSocket(long lLimited, SelectionKey key) {
		return key.attachment() == null;
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
				SelectionKey sk = onAccept(sc);
				emptySocketController.add(sk, iQueue);
				return sk;
			} else {
				SocketChannelUtils.close(sc);
			}
		} else {
			SocketChannelUtils.close(sc);
		}
		return null;
	}

	protected SelectionKey registerRead(SocketChannel sc, Object objForClient) {
		SelectionKey selectionKey = null;
		try {
			sc.configureBlocking(false);
			sc.socket().setSoLinger(true, 0);
			selectionKey = sc.register(mSelector, SelectionKey.OP_READ, objForClient);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	protected final EmptySocketControllerNoSafe emptySocketController = new EmptySocketControllerNoSafe(7, 6,
			1 << (iMaxSocket-1)) {
		@Override
		public void onTimeout(long lLimited, SelectionKey[] lsData, int iSize) {
			for (int i = 0; i < iSize; i++) {
				SelectionKey key = lsData[i];
				if (key != null) {
					if (key.isValid() && isInvalidSocket(lLimited, key)) {
//						Log.logError("isInvalidSocket " + IPHelper.getRemoteSocketAddress((SocketChannel)key.channel()));
						Log.logError("isInvalidSocket");
						freeKeyAndSocket(key);
					}
					lsData[i] = null;
				}
			}
		}
	};

	protected class SelectThread extends Thread {
		private boolean bRunning = true;
		@Override
		public void run() {
			int iFreeTime = 0;
			while (bRunning) {
				int iSel;
				if (qSelectedKey.haveSpace()) {
					try {
						iSel = mSelector.selectNow();
					} catch (Exception e) {
						Log.logException(e);
						iSel = 0;
					}
					if (iSel > 0) {
						iFreeTime = 0;
						Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
						if (selectedKeys != null) {
							emptySocketController.checkTimeout();
							for (SelectionKey key : selectedKeys) {
								if (key.isValid()) {
									if (key.isAcceptable()) {
										SocketChannel sc = accept(key);
										if (sc != null) {
											acceptSocket(sc);
										}
									} else {
										key.interestOps(0);
										qSelectedKey.addUntilSuccess(key);
									}
								} else {
									freeKeyAndSocket(key);
								}
							}
							selectedKeys.clear();
						}
					} else {
						iFreeTime++;
					}
				} else {
					iFreeTime++;
				}
				if (iFreeTime >= 30) {
					ThreadUtils.sleep(iSelectSleepTime);
					iFreeTime = 30;
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
	private AutoThreadPool readThreaPool = new AutoThreadPool() {
		@Override
		protected boolean doAction(Object objForThread) {
			SelectionKey key = qSelectedKey.poll();
			if (key != null) {
				switch (readSocket(key, objForThread)) {
				case NSA_READ:
					key.interestOps(SelectionKey.OP_READ);
					break;
				case NSA_WRITE:
					key.interestOps(SelectionKey.OP_WRITE);
					break;
				case NSA_CLOSE:
					onClose(key);
					freeKeyAndSocket(key);
					break;
				case NSA_READ_WRITE:
					key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					break;
				}
				return true;
			}
			return false;
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
			int ops = key.readyOps();
			if ((ops & SelectionKey.OP_READ) != 0) {
				return onRead(key, objForThread);
			} else if ((ops & SelectionKey.OP_WRITE) != 0) {
				return onWrite(key, objForThread);
			} else if ((ops & SelectionKey.OP_CONNECT) != 0) {
				return onConnect(key, objForThread);
			}
		}
		return NioSocketActionType.NSA_CLOSE;
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

//	protected void interestRead(SelectionKey key) {
//		key.interestOps(SelectionKey.NSA_READ);
//	}

	protected SelectionKey interestRead(SocketChannel sc) {
		SelectionKey key = sc.keyFor(mSelector);
		if (key != null) {
			key.interestOps(SelectionKey.OP_READ);
		}
		return key;
	}

	protected SelectionKey registerWrite(SocketChannel sc) {
		SelectionKey selectionKey = null;
		try {
			selectionKey = sc.register(mSelector, SelectionKey.OP_WRITE, null);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	protected SelectionKey registerRead(SocketChannel sc) {
		SelectionKey selectionKey = null;
		try {
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

	protected void startThreads() {
		selectThread.start();
		readThreaPool.setSleepTime(10);
		readThreaPool.start();
	}

	public boolean openAndStart(int iPort, int iMinThread, int iMaxThread) {
		boolean rs = false;
		if (openSelector()) {
			addAccept(iPort);
			startThreads();
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
		return startAndBlock(iPort, Runtime.getRuntime().availableProcessors() + 1);
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

	protected boolean finishConnect(SelectionKey key) {
		boolean rs = false;
		SocketChannel socketChannel = (SocketChannel) key.channel();
//        if (socketChannel.isConnectionPending()) {
		try {
			rs = socketChannel.finishConnect();
		} catch (Exception e) {
			Log.log(e.getMessage());
		}
//        } else {
//            rs = socketChannel.isConnected();
//        }
		return rs;
	}
	protected SelectionKey addConnect(String ip, int port) {
		InetSocketAddress isa = new InetSocketAddress(ip, port);
		return addConnect(isa);
	}

	protected SelectionKey addConnect(InetSocketAddress isa) {
		SelectionKey selectionKey = null;
		try {
			SocketChannel sc = SocketChannel.open();
			if (sc != null && sc.socket() != null) {
				sc.configureBlocking(false);
				sc.connect(isa);
				selectionKey = sc.register(mSelector, SelectionKey.OP_CONNECT);
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return selectionKey;
	}

	public String doCommand(String sCmd) {
		return readThreaPool.showStatus() + " Queue:" + qSelectedKey.size();
	}
}
