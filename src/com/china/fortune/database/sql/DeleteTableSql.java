package com.china.fortune.database.sql;

import com.china.fortune.global.Log;

public class DeleteTableSql {
	static public String toSql(Class<?> cls) {
		return "DROP TABLE " + cls.getSimpleName();
	}

	static public String toSql(String sTable) {
		return "DROP TABLE " + sTable;
	}

	public static void main(String[] args) {
		Log.log(DeleteTableSql.toSql("table"));
	}
}
