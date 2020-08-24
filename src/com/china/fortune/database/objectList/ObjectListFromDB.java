package com.china.fortune.database.objectList;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassDatabase;
import com.china.fortune.reflex.ClassJson;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.string.StringUtils;

public class ObjectListFromDB {
	private Class<?> cClass = null;
	private ArrayList<Object> lsObject = new ArrayList<Object>();
	private String sSelectSql = null;
	
	public ObjectListFromDB(Class<?> c) {
		cClass = c;
	}

	public ObjectListFromDB(Class<?> c, String sSql) {
		cClass = c;
		sSelectSql = sSql;
	}
	
	public void loadData(MySqlDbAction dbObj) {
		if (sSelectSql == null) {
			lsObject = ClassDatabase.selectObjects(dbObj, cClass);
		} else {
			lsObject = ClassDatabase.selectObjects(dbObj, cClass, sSelectSql);
		}
	}

	public JSONArray toJSONArray() {
		JSONArray jar = new JSONArray();
		for (Object obj : lsObject) {
			JSONObject item = ClassJson.toJSONObject(obj);
			jar.put(item);
		}
		return jar;
	}

	public ArrayList<Object> getObjectList() {
		return lsObject;
	}

	public void showObjectList() {
		for (Object obj : lsObject) {
			ClassUtils.showAllFields(obj);
		}
	}

	public Object get(String sField, String sValue) {
		Object rs = null;
		try {
			Field f = cClass.getField(sField);
			if (f != null) {
				f.setAccessible(true);
				for (Object obj : lsObject) {
					if (StringUtils.compareTo((String)f.get(obj), sValue) == 0) {
						rs = obj;
						break;
					}
				}
			}
		} catch (Exception e) {
		}
		return rs;
	}
	
	public Object get(String sField, int id) {
		Object rs = null;
		try {
			Field f = cClass.getField(sField);
			if (f != null) {
				f.setAccessible(true);
				for (Object obj : lsObject) {
					if (f.getInt(obj) == id) {
						rs = obj;
						break;
					}
				}
			}
		} catch (Exception e) {
		}
		return rs;
	}
}
