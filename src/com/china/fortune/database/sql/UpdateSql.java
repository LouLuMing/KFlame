package com.china.fortune.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.string.StringUtils;

public class UpdateSql {
	private String sTable;
	private ArrayList<SqlItem> lsObject = new ArrayList<SqlItem>();

	public UpdateSql() {
	}

	public UpdateSql(Class<?> c) {
		sTable = c.getSimpleName();
	}

	public UpdateSql(String table) {
		sTable = table;
	}

	public int getFields() {
		return lsObject.size();
	}

	public void setTable(String table) {
		sTable = table;
	}

	public void clear() {
		lsObject.clear();
	}

	public void addField(String field) {
		SqlItem s2S = getField(field);
		if (s2S == null) {
			lsObject.add(new SqlItem(field));
		}
	}

	private SqlItem getField(String sfield) {
		for (SqlItem s2S : lsObject) {
			if (sfield.compareTo(s2S.sField) == 0) {
				return s2S;
			}
		}
		return null;
	}

	private void addItem(String field, String value, keyType t) {
		SqlItem s2S = getField(field);
		if (s2S == null) {
			s2S = new SqlItem(field, value, t);
			lsObject.add(s2S);
		} else {
			s2S.sValue = value;
			s2S.iType = t;
		}
	}

	private void addItem(int i, String value, keyType t) {
		if (i >= 0 && i < lsObject.size()) {
			SqlItem s2S = lsObject.get(i);
			s2S.sValue = value;
			s2S.iType = t;
		}
	}

	public void setNull(String field) {
		addItem(field, "null", keyType.NO_DOT);
	}

	public void addString(String field, String value) {
		if (value != null) {
			addItem(field, value, keyType.DOT);
		}
	}

	public void increase(String field, long value) {
		if (value > 0) {
			addItem(field, field + "+" + value, keyType.NO_DOT);
		} else if (value < 0) {
			addItem(field, field + value, keyType.NO_DOT);
		}
	}
	
	public void increase(String field, int value) {
		if (value > 0) {
			addItem(field, field + "+" + value, keyType.NO_DOT);
		} else if (value < 0) {
			addItem(field, field + value, keyType.NO_DOT);
		}
	}

	public void addString(int i, String value) {
		if (value != null) {
			addItem(i, value, keyType.DOT);
		}
	}

	public void addLong(String name, long value) {
		addItem(name, String.valueOf(value), keyType.NO_DOT);
	}

	public void addInt(String name, int value) {
		addItem(name, String.valueOf(value), keyType.NO_DOT);
	}

	public void addInt(int i, int value) {
		addItem(i, String.valueOf(value), keyType.NO_DOT);
	}

	public void addKey(String field, String value) {
		if (value != null) {
			addItem(field, value, keyType.NO_DOT);
		}
	}

	public void addKey(int i, String value) {
		if (value != null) {
			addItem(i, value, keyType.NO_DOT);
		}
	}

	public int size() {
		return lsObject.size();
	}

	public String toSql() {
		StringBuilder sSql = new StringBuilder();
		sSql.append("update ");
		sSql.append(sTable);
		sSql.append(" set ");

		boolean bFirst = true;
		for (SqlItem s2S : lsObject) {
			if (bFirst) {
				bFirst = false;
			} else {
				sSql.append(",");
			}
			sSql.append(s2S.sField);
			sSql.append('=');
			if (s2S.iType == keyType.DOT) {
				sSql.append("'");
				sSql.append(s2S.sValue);
				sSql.append("'");
			} else {
				sSql.append(s2S.sValue);
			}
		}
		return sSql.toString();
	}
	
	private void increaseField(Object o, Field f) {
		if (f != null) {
			try {
				if (f.getType() == int.class || f.getType() == Integer.class) {
					addInt(f.getName(), f.getInt(o));
				} else if (f.getType() == long.class || f.getType() == Long.class) {
					addLong(f.getName(), f.getLong(o));
				}
			} catch (Exception e) {
			}
		}
	}
	

	private void addField(Object o, Field f) {
		if (f != null) {
			try {
				f.setAccessible(true);
				if (f.getType() == String.class) {
					addString(f.getName(), (String) f.get(o));
				} else if (f.getType() == int.class || f.getType() == Integer.class) {
					addInt(f.getName(), f.getInt(o));
				} else if (f.getType() == long.class || f.getType() == Long.class) {
					addLong(f.getName(), f.getLong(o));
				}
			} catch (Exception e) {
			}
		}
	}

	public void addObject(Object o, String sField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			addField(o, c.getField(sField));
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addObject(Object o, ArrayList<String> lsField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			for (String sField : lsField) {
				addField(o, c.getField(sField));
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addObject(Object o, String[] lsField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			for (String sField : lsField) {
				addField(o, c.getField(sField));
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addObjectExcept(Object o, String sField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					if (StringUtils.compareTo(sField, f.getName()) != 0) {
						addField(o, f);
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addObjectExcept(Object o, String[] lsField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					if (StringUtils.findString(lsField, f.getName()) < 0) {
						addField(o, f);
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addObject(Object o) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					addField(o, f);
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void increaseObject(Object o, String[] lsField) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			for (String sField : lsField) {
				increaseField(o, c.getField(sField));
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
	
	public void increaseObject(Object o) {
		try {
			Class<?> c = o.getClass();
			if (sTable == null) {
				sTable = c.getSimpleName();
			}
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					increaseField(o, f);
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
	
	public static String toSql(Object o, String sSet, String sWhere) {
		UpdateSql usa = new UpdateSql();
		usa.addObject(o, sSet);
		WhereSql wsa = new WhereSql();
		wsa.add(o, sWhere);
		return usa.toSql() + wsa.toSql();
	}

	public static int updateField(DbAction dbObj, Object o, String sSet, String sWhere) {
		UpdateSql usa = new UpdateSql();
		usa.addObject(o, sSet);
		WhereSql wsa = new WhereSql();
		wsa.add(o, sWhere);
		return dbObj.execute(usa.toSql() + wsa.toSql());
	}

	public static int increaseField(DbAction dbObj, Object o, String sSet, int iCount, String sWhere) {
		UpdateSql usa = new UpdateSql(o.getClass().getSimpleName());
		usa.increase(sSet, iCount);
		WhereSql wsa = new WhereSql();
		wsa.add(o, sWhere);
		return dbObj.execute(usa.toSql() + wsa.toSql());
	}

	public static void main(String[] args) {
		UpdateSql usa = new UpdateSql("table");
		usa.addString("key1", "value1");
		usa.addString("key2", "value2");
		usa.addString("key3", null);
		WhereSql wsa = new WhereSql();
		wsa.add("key1", "=", "value1");
		wsa.add("key2", "=", "value2");
		
		Log.logClass(usa.toSql() + wsa.toSql());
	}
}
