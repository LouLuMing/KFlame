package com.china.fortune.messageServer;

import java.util.HashMap;

public class ClientController {
	public HashMap<String, MessageClient> mapClient = null;

	public ClientController(int iMaxSize) {
		mapClient = new HashMap<String, MessageClient>(iMaxSize);
	}

	public void clear() {
		try {
			for (MessageClient ct : mapClient.values()) {
				ct.scChannel = null;
			}
		} catch (Exception e) {
		}
		mapClient.clear();
	}

	public MessageClient getClient(String sUid) {
		return mapClient.get(sUid);
	}

	public MessageClient getOrCreateClient(String sUid) {
		MessageClient ct = mapClient.get(sUid);
		if (ct == null) {
			ct = createClient(sUid);
		}
		return ct;
	}

	public MessageClient get(String sUid) {
		return mapClient.get(sUid);
	}

	public MessageClient createClient(String sUid) {
		MessageClient ct = new MessageClient();
		ct.sUid = sUid;
		synchronized (this) {
			mapClient.put(sUid, ct);
		}
		return ct;
	}

}
