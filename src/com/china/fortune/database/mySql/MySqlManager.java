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

	public boolean init(String sIP, String sDBName, String sSqlUser, String sSqlPasswd) {
		Log.logClass(sIP + ":" + sSqlUser + ":" + sSqlPasswd + ":" + sDBName);
		sMySqlServer = sIP;
		sMySqlDBName = sDBName;
		sMySqlUser = sSqlUser;
		sMySqlPasswd = sSqlPasswd;
		return true;
	}

	private ConcurrentLinkedQueue<MySqlDbAction> queueDB = new ConcurrentLinkedQueue<MySqlDbAction>();

	public MySqlDbAction get() {
		MySqlDbAction dbObj = null;
		if (sMySqlServer != null) {
            if (!queueDB.isEmpty()) {
                dbObj = queueDB.poll();
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
			int iCount = queueDB.size();
			if (iCount > iMinAliveCount) {
				dbObj.close();
				iMySqlCount.decrementAndGet();
			} else {
				queueDB.add(dbObj);
			}
		}
	}

	public void clear() {
		while (!queueDB.isEmpty()) {
			MySqlDbAction dbObj = queueDB.poll();
			if (dbObj != null) {
				dbObj.close();
			}
		}
	}
}
