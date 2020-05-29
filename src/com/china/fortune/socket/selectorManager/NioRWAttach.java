package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.Log;
import com.china.fortune.socket.EmptySocketControllerNoSafe;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.struct.FastList;
import com.china.fortune.thread.ThreadUtils;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// accept, onaccept (no more cost)
// first start open selector(call start function)
// second and accept

public abstract class NioRWAttach {
	private int iDefListSize = 128 * 1024;
	protected Selector mSelector = null;

	protected abstract NioSocketActionType onRead(SelectionKey key, Object objForThread);

	protected abstract NioSocketActionType onWrite(SelectionKey key, Object objForThread);

	protected abstract void onClose(SelectionKey key);

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
			}
		}
		SocketChannelHelper.close(sc);
		return null;
	}

	protected final EmptySocketControllerNoSafe emptySocketController = new EmptySocketControllerNoSafe(7, 5,
			iDefListSize) {
		@Override
		public void onTimeout(long lLimited, SelectionKey[] lsData, int iSize) {
			for (int i = 0; i < iSize; i++) {
				SelectionKey key = lsData[i];
				if (key != null) {
					if (key.isValid() && isInvalidSocket(lLimited, key.attachment())) {
//						Log.logError("isInvalidSocket " + IPHelper.getRemoteSocketAddress((SocketChannel)key.channel()));
						freeKeyAndSocket(key);
					}
					lsData[i] = null;
				}
			}
		}
	};

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
				Iterator<SelectionKey> it = selectedKeys.iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					if (key.isValid()) {
						if (key.isAcceptable()) {
							SocketChannel sc = accept(key);
							if (sc != null) {
								acceptSocket(sc);
							}
						} else {
							key.interestOps(0);
							qSelectedKey.add(key);
						}
					} else {
						freeKeyAndSocket(key);
					}
				}
				selectedKeys.clear();
			}
		}
	}

	public void interestOps(SelectionKey key, int ops) {
		try {
			key.interestOps(ops);
		} catch (Exception e) {
		}
	}

	private boolean hasFreeThread = true;
	private NioThreadPool readThreaPool = new NioThreadPool() {
		@Override
		protected void doWorkInThread(Object objForThread) {
			boolean isNeedDecrement = true;
			int iQuitRequest = 0;
			int iContinuouslyWork = 0;
			long lThreadId = Thread.currentThread().getId();
			FastList<SelectionKey> qSelectedKey = new FastList<>(iDefListSize);
			while (bRunning) {
				selectAction(qSelectedKey);
				int iSize = qSelectedKey.size();
				if (iSize > 0) {
					for (int i = 0; i < iSize; i++) {
						SelectionKey key = qSelectedKey.getAndSetNull(i);
						if (key != null) {
							readSocket(key, objForThread);
							if (hasFreeThread) {
								iQuitRequest = 0;
								if (++iContinuouslyWork > iLimitContinuousWork) {
									if (iTotalThreadCount.get() < iMaxThread) {
										iTotalThreadCount.getAndIncrement();
										addNewThread();
									} else {
										hasFreeThread = false;
									}
									iContinuouslyWork = 0;
								}
							}
						}
					}
					qSelectedKey.size(0);
				} else {
					iContinuouslyWork = 0;
					if (lFirstThreadId != lThreadId) {
						if (++iQuitRequest > iLimitQuitRequest) {
							if (iTotalThreadCount.get() > iMinThread) {
								if (iTotalThreadCount.getAndDecrement() > iMinThread) {
									isNeedDecrement = false;
									hasFreeThread = true;
									break;
								} else {
									iTotalThreadCount.getAndIncrement();
								}
							}
						}
					}
					ThreadUtils.sleep(iThreadSleep);
				}
			}
			if (isNeedDecrement) {
				iTotalThreadCount.getAndDecrement();
			}
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

	protected void readSocket(SelectionKey key, Object objForThread) {
		NioSocketActionType nsat = NioSocketActionType.OP_CLOSE;
		if (key.isValid()) {
			if (key.isReadable()) {
				nsat = onRead(key, objForThread);
			} else if (key.isWritable()) {
				nsat = onWrite(key, objForThread);
			}
		}
		switch (nsat) {
			case OP_READ:
				key.interestOps(SelectionKey.OP_READ);
				break;
			case OP_WRITE:
				key.interestOps(SelectionKey.OP_WRITE);
				break;
			case OP_CLOSE:
				onClose(key);
				freeKeyAndSocket(key);
				break;
		}
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
				for (SelectionKey key : mSelector.keys()) {
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

	protected void interestRead(SelectionKey key) {
		key.interestOps(SelectionKey.OP_READ);
	}

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

	public void setSleepTime(int sleep) {
		readThreaPool.setSleepTime(sleep);
	}

	public void setThread(int iMin, int iMax) {
		readThreaPool.setThread(iMin, iMax);
	}

	public boolean openAndStart(int iPort) {
		boolean rs = false;
		if (openSelector()) {
			addAccept(iPort);
			readThreaPool.start();
			rs = true;
		}
		return rs;
	}

	public void join() {
		readThreaPool.join();
	}

	public void stop() {
		readThreaPool.waitToStop();
		closeSelector();
	}

	public boolean startAndBlock(int iPort) {
		boolean rs = false;
		if (openAndStart(iPort)) {
			join();
			stop();
			rs = true;
		}
		return rs;
	}

	public String doCommand(String sCmd) {
		return readThreaPool.showStatus();
	}
}
