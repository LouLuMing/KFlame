package com.china.fortune.database.mySql;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.global.Log;

public class MySqlManager {
	private String sMySqlUser = null;
	private String sMySqlPasswd = null;
	private int iMinAliveCount = Runtime.getRuntime().availableProcessors();
	private AtomicInteger iMySqlCount = new AtomicInteger(0);
	private String sMySqlServer = null;
	private String sMySqlDBName = null;
	private ConcurrentLinkedQueue<MySqlDbAction> lsDBObjs = new ConcurrentLinkedQueue<MySqlDbAction>();

	public boolean init(String sIP, String sDBName, String sSqlUser, String sSqlPasswd) {
		Log.logClass(sIP + ":" + sSqlUser + ":" + sSqlPasswd + ":" + sDBName);
		sMySqlServer = sIP;
		sMySqlDBName = sDBName;
		sMySqlUser = sSqlUser;
		sMySqlPasswd = sSqlPasswd;
		MySqlDbAction dbObj = get();
		if (dbObj != null) {
			free(dbObj);
			return true;
		} else {
			return false;
		}
	}

	public MySqlDbAction get() {
		MySqlDbAction dbObj = null;
		if (sMySqlServer != null) {
            if (!lsDBObjs.isEmpty()) {
                dbObj = lsDBObjs.poll();
            }
            if (dbObj == null) {
                dbObj = new MySqlDbAction();
                if (dbObj.open(sMySqlServer, sMySqlDBName, sMySqlUser, sMySqlPasswd)) {
                    iMySqlCount.incrementAndGet();
                } else {
                    dbObj = null;
                }
            }
        }
		return dbObj;
	}

	public int size() {
		return iMySqlCount.get();
	}

	public void free(MySqlDbAction dbObj) {
		if (dbObj != null) {
			int iCount = lsDBObjs.size();
			if (iCount > iMinAliveCount) {
				dbObj.close();
				iMySqlCount.decrementAndGet();
			} else {
				lsDBObjs.add(dbObj);
			}
		}
	}

	public void clear() {
		while (!lsDBObjs.isEmpty()) {
			MySqlDbAction dbObj = lsDBObjs.poll();
			if (dbObj != null) {
				dbObj.close();
				iMySqlCount.decrementAndGet();
			}
		}
	}
}
