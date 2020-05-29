package com.china.fortune.socket.pointToPoint;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.AutoIncreaseThreadPool;
import com.china.fortune.thread.AutoThreadPool;

public abstract class P2PThreadServer extends P2PServer {
	private EnConcurrentLinkedQueue<ByteBuffer> qFreeQueue = new EnConcurrentLinkedQueue<ByteBuffer>(14);
	private EnConcurrentLinkedQueue<ByteBuffer> qWorkQueue = new EnConcurrentLinkedQueue<ByteBuffer>(14);
	
	protected abstract boolean doReadPacket(SocketChannel sc, ByteBuffer bb);
	protected abstract void doWork(Object objForThread, ByteBuffer bb);
	protected abstract Object createObjForThread();
	private int iByteBufferLen = 2 * 1024;
	public void setByteBufferLength(int iLen) {
		iByteBufferLen = iLen;
	}
	
	public int getWorkQueueSize() {
		return qWorkQueue.size();
	}

	public int getWorkingThreadCount() {
		return threadPool.getWorkingThreadCount();
	}
	
	@Override
	protected boolean onRead(SocketChannel sc) {
		boolean rs = false;
		ByteBuffer bb = qFreeQueue.poll();
		if (bb == null) {
			bb = ByteBuffer.allocate(iByteBufferLen);
		}
		if (bb != null) {
			if (doReadPacket(sc, bb)) {
				rs = qWorkQueue.add(bb);
			} else {
				qFreeQueue.add(bb);
			}
		}
		return rs;
	}
	
	private AutoThreadPool threadPool = new AutoThreadPool() {
		@Override
		protected boolean doAction(Object objForThread) {
			ByteBuffer bb = qWorkQueue.poll();
			if (bb != null) {
				doWork(objForThread, bb);
				qFreeQueue.add(bb);
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onDestroy(Object objForThread) {
		}

		@Override
		protected Object onCreate() {
			return createObjForThread();
		}
	};
	
	public boolean start(int iPort) {
		if (super.start(iPort)) {
			threadPool.start();
			return true;
		} else {
			return false;
		}
	}
}
