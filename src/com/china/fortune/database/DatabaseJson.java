package com.china.fortune.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import com.china.fortune.global.Log;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.database.DbAction;

public class DatabaseJson {

	static public JSONObject toJSONObject(DbAction dbObj, String sSelect) {
		final JSONObject json = new JSONObject();
		toJSONObject(json, dbObj, sSelect);
		return json;
	}

	static public boolean toJSONObject(final JSONObject json, DbAction dbObj, String sSelect) {
		return dbObj.select(sSelect, (ResultSet rs) ->{
			int iRecord = 0;
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int iColumn = rsmd.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= iColumn; i++) {
						json.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					iRecord++;
					break;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			return iRecord;
		}) > 0;
	}

	public static boolean toJSONArray(DbAction dbObj, String sSelect, final JSONArray jarr) {
		return dbObj.select(sSelect, (ResultSet rs) -> {
			int iRecord = 0;
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int iColumn = rsmd.getColumnCount();
				while (rs.next()) {
					JSONObject json = new JSONObject();
					for (int i = 1; i <= iColumn; i++) {
						json.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					jarr.put(json);
					iRecord++;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			return iRecord;
		}) >= 0;
	}

	public static JSONArray toJSONArray(DbAction dbObj, String sSelect) {
		final JSONArray jarr = new JSONArray();
		toJSONArray(dbObj, sSelect, jarr);
		return jarr;
	}

	public static boolean toHashMapJSONObject(DbAction dbObj, String sSelect,
			final HashMap<Integer, JSONObject> hashmap) {
		return dbObj.select(sSelect, new DbAction.onSelectListener() {
			@Override
			public int onSelect(ResultSet rs) {
				int iRecord = 0;
				try {
					ResultSetMetaData rsmd = rs.getMetaData();
					int iColumn = rsmd.getColumnCount();
					while (rs.next()) {
						JSONObject json = new JSONObject();
						for (int i = 1; i <= iColumn; i++) {
							json.put(rsmd.getColumnName(i), rs.getObject(i));
						}
						hashmap.put(rs.getInt(1), json);
						iRecord++;
					}
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
				return iRecord;
			}
		}) >= 0;
	}

}
