package com.china.fortune.cache;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferCache {
	static private ConcurrentLinkedQueue<ByteBuffer> sq1K = new ConcurrentLinkedQueue<ByteBuffer>();
	static private ConcurrentLinkedQueue<ByteBuffer> sq2K = new ConcurrentLinkedQueue<ByteBuffer>();
	static private ConcurrentLinkedQueue<ByteBuffer> sq1M = new ConcurrentLinkedQueue<ByteBuffer>();
	static final public int i1K = 1024;
	static final public int i2K = 2 * 1024;
	static final public int i1M = 1024 * 1024;

	static public ByteBuffer allocate(int iSize) {
		ByteBuffer bb;
		if (iSize <= i1K) {
			bb = sq1K.poll();
			if (bb != null) {
				bb.clear();
			} else {
				bb = ByteBuffer.allocate(i1K);
			}
		} else if (iSize <= i2K) {
			bb = sq2K.poll();
			if (bb != null) {
				bb.clear();
			} else {
				bb = ByteBuffer.allocate(i2K);
			}
		} else if (iSize <= i1M) {
			bb = sq1M.poll();
			if (bb != null) {
				bb.clear();
			} else {
				bb = ByteBuffer.allocate(i1M);
			}
		} else {
			bb = ByteBuffer.allocate(iSize);
		}
		return bb;
	}
	
	static public void free(ByteBuffer bb) {
		switch (bb.capacity()) {
		case i1K:
			sq1K.add(bb);
			break;
		case i2K:
			sq2K.add(bb);
			break;
		case i1M:
			sq1M.add(bb);
			break;
		}
	}
	
	static public ByteBuffer allocate1K() {
		ByteBuffer bb = sq1K.poll();
		if (bb != null) {
			bb.clear();
		} else {
			bb = ByteBuffer.allocate(i1K);
		}
		return bb;
	}	
	
	static public void free1K(ByteBuffer bb) {
		sq1K.add(bb);
	}
}
