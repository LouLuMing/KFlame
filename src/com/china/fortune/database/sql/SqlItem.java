package com.china.fortune.database.sql;

public class SqlItem {
	public String sField;
	public String sValue;
	public keyType iType;

	public SqlItem(String s) {
		sField = s;
		iType = keyType.DOT;
	}

	public SqlItem(String s1, String s2, keyType t) {
		sField = s1;
		sValue = s2;
		iType = t;
	}
}
