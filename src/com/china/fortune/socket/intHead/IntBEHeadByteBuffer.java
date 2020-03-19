package com.china.fortune.socket.intHead;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

public class IntBEHeadByteBuffer {
	private ByteBuffer bBuffer = null;
	private int iBuffer = 0;
	static public int icHeadLen = 4;
	static private String scCharset = "utf-8";
	static final int ciMaxSendRetry = 3;
	static public void setCharset(String s) {
		scCharset = s;
	}
	
	public IntBEHeadByteBuffer() {
		iBuffer = 1024;
		bBuffer = ByteBuffer.allocate(iBuffer);
	}

	public IntBEHeadByteBuffer(int iSize) {
		if (iSize <= 0) {
			iSize = 1024;
		}
		iBuffer = iSize;
		bBuffer = ByteBuffer.allocate(iSize);
	}

	final static public int getDataLen(ByteBuffer bb) {
		int iData = 0;
		byte[] bBuf = bb.array();
		for (int i = 0; i < icHeadLen; i++) {
			iData <<= 8;
			iData += ((int) (bBuf[i]) & 0xff);
		}
		return iData;
	}

	final static public void setDataLen(ByteBuffer bb) {
		byte[] bArray = bb.array();
		int iTotalLen = bb.position() - icHeadLen;
		for (int i = 0; i < icHeadLen; i++) {
			bArray[icHeadLen - i - 1] = (byte) (iTotalLen & 0xff);
			iTotalLen >>= 8;
		}
		bb.limit(bb.position());
		bb.position(0);
	}

	final static public void initByteBuffer(ByteBuffer bb) {
		bb.clear();
		bb.position(icHeadLen);
	}

	private void enlargeBuffer(int iLen) {
		if (bBuffer.capacity() < iLen) {
			ByteBuffer bb = ByteBuffer.allocate(iLen);
			bb.put(bBuffer);
			bb.position(bBuffer.position());
			bBuffer = bb;
		}
	}
	
	public boolean read(SocketChannel sc) {
		boolean rs = false;
		try {
			bBuffer.clear();
			int iRecv = sc.read(bBuffer);
			if (iRecv > icHeadLen) {
//				Log.logClass(ByteAction.toHexString(bBuffer.array(), 0, iRecv));
				int iLeft = getDataLen(bBuffer);
				if (bBuffer.position() == iLeft + icHeadLen) {
					bBuffer.limit(bBuffer.position());
					rs = true;
				} else {
//					Log.logClass("Error iRecv:" + iRecv + " iLeft:" + iLeft);
				}
			} else {
//				Log.logClass("Error iRecv:" + iRecv);
			}
		} catch (Exception e) {
//			Log.logClass(e.getMessage());
		}
		return rs;
	}
	
	public boolean read(SocketChannel sc, int iLimited) {
		boolean rs = false;
		try {
			bBuffer.position(0);
			bBuffer.limit(iLimited);
			int iRecv = sc.read(bBuffer);
			if (iRecv > icHeadLen) {
				int iLeft = getDataLen(bBuffer);
				if (bBuffer.position() == iLeft + icHeadLen) {
					bBuffer.limit(bBuffer.position());
					rs = true;
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public String readString(SocketChannel sc) {
		if (readBlocking(sc, bBuffer.capacity())) {
			return toString(bBuffer, scCharset);
		}
		return null;
	}
	
	public String readString(SocketChannel sc, String sCharset) {
		if (readBlocking(sc, bBuffer.capacity())) {
			return toString(bBuffer, sCharset);
		}
		return null;
	}
	
	public boolean readBlocking(SocketChannel sc, int iLimited) {
		boolean rs = false;
		try {
			int iRecv = 0;
			int iLeft = 0;
			final int iMaxRetry = 10;
			int iRetry = iMaxRetry;
			bBuffer.position(0);
			bBuffer.limit(icHeadLen);
			do {
				iRecv = sc.read(bBuffer);
				if (iRecv > 0) {
					iRetry = iMaxRetry;
					if (iLeft == 0) {
						if (bBuffer.position() >= icHeadLen) {
							iLeft = getDataLen(bBuffer);
							if (iLeft + icHeadLen < iLimited) {
								bBuffer.limit(iLeft + icHeadLen);
							} else {
								break;
							}
						}
					} else {
						if (bBuffer.position() == iLeft + icHeadLen) {
							rs = true;
							break;
						}
					}
				} else if (iRecv == 0) {
					Thread.sleep(50);
				} else {
					break;
				}
			} while (iRetry-- > 0);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}

		return rs;
	}

	static public ByteBuffer createIntHeadByteBuffer(String sSend, String sCharset) {
		ByteBuffer bb = null;
		try {
			byte[] pData = sSend.getBytes(sCharset);
			bb = ByteBuffer.allocate(pData.length + icHeadLen);
			initByteBuffer(bb);
			bb.put(pData);
			setDataLen(bb);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return bb;
	}

	static public ByteBuffer createIntHeadByteBuffer(byte[] pData) {
		ByteBuffer bb = ByteBuffer.allocate(pData.length + icHeadLen);
		initByteBuffer(bb);
		bb.put(pData);
		setDataLen(bb);
		return bb;
	}

	public ByteBuffer toIntHeadByteBuffer(byte[] pData) {
		initByteBuffer(bBuffer);
		bBuffer.put(pData);
		setDataLen(bBuffer);
		return bBuffer;
	}

	public ByteBuffer toIntHeadByteBuffer(String sSend) {
		try {
			byte[] pData = sSend.getBytes(scCharset);
			initByteBuffer(bBuffer);
			bBuffer.put(pData);
			setDataLen(bBuffer);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return bBuffer;
	}

	public boolean sendString(SocketChannel sc, String sSend) {
		boolean rs = false;
		try {
			byte[] pData = sSend.getBytes(scCharset);
			enlargeBuffer(pData.length + icHeadLen);
			initByteBuffer(bBuffer);
			bBuffer.put(pData);
			setDataLen(bBuffer);
//			Log.logClass(ByteAction.toHexString(bBuffer.array(), 0, bBuffer.limit()));
			int iRetry = ciMaxSendRetry;
			while (bBuffer.hasRemaining() && iRetry-- > 0) {
				sc.write(bBuffer);
			}
			if (bBuffer.remaining() == 0) {
				rs = true;
			}
		} catch (Exception e) {
//			Log.logException(e);
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	static public boolean sendStringG(SocketChannel sc, String sSend) {
		boolean rs = false;
		try {
			byte[] pData = sSend.getBytes(scCharset);
			ByteBuffer bb = ByteBuffer.allocate(pData.length + icHeadLen);
			initByteBuffer(bb);
			bb.put(pData);
			setDataLen(bb);
			int iRetry = ciMaxSendRetry;
			while (bb.hasRemaining() && iRetry-- > 0) {
				sc.write(bb);
			}
			if (bb.remaining() == 0) {
				rs = true;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}
	
	public boolean send(SocketChannel sc, ByteBuffer bb) {
		boolean rs = false;
		try {
			bb.position(0);
			int iRetry = ciMaxSendRetry;
			while (bb.hasRemaining() && iRetry-- > 0) {
				sc.write(bb);
			}
			if (bb.remaining() == 0) {
				rs = true;
			}
		} catch (Exception e) {
			rs = false;
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public boolean send(SocketChannel sc) {
		boolean rs = false;
		try {
			bBuffer.position(0);
			while (bBuffer.hasRemaining()) {
				sc.write(bBuffer);
			}
			rs = true;
		} catch (Exception e) {
			rs = false;
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public ByteBuffer getByteBuffer() {
		return bBuffer;
	}

	public String toString() {
		String sText = null;
		try {
			sText = new String(bBuffer.array(), icHeadLen,
					bBuffer.limit() - icHeadLen, scCharset);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sText;
	}
	
	public String toString(String sCharset) {
		String sText = null;
		try {
			sText = new String(bBuffer.array(), icHeadLen,
					bBuffer.limit() - icHeadLen, sCharset);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sText;
	}

	static public String toString(ByteBuffer bb, int iLen, String sCharset) {
		String sText = null;
		try {
			sText = new String(bb.array(), icHeadLen,
					iLen - icHeadLen, sCharset);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sText;
	}

	static public String toString(ByteBuffer bb, String sCharset) {
		String sText = null;
		try {
			sText = new String(bb.array(), icHeadLen,
					bb.limit() - icHeadLen, sCharset);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sText;
	}
	
	public static void main(String[] args) {
		String sSend = "<message from='123@w.w.w' to='222@w.w.w' id='123213123'>";
		ByteBuffer bBuffer = ByteBuffer.allocate(1024);
		try {
			byte[] pData = sSend.getBytes(ConstData.sSocketCharset);
			byte[] pLen = ByteAction.intToByteLE(pData.length);

			bBuffer.clear();
			bBuffer.put(pLen);
			bBuffer.put(pData);
			bBuffer.limit(bBuffer.position());

			Log.log(pData.length + ":" + bBuffer.position() + ":"
					+ bBuffer.limit());
		} catch (Exception e) {
		}
	}
}
