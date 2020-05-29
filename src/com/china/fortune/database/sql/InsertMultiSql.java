package com.china.fortune.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.global.Log;

public class InsertMultiSql {
	private StringBuilder sSqlHead = new StringBuilder();
	private int iValues = 0;

	public void clear() {
		iValues = 0;
		sSqlHead.setLength(0);
	}

	public void addFields(Class<?> c) {
		addFields(c.getSimpleName(), c);
	}

	public void addFields(String sTable, Class<?> c) {
		try {
			sSqlHead.append("insert into ");
			sSqlHead.append(sTable);
			sSqlHead.append(" (");
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					sSqlHead.append(f.getName());
					sSqlHead.append(',');
				}
			}
			sSqlHead.setLength(sSqlHead.length() - 1);
			sSqlHead.append(") values ");
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addFields(String sTable, ArrayList<String> lsFields) {
		try {
			sSqlHead.append("insert into ");
			sSqlHead.append(sTable);
			sSqlHead.append(" (");
			for (String sName : lsFields) {
				boolean isKey = false;
				if (sName.compareToIgnoreCase("key") == 0
				|| sName.compareToIgnoreCase("value") == 0) {
					isKey = true;
				}
				if (isKey) {
					sSqlHead.append('`');
				}
				sSqlHead.append(sName);
				if (isKey) {
					sSqlHead.append('`');
				}
				sSqlHead.append(',');
			}
			sSqlHead.setLength(sSqlHead.length() - 1);
			sSqlHead.append(") values ");
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addValue(ArrayList<String> lsValues) {
		try {
			sSqlHead.append("(");
			for (String sValue : lsValues) {
				if (sValue != null) {
					sSqlHead.append('\'');
					sSqlHead.append(sValue);
					sSqlHead.append("\',");
				} else {
					sSqlHead.append("null");
					sSqlHead.append(",");
				}
			}
			sSqlHead.setLength(sSqlHead.length() - 1);
			sSqlHead.append("),");
			iValues++;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public void addValue(Object o) {
		try {
			Class<?> c = o.getClass();
			Field[] lsFields = c.getFields();
			sSqlHead.append("(");
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					Object value = f.get(o);
					if (f.getType() == String.class) {
						sSqlHead.append('\'');
						sSqlHead.append((String) value);
						sSqlHead.append('\'');
					} else if (f.getType() == int.class || f.getType() == Integer.class) {
						sSqlHead.append((Integer) value);
					} else if (f.getType() == long.class || f.getType() == Long.class) {
						sSqlHead.append((Long) value);
					}
					sSqlHead.append(',');
				}
			}
			sSqlHead.setLength(sSqlHead.length() - 1);
			sSqlHead.append("),");
			iValues++;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public int getValues() {
		return iValues;
	}

	public String toSql() {
		sSqlHead.setCharAt(sSqlHead.length() - 1, ';');
		return sSqlHead.toString();
	}

}
