package com.china.fortune.database.objectList;

import java.util.HashMap;

import com.china.fortune.database.mySql.MySqlDbAction;

public class ObjectListManager {
	private HashMap<String, ObjectListFromDB> mapObjectListManager = new HashMap<String, ObjectListFromDB>();

	public boolean update(MySqlDbAction dbObj, Class<?> c) {
		ObjectListFromDB om = mapObjectListManager.get(c.getSimpleName());
		if (om != null) {
			om.loadData(dbObj);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update(MySqlDbAction dbObj, String sKey) {
		ObjectListFromDB om = mapObjectListManager.get(sKey);
		if (om != null) {
			om.loadData(dbObj);
			return true;
		} else {
			return false;
		}
	}
	
	public void update(MySqlDbAction dbObj) {
		for (ObjectListFromDB om : mapObjectListManager.values()) {
			om.loadData(dbObj);
		}
	}
	
	public void add(Class<?> c) {
		ObjectListFromDB om = new ObjectListFromDB(c);
		mapObjectListManager.put(c.getSimpleName(), om);
	}
	
	public void add(Class<?> c, String sSql) {
		ObjectListFromDB om = new ObjectListFromDB(c, sSql);
		mapObjectListManager.put(c.getSimpleName(), om);
	}
	
	public ObjectListFromDB get(Class<?> c) {
		return mapObjectListManager.get(c.getSimpleName());
	}
	
	public ObjectListFromDB get(String sKey) {
		return mapObjectListManager.get(sKey);
	}
}
