package com.china.fortune.restfulHttpServer.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.os.database.DbAction;

public class TotalSummer {

	private HashMap<String, SelectCount> mapField = new HashMap<String, SelectCount>();
	
	public void addSum(String sKey, String sTable, String sField) {
		SelectCount sc = new SelectCount(sTable, sField, null, null);
		mapField.put(sKey, sc);
	}

	public void fillData(DbAction dbObj) {
		for (Entry<String, SelectCount> field: mapField.entrySet()) {
			SelectCount sc = field.getValue();
			int sum = dbObj.selectInt(sc.toSql());
			try {
				Field f = getClass().getField(field.getKey());
				f.setInt(this, sum);
			} catch (Exception e) {
			}
		}
	}
}
