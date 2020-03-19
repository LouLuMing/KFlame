package com.china.fortune.database.mySql;

import com.china.fortune.database.sql.InsertSql;

public class ReplaceSql extends InsertSql {
	public ReplaceSql() {
		super();
		sAction = "replace";
	}
	
	public ReplaceSql(String table) {
		super(table);
		sAction = "replace";
	}
}
