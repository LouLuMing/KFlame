package com.china.fortune.restfulHttpServer.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.os.database.DbAction;

public class DaySummer {
	private HashMap<String, SelectCount> mapField = new HashMap<String, SelectCount>();

	public void addSum(String sKey, String sTable, String sField, String sTicket) {
		SelectCount sc = new SelectCount(sTable, sField, sTicket, null);
		mapField.put(sKey, sc);
	}

	public void fillData(DbAction dbObj, int iDate, int iDays) {
		long startTicket = iDate;
		long endTicket = startTicket + iDays;

		for (Entry<String, SelectCount> field : mapField.entrySet()) {
			SelectCount sc = field.getValue();
			WhereSql ws = new WhereSql();
			ws.add(sc.sTicket, ">=", startTicket);
			ws.add(sc.sTicket, "<", endTicket);
			int sum = dbObj.selectInt(sc.toSql(ws));
			try {
				Field f = getClass().getField(field.getKey());
				f.setInt(this, sum);
			} catch (Exception e) {
			}
		}
	}

}
