package com.china.fortune.database.sql;

public class DropTableSql {
	static public String toSql(Class<?> c) {
		return toSql(c.getSimpleName());
	}
	
	static public String toSql(String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("drop table ");
		sb.append(sTable);
		return sb.toString();
	}
}
