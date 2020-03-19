package com.china.fortune.database.sql;

public class GroupBy {
	static public String toSql(String sKey) {
		return " group by " + sKey;
	}
}
