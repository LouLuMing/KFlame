package com.china.fortune.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class InsertSql {
	protected String sAction = "insert";
	private String sTable;
	private ArrayList<SqlItem> lsObject = new ArrayList<SqlItem>();

	public int size() {
		return lsObject.size();
	}

	public void removeItem(String sKey) {
		for (SqlItem si : lsObject) {
			if (StringAction.compareTo(si.sField, sKey) == 0) {
				lsObject.remove(si);
				break;
			}
		}
	}

	public InsertSql() {
	}

	public InsertSql(String table) {
		sTable = table;
	}

	public InsertSql(Class<?> cls) {
		sTable = cls.getSimpleName();
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

	public void addString(String field, String value) {
		if (value != null) {
			addItem(field, value, keyType.DOT);
		}
	}

	public void addString(int i, String value) {
		if (value != null) {
			addItem(i, value, keyType.DOT);
		}
	}

	public void addInt(String name, int value) {
		addItem(name, String.valueOf(value), keyType.NO_DOT);
	}

	public void addLong(String name, long value) {
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

	public String toSql() {
		StringBuilder sSqlHead = new StringBuilder();
		sSqlHead.append(sAction);
		sSqlHead.append(" into ");
		sSqlHead.append(sTable);
		sSqlHead.append(" (");

		StringBuilder sSqlTail = new StringBuilder();
		sSqlTail.append("values (");

		boolean bNext = false;
		for (SqlItem s2S : lsObject) {
			if (bNext) {
				sSqlHead.append(",");
				sSqlTail.append(",");
			} else {
				bNext = true;
			}
			sSqlHead.append(s2S.sField);
			if (s2S.iType == keyType.DOT) {
				sSqlTail.append('\'');
				sSqlTail.append(s2S.sValue);
				sSqlTail.append('\'');
			} else {
				sSqlTail.append(s2S.sValue);
			}
		}

		sSqlHead.append(") ");
		sSqlHead.append(sSqlTail);
		sSqlHead.append(");");

		return sSqlHead.toString();
	}

	public void addObject(Object o) {
		try {
			Class<?> c = o.getClass();
			sTable = c.getSimpleName();
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					Object value = f.get(o);
					if (value != null) {
						if (f.getType() == String.class) {
							addString(f.getName(), (String) value);
						} else if (f.getType() == int.class || f.getType() == Integer.class) {
							addInt(f.getName(), (Integer) value);
						} else if (f.getType() == long.class || f.getType() == Long.class) {
							addLong(f.getName(), (Long) value);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public static void main(String[] args) {
		InsertSql isa = new InsertSql("table");
		isa.addString("key1", "value1");
		isa.addString("key2", "value2");
		Log.logClass(isa.toSql());
	}
}
