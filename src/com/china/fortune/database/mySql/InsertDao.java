package com.china.fortune.database.mySql;

import java.util.HashMap;

import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;

public class InsertDao {
	static private HashMap<String, MySqlTable> mapInsert = new HashMap<String, MySqlTable>();

	static public MySqlTable addTable(DbAction dbObj, Class<?> cls) {
		MySqlTable mst = new MySqlTable();
		if (mst.init(dbObj, cls)) {
			mapInsert.put(cls.getSimpleName(), mst);
			return mst;
		} else {
			return null;
		}
	}

	static public int insert(DbAction dbObj, Object obj) {
		MySqlTable mst = mapInsert.get(obj.getClass().getSimpleName());
		if (mst == null) {
			mst = addTable(dbObj, obj.getClass());
		}
		if (mst != null) {
			return mst.insert(dbObj, obj);
		} else {
			Log.logError("InsertDao:insert " + obj.getClass().getSimpleName());
			return 0;
		}
	}

	static public int select(DbAction dbObj, Object obj, String sWhere) {
		MySqlTable mst = mapInsert.get(obj.getClass().getSimpleName());
		if (mst == null) {
			mst = addTable(dbObj, obj.getClass());
		}
		if (mst != null) {
			WhereSql ws = new WhereSql();
			ws.add(obj, sWhere);
			return mst.select(dbObj, obj, ws.toSql());
		} else {
			Log.logError("InsertDao:select " + obj.getClass().getSimpleName());
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object obj, String sUpdate, String sWhere) {
		MySqlTable mst = mapInsert.get(obj.getClass().getSimpleName());
		if (mst == null) {
			mst = addTable(dbObj, obj.getClass());
		}
		if (mst != null) {
			return mst.update(dbObj, obj, sUpdate, sWhere);
		} else {
			Log.logError("InsertDao:select " + obj.getClass().getSimpleName());
			return 0;
		}
	}
	
	static public int update(DbAction dbObj, Object obj, String[] lsUpdate, String sWhere) {
		MySqlTable mst = mapInsert.get(obj.getClass().getSimpleName());
		if (mst == null) {
			mst = addTable(dbObj, obj.getClass());
		}
		if (mst != null) {
			return mst.update(dbObj, obj, lsUpdate, sWhere);
		} else {
			Log.logError("InsertDao:select " + obj.getClass().getSimpleName());
			return 0;
		}
	}
}
