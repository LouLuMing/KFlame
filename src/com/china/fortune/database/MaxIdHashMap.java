package com.china.fortune.database;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.database.mySql.MySqlDbAction;

public class MaxIdHashMap {
	private HashMap<String, AtomicInteger> mapUnicodeId = new HashMap<String, AtomicInteger>();
	
	public void addTableAndId(MySqlDbAction dbObj, String table, String id) {
		String sSelect = "select max(%s) from %s;";
		int maxId = dbObj.selectInt(String.format(sSelect, id, table));
		if (maxId < 0) {
			maxId = 0;
		}
		mapUnicodeId.put(createKey(table, id), new AtomicInteger(maxId));
	}
	
	private String createKey(String table, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(table);
		sb.append('_');
		sb.append(id);
		return sb.toString();
	}
	
	public int getMaxId(String table, String id) {
		int maxId = -1;
		AtomicInteger ai = mapUnicodeId.get(createKey(table, id));
		if (ai != null) {
			maxId = ai.incrementAndGet();
		}
		return maxId;
	}
}
