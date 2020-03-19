package com.china.fortune.database;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.os.database.DbAction;

public class IncreaseLong {
	private AtomicLong maxId = new AtomicLong(0);

	public void loadMaxId(DbAction dbObj, String sTable, String sId) {
		String sSelect = "select max(%s) from %s;";
		long id = dbObj.selectLong(String.format(sSelect, sId, sTable));
		if (id > maxId.get()) {
			maxId.set(id);
		}
	}

	public void loadMaxId(DbAction dbObj, ArrayList<String> lsTable, String sId) {
		String sSelect = "select max(%s) from %s;";
		long iMaxId = 0;
		for (String sTable : lsTable) {
			long id = dbObj.selectLong(String.format(sSelect, sId, sTable));
			if (id > iMaxId) {
				iMaxId = id;
			}
		}
		maxId.set(iMaxId);
	}

	public long getMaxId() {
		return maxId.incrementAndGet();
	}
}
