package com.china.fortune.socket.shortConnection;

import java.net.ServerSocket;
import java.net.Socket;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.thread.AutoThreadPool;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.AutoIncreaseThreadPool;

// put the closing socket to selector to waiting close
public abstract class ShortConnectionServerAttach {
	final private int iLingerTimeout = 250;
	private int iRecvTimeout = 2500;
	private boolean bRunning = true;
	private ServerSocket mServerSocket = null;

	protected abstract Object createObjectInThread();

	protected abstract void destroyObjectInThread(Object objForThread);

	private EnConcurrentLinkedQueue<Socket> qAcceptSocket = new EnConcurrentLinkedQueue<Socket>(12);
	private Thread tAcceptAndRead = null;

	protected abstract void onRead(Socket sc, Object obj);

	public int getQueueSize() {
		return qAcceptSocket.size();
	}

	public int getThreadCount() {
		return readThreaPool.getWorkingThreadCount();
	}

	public void setTimeout(int iTimeout) {
		iRecvTimeout = iTimeout;
	}

	private boolean openServer(int iPort) {
		boolean rs = false;
		try {
			mServerSocket = new ServerSocket(iPort);
			rs = true;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public void startAndBlock(int iPort) {
		start(iPort);
		waitUntilStop();
		closeSocket();
		readThreaPool.waitToStop();
	}

	public boolean start(int iPort) {
		boolean rs = openServer(iPort);
		if (rs) {
			bRunning = true;
			readThreaPool.start();
			startAcceptThread();
		}
		return rs;
	}

	public void stopSelf() {
		bRunning = false;
		closeSocket();
		readThreaPool.setAllStop();
	}

	public void waitUntilStop() {
		if (tAcceptAndRead != null) {
			try {
				tAcceptAndRead.join();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			tAcceptAndRead = null;
		}
	}

	private void closeSocket() {
		try {
			mServerSocket.close();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	private void startAcceptThread() {
		tAcceptAndRead = new Thread() {
			@Override
			public void run() {
				acceptAction();
			}
		};
		tAcceptAndRead.start();
	}

	public void stop() {
		bRunning = false;
		closeSocket();
		waitUntilStop();
		readThreaPool.waitToStop();
	}

	private void acceptAction() {
		while (bRunning) {
			if (qAcceptSocket.haveSpace()) {
				try {
					Socket sc = mServerSocket.accept();
					if (sc != null) {
						sc.setSoTimeout(iRecvTimeout);
						sc.setSoLinger(true, iLingerTimeout);
						qAcceptSocket.addUntilSuccess(sc);
					}
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
			} else {
				ThreadUtils.sleep(ConstData.iThreadSleepTime);
			}
		}
	}

	private AutoThreadPool readThreaPool = new AutoThreadPool() {
		@Override
		protected boolean doAction(Object objForThread) {
			Socket sc = qAcceptSocket.poll();
			if (sc != null) {
				onRead(sc, objForThread);
				try {
					sc.close();
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
				return true;
			} else {
				return false;
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
}
