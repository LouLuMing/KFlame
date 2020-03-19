package com.china.fortune.database.sql;

public class TruncateSql {
	static public String toSql(Class<?> c) {
		return toSql(c.getSimpleName());
	}
	
	static public String toSql(String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("truncate ");
		sb.append(sTable);
		return sb.toString();
	}
}
