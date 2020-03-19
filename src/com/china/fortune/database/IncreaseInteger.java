package com.china.fortune.database;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.os.database.DbAction;

public class IncreaseInteger {
	private AtomicInteger maxId = new AtomicInteger(0);

	public void loadMaxId(DbAction dbObj, String sTable, String sId) {
		String sSelect = "select max(%s) from %s;";
		int id = dbObj.selectInt(String.format(sSelect, sId, sTable));
		if (id > maxId.get()) {
			maxId.set(id);
		}
	}

	public void loadMaxId(DbAction dbObj, ArrayList<String> lsTable, String sId) {
		String sSelect = "select max(%s) from %s;";
		int iMaxId = 0;
		for (String sTable : lsTable) {
			int id = dbObj.selectInt(String.format(sSelect, sId, sTable));
			if (id > iMaxId) {
				iMaxId = id;
			}
		}
		maxId.set(iMaxId);
	}

	public int getMaxId() {
		return maxId.incrementAndGet();
	}
}
