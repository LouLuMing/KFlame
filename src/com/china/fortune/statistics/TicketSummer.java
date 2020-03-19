package com.china.fortune.statistics;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.common.DateAction;
import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.restfulHttpServer.entity.SelectCount;

public class TicketSummer {
	public int date;
	private HashMap<String, SelectCount> mapField = new HashMap<String, SelectCount>();
	
	public void addCount(String sKey, String sTable, String sTicket) {
		SelectCount sc = new SelectCount(sTable, sTicket, null);
		mapField.put(sKey, sc);
	}
	
	public void addCount(String sKey, String sTable, String sTicket, WhereSql ws) {
		SelectCount sc = new SelectCount(sTable, sTicket, ws);
		mapField.put(sKey, sc);
	}
	
	public void addSum(String sKey, String sTable, String sField, String sTicket) {
		SelectCount sc = new SelectCount(sTable, sField, sTicket, null);
		mapField.put(sKey, sc);
	}
	
	public void addSum(String sKey, String sTable, String sField, String sTicket, WhereSql ws) {
		SelectCount sc = new SelectCount(sTable, sField, sTicket, ws);
		mapField.put(sKey, sc);
	}

	public void addInDayWhere(WhereSql ws, String sField, int date) {
		long startTicket = DateAction.daysToHours(date) * 3600;
		long endTicket = startTicket + 24 * 3600;
		ws.largerEqual(sField, startTicket);
		ws.smaller(sField, endTicket);
	}

	public void fillData(DbAction dbObj, long startTicket, long endTicket) {
		for (Entry<String, SelectCount> field: mapField.entrySet()) {
			SelectCount sc = field.getValue();
			WhereSql ws = new WhereSql();
			ws.largerEqual(sc.sTicket, startTicket);
			ws.smaller(sc.sTicket, endTicket);
			try {
				Field f = getClass().getField(field.getKey());
				Class<?> clsType = f.getType();
				if (clsType == int.class || clsType == Integer.class) {
					int sum = dbObj.selectInt(sc.toSql(ws));
					f.setInt(this, sum);
				} else if (clsType == long.class || clsType == Long.class) {
					long sum = dbObj.selectLong(sc.toSql(ws));
					f.setLong(this, sum);
				}
			} catch (Exception e) {
			}
		}
	}

	public void fillData(DbAction dbObj, int startDate, int endDate) {
		long startTicket = DateAction.daysToHours(startDate) * 3600;
		long endTicket = DateAction.daysToHours(endDate) * 3600;
		this.date = startDate;
		fillData(dbObj, startTicket, endTicket);
	}

	public void fillData(DbAction dbObj, int days) {
		fillData(dbObj, days, days + 1);
	}

}
