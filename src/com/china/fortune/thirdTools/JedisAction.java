package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import redis.clients.jedis.Jedis;

public class JedisAction {
	private Jedis jedis = null;
	private String sServer = null;
	private int iServer = 6379;
	private String sAuth = null;

	public JedisAction(String s1, int i, String s2) {
		sServer = s1;
		iServer = i;
		sAuth = s2;
	}

	public void close() {
		if (jedis != null) {
			try {
				jedis.quit();
			} catch (Exception e) {
				Log.logException(e);
			}
			try {
				jedis.disconnect();
			} catch (Exception e) {
				Log.logException(e);
			}
			jedis = null;
		}
	}
	
	synchronized private void open() {
		close();
		try {
			jedis = new Jedis(sServer, iServer);
			if (sAuth != null) {
				jedis.auth(sAuth);
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public void lpushRetry(String sKey, String[] lsValue) {
		if (jedis == null) {
			open();
		}
		for (int i = 0; i < 2; i++) {
			if (lpush(sKey, lsValue)) {
				break;
			} else {
				open();
			}
		}
	}
	
	protected boolean lpush(String sKey, String[] lsValue) {
		boolean rs = false;
		try {
			jedis.lpush(sKey, lsValue);
			rs = true;
		} catch (Exception e) {
		}
		return rs;
	}
	
	protected boolean append(String sKey, String sValue) {
		boolean rs = false;
		try {
			jedis.append(sKey, sValue);
			rs = true;
		} catch (Exception e) {
		}
		return rs;
	}
	
	public void appendRetry(String sKey, String sValue) {
		if (jedis == null) {
			open();
		}
		for (int i = 0; i < 2; i++) {
			if (append(sKey, sValue)) {
				break;
			} else {
				open();
			}
		}
	}
	
	protected boolean set(String sKey, String sValue) {
		boolean rs = false;
		try {
			jedis.set(sKey, sValue);
			rs = true;
		} catch (Exception e) {
		}
		return rs;
	}
	
	public void setRetry(String sKey, String sValue) {
		if (jedis == null) {
			open();
		}
		for (int i = 0; i < 2; i++) {
			if (set(sKey, sValue)) {
				break;
			} else {
				open();
			}
		}
	}
	
	protected String get(String sKey) {
		String sValue = null;
		try {
			sValue = jedis.get(sKey);
		} catch (Exception e) {
		}
		return sValue;
	}
	
	public String getRetry(String sKey) {
		if (jedis == null) {
			open();
		}
		String sValue = null;
		for (int i = 0; i < 2; i++) {
			sValue = get(sKey);
			if (sValue != null) {
				break;
			} else {
				open();
			}
		}
		return sValue;
	}
}
