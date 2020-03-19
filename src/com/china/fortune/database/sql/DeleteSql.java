package com.china.fortune.database.sql;

public class DeleteSql {
	static public String toSql(Class<?> c) {
		return toSql(c.getSimpleName());
	}
	
	static public String toSql(String sTable) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ");
		sb.append(sTable);
		return sb.toString();
	}
}
