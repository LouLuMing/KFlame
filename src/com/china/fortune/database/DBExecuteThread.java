package com.china.fortune.database;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.china.fortune.os.database.DbAction;
import com.china.fortune.thread.LoopThread;

public class DBExecuteThread extends LoopThread {
	private ConcurrentLinkedQueue<String> lsSql = new ConcurrentLinkedQueue<String>();
	private DbAction dbObj = null;

	public void addSql(String sSql) {
		lsSql.add(sSql);
	}
	
	public void init(DbAction db) {
		dbObj = db;
	}

	@Override
	protected void onClose() {
		if (dbObj != null) {
			dbObj.close();
		}
	};

	@Override
	protected boolean doAction() {
		if (!lsSql.isEmpty()) {
			do {
				String sSql = lsSql.poll();
				if (sSql != null) {
					dbObj.execute(sSql);
				} else {
					break;
				}
			} while (true);
		}	
		return true;
	}

}
