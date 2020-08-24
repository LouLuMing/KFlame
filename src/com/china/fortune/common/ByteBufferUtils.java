package com.china.fortune.common;

import com.china.fortune.global.Log;

import java.nio.ByteBuffer;

public class ByteBufferUtils {
	static final public char HEX_DIGEST_UPPER[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	static public ByteBuffer aloneDirect(ByteBuffer bb) {
		int iLimit = bb.limit();
		ByteBuffer bbNew = ByteBuffer.allocate(iLimit);
		int pos = bb.position();
		bb.position(0);
		bbNew.put(bb);
		bb.position(pos);
		bbNew.position(pos);
		return bbNew;
	}

	static public ByteBuffer alone(ByteBuffer bb) {
		int iLimit = bb.limit();
		ByteBuffer bbNew = ByteBuffer.allocate(iLimit);
		bbNew.put(bb.array(), 0, iLimit);
		bbNew.position(bb.position());
		return bbNew;
	}

	static public ByteBuffer largeDirect(ByteBuffer bb, int iTotalLength) {
		if (iTotalLength > bb.capacity()) {
			ByteBuffer bbNew = ByteBuffer.allocate(iTotalLength);
			int pos = bb.position();
			bb.position(0);
			bbNew.put(bb);
			bb.position(pos);
			bbNew.position(pos);
			return bbNew;
		} else {
			return bb;
		}
	}

	static public ByteBuffer large(ByteBuffer bb, int iTotalLength) {
		if (iTotalLength > bb.capacity()) {
			ByteBuffer bbNew = ByteBuffer.allocate(iTotalLength);
			bb.put(bb.array(), 0, bb.position());
			return bbNew;
		} else {
			return bb;
		}
	}

	static public int indexOf(ByteBuffer bbData, int iOff, byte[] bCompare) {
		return indexOf(bbData, iOff, bbData.position()+1, bCompare);
	}

	static public int indexOf(ByteBuffer bbData, int iStart, int iEnd , byte[] bCompare) {
		int iIndex = -1;
		for (int i = iStart; i < iEnd - bCompare.length; i++) {
			boolean bFound = true;
			for (int j = 0; j < bCompare.length; j++) {
				if (bbData.get(i + j) != bCompare[j]) {
					bFound = false;
					break;
				}
			}
			if (bFound) {
				iIndex = i;
				break;
			}
		}
		return iIndex;
	}

	static public int indexOf(ByteBuffer bbData, int iOff, int iLen, byte bCompare) {
		for (int i = iOff; i < iLen ; i++) {
			if (bbData.get(i) == bCompare) {
				return i;
			}
		}
		return -1;
	}

	static public int indexOf(ByteBuffer bbData, int iOff, byte bCompare) {
		return indexOf(bbData, iOff, bbData.position()+1, bCompare);
	}

	static public int lastIndexOf(ByteBuffer bbData, int iOff, byte[] bCompare) {
		int iIndex = -1;
		for (int i = iOff; i >= 0; i--) {
			boolean bFound = true;
			for (int j = 0; j < bCompare.length; j++) {
				if (bbData.get(i + j) != bCompare[j]) {
					bFound = false;
					break;
				}
			}
			if (bFound) {
				iIndex = i;
				break;
			}
		}
		return iIndex;
	}

	static public byte[] toByte(ByteBuffer bb, int iStart, int iEnd) {
		byte[] bData = new byte[iEnd - iStart];
		int j = 0;
		for (int i = iStart; i < iEnd; i++) {
			bData[j++] = bb.get(i);
		}

//		System.arraycopy(bb.array(), iStart, bData, 0, bData.length);

		return bData;
	}

	static public String toString(ByteBuffer bb) {
		return new String(toByte(bb, 0, bb.position()));
	}

	static public String toString(ByteBuffer bb, int iStart, int iEnd) {
		return new String(toByte(bb, iStart, iEnd));
	}

	static public String toHexString(ByteBuffer bb, int iOff, int iCount) {
		StringBuilder sb = new StringBuilder(iCount * 2 + 1);
		for (int i = iOff; i < iOff + iCount; i++) {
			sb.append(HEX_DIGEST_UPPER[((bb.get(i) >> 4) & 0x0f)]);
			sb.append(HEX_DIGEST_UPPER[(bb.get(i) & 0x0f)]);
		}
		return sb.toString();
	}

	static public int getLength(ByteBuffer bb, int iStart, int iEnd) {
		int iLen = 0;
		for (int j = iStart; j <= iEnd; j++) {
			byte b = bb.get(j);
			if (b >= (byte) '0' && b <= (byte) '9') {
				iLen *= 10;
				iLen += (b - '0');
			} else if (b == 0x0d) {
				break;
//			} else {
//				break;
			}
		}
		return iLen;
	}

	static public byte[] toByte(ByteBuffer bbData) {
		int iLen = bbData.limit();
		byte[] bData = new byte[iLen];
		bbData.get(bData);
		bbData.position(0);
		return bData;
	}

	public static ByteBuffer byteToByteBuffer(byte[] bData) {
		if (bData != null) {
			ByteBuffer bb = ByteBuffer.wrap(bData);
			Log.logClass(bb.position() + " " + bb.limit());
			return bb;
		} else {
			return null;
		}
	}
	public static void main(String[] args) {
		ByteBuffer bb = ByteBuffer.allocate(1000);
		bb.put("hello".getBytes());
		bb.flip();
		Log.logClass(bb.position() + ":" + bb.limit());
		bb.position(3);
		Log.logClass(bb.position() + ":" + bb.limit());
		bb.compact();
		Log.logClass(bb.position() + ":" + bb.limit());
		bb.flip();
		byte[] bData = toByte(bb);
		Log.log(new String(bData));
//		Log.logClass(bb.position() + ":" + bb.limit());
//		ByteBuffer cc = alone(bb);
//
//		Log.logClass(cc.position() + ":" + cc.limit());
//		Log.log(new String(cc.array()));

	}


}

