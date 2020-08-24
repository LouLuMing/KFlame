package com.china.fortune.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;

public class SelectSql {
	static public String toSumSql(Class<?> c) {
		return toSumSql(c, c.getSimpleName());
	}

	static public String toSumSql(Class<?> c, String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					String sName = f.getName();
					sb.append("sum(");
					sb.append(sName);
					sb.append(") as ");
					sb.append(sName);
					sb.append(",");
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		sb.setLength(sb.length() - 1);
		sb.append(" from ");
		sb.append(sTable);

		return sb.toString();
	}

	static public String toSql(String sTable, ArrayList<String> lsFields) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		for (String s : lsFields) {
			sb.append(s);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(" from ");
		sb.append(sTable);
		return sb.toString();
	}
	
	static public String toSql(String sTable, String[] lsFields) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		for (String s : lsFields) {
			sb.append(s);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(" from ");
		sb.append(sTable);
		return sb.toString();
	}

	static public String toSql(Class<?> c, String sTable, String[] lsExpectKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					String sKey = f.getName();
					if (StringUtils.findString(lsExpectKey, sKey) < 0) {
						sb.append(f.getName());
						sb.append(',');
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		sb.setLength(sb.length() - 1);
		sb.append(" from ");
		sb.append(sTable);

		return sb.toString();
	}

	static public String toSql(Class<?> c, String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					sb.append(f.getName());
					sb.append(',');
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		sb.setLength(sb.length() - 1);
		sb.append(" from ");
		sb.append(sTable);

		return sb.toString();
	}

	static public String toSqlExpect(Class<?> c, String[] lsExpectKey) {
		return toSql(c, c.getSimpleName(), lsExpectKey);
	}

    static public String toSelectAllSql(String sTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ");
        sb.append(sTable);
        return sb.toString();
    }

	static public String toSelectAllSql(Class<?> c) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(c.getSimpleName());
		return sb.toString();
	}

	static public String toSql(Class<?> c) {
		return toSql(c, c.getSimpleName());
	}

	static public String toSelectCountSql(String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from ");
		sb.append(sTable);
		return sb.toString();
	}

	static public String toSelectCountSql(Class<?> c) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from ");
		sb.append(c.getSimpleName());
		return sb.toString();
	}

	static public String loadMaxId(String field, String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select max(");
		sb.append(field);
		sb.append(") from ");
		sb.append(table);
		sb.append(';');
		return sb.toString();
	}
}
