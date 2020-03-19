package com.china.fortune.common;

import java.nio.ByteBuffer;

public class ByteBufferUtils {
	static public ByteBuffer alone(ByteBuffer bb) {
		int iLimit = bb.limit();
		ByteBuffer bbNew = ByteBuffer.allocate(iLimit);
		bbNew.put(bb.array(), 0, iLimit);
		return bbNew;
	}
}
