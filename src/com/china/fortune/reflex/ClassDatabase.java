package com.china.fortune.reflex;

import com.china.fortune.database.Table;
import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlTable;
import com.china.fortune.database.mySql.ReplaceSql;
import com.china.fortune.database.sql.*;
import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.struct.FastHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassDatabase {
	static private FastHashMap<Table> mapInsert = new FastHashMap<Table>();

	static public void addTable(DbAction dbObj, Class<?> cls) {
		String sTag = cls.getSimpleName();
		Table mst = mapInsert.get(sTag);
		if (mst == null) {
			if (dbObj instanceof MySqlDbAction) {
				mst = new MySqlTable();
				if (mst.init(dbObj, cls)) {
					mapInsert.put(cls, mst);
				}
			}
		}
	}

	static public int initHitCache() {
		return mapInsert.initHitCache();
	}

	static public int deleteTable(DbAction dbObj, Class<?> c) {
		return dbObj.execute(DeleteTableSql.toSql(c));
	}

	static public int createTable(DbAction dbObj, Class<?> c) {
		CreateTableSql ctsq = new CreateTableSql();
		ctsq.addClass(c);
		return dbObj.execute(ctsq.toSql());
	}

	static public int insert(DbAction dbObj, Object o) {
		Table tb = mapInsert.get(o.getClass());
		if (tb != null) {
			return tb.insert(dbObj, o);
		} else {
			InsertSql isa = new InsertSql();
			isa.addObject(o);
			return dbObj.execute(isa.toSql());
		}
	}

	static public int insert(DbAction dbObj, Object o, String sExcept) {
		InsertSql isa = null;
		Table tb = mapInsert.get(o.getClass());
		if (tb != null) {
			isa = tb.toInsertSql(o);
		} else {
			isa = new InsertSql();
			isa.addObject(o);
		}
		isa.removeItem(sExcept);
		return dbObj.execute(isa.toSql());
	}

	static public int replace(DbAction dbObj, Object o) {
		ReplaceSql isa = new ReplaceSql();
		isa.addObject(o);
		return dbObj.execute(isa.toSql());
	}

	static public int delete(DbAction dbObj, Object o, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		return dbObj.execute(DeleteSql.toSql(o.getClass()) + ws.toSql());
	}

	static public int delete(DbAction dbObj, Object o, String[] lsWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		return dbObj.execute(DeleteSql.toSql(o.getClass()) + ws.toSql());
	}

	static public int delete(DbAction dbObj, Object o) {
		WhereSql ws = new WhereSql();
		ws.add(o);
		return dbObj.execute(DeleteSql.toSql(o.getClass()) + ws.toSql());
	}

	static public int deleteAll(DbAction dbObj, Class<?> cls) {
		return dbObj.execute(DeleteSql.toSql(cls));
	}

	static public int exist(DbAction dbObj, Object o) {
		WhereSql ws = new WhereSql();
		ws.add(o);
		return dbObj.selectInt("select count(1) from " + o.getClass().getSimpleName() + ws.toSql());
	}

	static public int exist(DbAction dbObj, Class<?> cls, WhereSql ws) {
		return dbObj.selectInt("select count(1) from " + cls.getSimpleName() + ws.toSql());
	}

	static public int exist(DbAction dbObj, Object o, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		return dbObj.selectInt("select count(1) from " + o.getClass().getSimpleName() + ws.toSql());
	}

	static public int exist(DbAction dbObj, Object o, String[] lsWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		return dbObj.selectInt("select count(1) from " + o.getClass().getSimpleName() + ws.toSql());
	}

	static private void resultSetToObject(ResultSet rs, Class<?> c, Object obj) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int iColumn = rsmd.getColumnCount();
			for (int i = 1; i <= iColumn; i++) {
				Field f = null;
				try {
					f = c.getField(rsmd.getColumnName(i));
				} catch (Exception e) {
				}
				if (f != null) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						if (f.getType() == String.class) {
							f.set(obj, rs.getString(i));
						} else if (f.getType() == int.class || f.getType() == Integer.class) {
							f.setInt(obj, rs.getInt(i));
						} else if (f.getType() == long.class || f.getType() == Long.class) {
							f.setLong(obj, rs.getLong(i));
						}
					}
				}
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	static public int selectObject(DbAction dbObj, final Object obj, String sSql) {
		int iResult = dbObj.select(sSql, new DbAction.onSelectListener() {
			@Override
			public int onSelect(ResultSet rs) {
				int iRecord = 0;
				try {
					Class<?> c = obj.getClass();
					while (rs.next()) {
						resultSetToObject(rs, c, obj);
						iRecord++;
						break;
					}
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
				return iRecord;
			}
		});
		return iResult;
	}

    static public int selectWhere(DbAction dbObj, Object o, WhereSql ws) {
        Class<?> cls = o.getClass();
        String sSelect = SelectSql.toSelectAllSql(cls);
        return selectObject(dbObj, o, sSelect + ws.toSql());
    }

	static public int selectWhere(DbAction dbObj, Object o, String sWhere) {
		Class<?> cls = o.getClass();
		String sSelect = SelectSql.toSelectAllSql(cls);
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		return selectObject(dbObj, o, sSelect + ws.toSql());
	}

	static public int selectWhere(DbAction dbObj, Object o, String[] lsWhere) {
		String sSelect = SelectSql.toSelectAllSql(o.getClass());
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		return selectObject(dbObj, o, sSelect + ws.toSql());
	}

	static public ArrayList<Object> selectObjects(DbAction dbObj, final Class<?> c, String sSelect) {
		final ArrayList<Object> lsObj = new ArrayList<Object>();
		dbObj.select(sSelect, new DbAction.onSelectListener() {
			@Override
			public int onSelect(ResultSet rs) {
				int iRecord = 0;
				try {
					while (rs.next()) {
						Object obj = c.newInstance();
						resultSetToObject(rs, c, obj);
						lsObj.add(obj);
						iRecord++;
					}
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
				return iRecord;
			};
		});
		return lsObj;
	}

	static public ArrayList<Object> selectObjects(DbAction dbObj, Class<?> c) {
		String sSelect = SelectSql.toSelectAllSql(c);
		return selectObjects(dbObj, c, sSelect);
	}

	static public ArrayList<Object> selectObjsWhere(DbAction dbObj, Object o, String sWhere) {
		Class<?> cls = o.getClass();
		String sSelect = SelectSql.toSelectAllSql(cls);
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		return selectObjects(dbObj, cls, sSelect + ws.toSql());
	}

	static public ArrayList<Object> selectObjsWhere(DbAction dbObj, Object o, String[] lsWhere) {
		Class<?> cls = o.getClass();
		String sSelect = SelectSql.toSelectAllSql(cls);
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		return selectObjects(dbObj, cls, sSelect + ws.toSql());
	}
	
	static public int update(DbAction dbObj, Object o, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObjectExcept(o, sWhere);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object o, String[] lsWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObjectExcept(o, lsWhere);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object o, String sUpdate, String[] lsWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObject(o, sUpdate);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}
	
	static public int update(DbAction dbObj, Object o, String sUpdate, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObject(o, sUpdate);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object o, String[] lsUpdate, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObject(o, lsUpdate);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object o, ArrayList<String> lsUpdate, String sWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, sWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObject(o, lsUpdate);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

	static public int update(DbAction dbObj, Object o, String[] lsUpdate, String[] lsWhere) {
		WhereSql ws = new WhereSql();
		ws.add(o, lsWhere);
		UpdateSql us = new UpdateSql(o.getClass().getSimpleName());
		us.addObject(o, lsUpdate);
		if (us.getFields() > 0) {
			return dbObj.execute(us.toSql() + ws.toSql());
		} else {
			return 0;
		}
	}

}
