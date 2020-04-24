package com.china.fortune.messageServer;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.intHead.IntBEHeadByteBuffer;
import com.china.fortune.socket.selectorManager.NioLoginAttach;
import com.china.fortune.string.StringAction;

public abstract class MessageServer extends NioLoginAttach {

	static final public int iClientMapSize = 1024 * 64;

	final private String sCharset = "utf-8";
	protected ClientController ccChild = new ClientController(iClientMapSize);

	abstract protected String parseUid(String sRecv);

	abstract protected String parseToken(String sRecv);

	abstract protected String getToken(String sUid);

	abstract protected Object doLogin(String sUid, MessageClient ct);

	abstract protected void onClose(MessageClient ct, Object loginObj);

	abstract protected boolean doRead(String sUid, Object o, String sText);

	@Override
	protected boolean onRead(SocketChannel sc, Object objForClient, Object objForThread) {
		boolean rs = true;
		MessageClient ct = (MessageClient) objForClient;
		if (ct.bbData.position() == 0) {
			ct.bbData.limit(IntBEHeadByteBuffer.icHeadLen);
		}
		if (SocketChannelHelper.read(sc, ct.bbData) > 0) {
			if (ct.iDataLength == 0) {
				if (ct.parseHead()) {
					if (ct.iDataLength <= MessageClient.iMaxDataLength) {
						ct.bbData.limit(ct.iDataLength + MessageClient.iHeadLength);
						rs = SocketChannelHelper.read(sc, ct.bbData) >= 0;
					} else {
						MessageLog.log(ct.getUid() + ":Length out of limit:" + ct.iDataLength);
						rs = false;
					}
				}
			}
			if (ct.readCompleted()) {
				String sRecv = ct.getBody(sCharset);
				rs = doRead(ct.sUid, ct.attachment(), sRecv);
				ct.bbData.clear();
			}
		} else {
			rs = false;
		}
		return rs;
	}

	@Override
	protected void onClose(SelectionKey key, Object objForClient) {
		if (objForClient != null) {
			MessageClient ct = (MessageClient) objForClient;
			onClose(ct, ct.attachment());
			ct.attach(null);
		}
	}

	@Override
	protected Object createObjectInThread() {
		return new IntBEHeadByteBuffer(MessageClient.iMaxBufferLen);
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}

	public boolean sendMessage(MessageClient ct, String sMsg) {
		boolean rs = false;
		if (ct != null && sMsg != null) {
			if (ct.scChannel != null) {
				rs = IntBEHeadByteBuffer.sendStringG(ct.scChannel, sMsg);
			}
			if (rs) {
				MessageLog.log("sendMsg:" + ct.sUid + ":OK:" + sMsg);
			} else {
				MessageLog.log("sendMsg:" + ct.sUid + ":Err:" + sMsg);
			}
		}
		return rs;
	}

	public boolean sendMessage(String sUid, String sMsg) {
		MessageClient ct = ccChild.get(sUid);
		return sendMessage(ct, sMsg);
	}

	private boolean checkToken(MessageClient ct, String sUid, String sToken) {
		boolean rs = ct.checkToken(sToken);
		if (!rs) {
			String sValidToken = getToken(sUid);
			if (StringAction.compareTo(sToken, sValidToken) == 0) {
				ct.sToken = sValidToken;
				rs = true;
			}
		}
		return rs;
	}

	private MessageClient getClient(String sUid, String sToken, SocketChannel sc) {
		MessageClient obj = null;
		MessageClient ct = ccChild.get(sUid);
		if (ct != null) {
			if (ct.isAllowLogin()) {
				if (checkToken(ct, sUid, sToken)) {
					if (ct.scChannel != null) {
						freeKeyAndSocket(ct.scChannel);
						MessageLog.log(sUid + ":close another socket");
					}
					ct.scChannel = sc;
					obj = ct;
				} else {
					MessageLog.log(sUid + ":" + ct.sToken + ":token is invalid");
				}
			} else {
				MessageLog.log(sUid + ":login too frequently");
			}
		} else {
			String sValidToken = getToken(sUid);
			if (sValidToken != null) {
				if (StringAction.compareTo(sValidToken, sToken) == 0) {
					ct = ccChild.createClient(sUid);
					ct.sToken = sToken;
					ct.scChannel = sc;
					obj = ct;
				} else {
					MessageLog.log(sUid + ":" + sValidToken + ":token is invalid");
				}
			} else {
				MessageLog.log(sUid + ":uid is invalid");
			}
		}
		return obj;
	}

	@Override
	protected Object onLogin(SocketChannel sc, Object objForThread) {
		MessageClient ct = null;
		IntBEHeadByteBuffer ihbb = (IntBEHeadByteBuffer) objForThread;
		if (ihbb.read(sc)) {
			String sRecv = ihbb.toString(sCharset);
			MessageLog.log("onLogin:" + sRecv);
			String sUid = parseUid(sRecv);
			if (sUid != null) {
				String sToken = parseToken(sRecv);
				if (sToken != null) {
					ct = getClient(sUid, sToken, sc);
					if (ct != null) {
						ct.initBuffer();
						Object o = doLogin(sUid, ct);
						if (o != null) {
							ct.attach(o);
						} else {
							ct = null;
						}
					}
				} else {
					MessageLog.log(sUid + ":token null");
				}
			} else {
				MessageLog.log("uid is null");
			}
		}
		return ct;
	}
}
