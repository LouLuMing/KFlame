package com.china.fortune.messageServer;

import com.china.fortune.common.AttachHelper;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageClient extends AttachHelper {
	static public int iHeadLength = 4;
	static final public int iMaxDataLength = 32 * 1024;
	static final public int iMaxBufferLen = iHeadLength + iMaxDataLength;
	static final private int iReloginTimeSpan = 250;
	static final private int iAliveTimeSpan = 3 * 60 * 1000;

	public SocketChannel scChannel;
	public String sUid;
	public String sToken;
	public long lLastLoginTime;
	public long lLastAliveTime;
	
	public String getUid() {
		return sUid;
	}
	
	public boolean checkToken(String s) {
		return StringAction.compareTo(s, sToken) == 0;
	}
	
	public MessageClient() {
		scChannel = null;
		sToken = null;
	}

	public void clear() {
		bbData = null;
		sToken = null;
	}
	
	public boolean isAllowLogin() {
		if (System.currentTimeMillis() - lLastLoginTime > iReloginTimeSpan) {
			lLastLoginTime = System.currentTimeMillis();
			lLastAliveTime = lLastLoginTime;
			return true;
		}
		return false;
	}
	
	public void setAlive() {
		lLastAliveTime = System.currentTimeMillis(); 
	}
	
	public boolean isAlive() {
		if (System.currentTimeMillis() - lLastAliveTime > iAliveTimeSpan) {
			return false;
		}
		return true;
	}
	
	//public ByteBuffer bHead = ByteBuffer.allocate(IMServerConst.icPacketHeadLen);
	public ByteBuffer bbData = ByteBuffer.allocate(iMaxBufferLen);
	public int iDataLength = 0;
	public void initBuffer() {
		bbData.clear();
	}

	public boolean parseHead() {
		if (bbData.position() >= iHeadLength) {
			iHeadLength = 0;
			for (int i = 0; i < iHeadLength; i++) {
				iHeadLength <<= 8;
				iHeadLength += ((int) (bbData.get(i)) & 0xff);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean readCompleted() {
		return bbData.position() >= iHeadLength + iDataLength;
	}

	public String getBody(String sCharset) {
		String sText = null;
		try {
			sText = new String(bbData.array(), iHeadLength,
					iDataLength - iHeadLength, sCharset);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sText;
	}

	public int removeUsedData() {
		int iLeft = bbData.position() - (iHeadLength + iDataLength);
		if (iLeft > 0) {
			bbData.position(0);
			bbData.put(bbData.array(), iHeadLength + iDataLength, iLeft);
			iDataLength = 0;
		} else {
			bbData.clear();
			iDataLength = 0;
		}
		return iLeft;
	}
}
