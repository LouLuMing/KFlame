package com.china.fortune.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.database.sql.InsertSql;
import com.china.fortune.database.sql.SelectSql;
import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.reflex.ClassDatabase;

public abstract class Table {
	protected String sTable;
	protected ArrayList<String> lsFields = new ArrayList<String>();

	abstract protected boolean init(DbAction dbObj, String sTable);

	public boolean init(DbAction dbObj, Class<?> cls) {
		if (init(dbObj, cls.getSimpleName())) {
			syncFields(cls);
			return true;
		} else {
			Log.logClassError(cls.getSimpleName());
			return false;
		}
	}

	protected void syncFields(Class<?> c) {
		ArrayList<String> lsNewFields = new ArrayList<String>();
		for (String sField : lsFields) {
			try {
				Field f = c.getField(sField);
				if (f != null) {
					lsNewFields.add(sField);
				}
			} catch (NoSuchFieldException e) {
			} catch (Exception e) {
				Log.logClassError(c.getSimpleName() + " " + e.getMessage());
			}
		}
		lsFields.clear();
		lsFields = lsNewFields;
	}

	public int select(DbAction dbObj, Object o, String sWhere) {
		String sSelect = SelectSql.toSelectAllSql(o.getClass()) + sWhere;
		return ClassDatabase.selectObject(dbObj, o, sSelect);
	}

	public ArrayList<Object> select(DbAction dbObj, Class<?> cls, String sWhere) {
		String sSelect = SelectSql.toSelectAllSql(cls) + sWhere;
		return ClassDatabase.selectObjects(dbObj, cls, sSelect);
	}
	
	public int update(DbAction dbObj, Object o, String sUpdate, String sWhere) {
		return ClassDatabase.update(dbObj, o, sUpdate, sWhere);
	}
	
	public int update(DbAction dbObj, Object o, String[] lsUpdate, String sWhere) {
		return ClassDatabase.update(dbObj, o, lsUpdate, sWhere);
	}
	
	public InsertSql toInsertSql(Object o) {
		InsertSql isa = new InsertSql(sTable);
		Class<?> c = o.getClass();
		for (String sField : lsFields) {
			try {
				Field f = c.getField(sField);
				if (f != null) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						Object value = f.get(o);
						if (f.getType() == String.class) {
							isa.addString(f.getName(), (String) value);
						} else if (f.getType() == int.class || f.getType() == Integer.class) {
							isa.addInt(f.getName(), (Integer) value);
						} else if (f.getType() == long.class || f.getType() == Long.class) {
							isa.addLong(f.getName(), (Long) value);
						}
					}
				}
			} catch (NoSuchFieldException e) {
			} catch (Exception e) {
				Log.logClassError(c.getSimpleName() + " " + e.getMessage());
			}
		}
		return isa;
	}
	
	public int insert(DbAction dbObj, Object o) {
		InsertSql isa = toInsertSql(o);
		if (isa.size() > 0) {
			return dbObj.execute(isa.toSql());
		}
		return 0;
	}
}
