package com.china.fortune.database.sql;


public class WhereItem {
	public String sField;
	public String sCompare;
	public String sValue;
	public keyType iType;
	
	public WhereItem(String s1, String s2, String s3, keyType kt) {
		sField = s1;
		sCompare = s2;
		sValue = s3;
		iType = kt;
	}
	
	public WhereItem(String s1, String s2, String s3) {
		sField = s1;
		sCompare = s2;
		sValue = s3;
		iType = keyType.DOT;
	}
	
	public WhereItem(String s1, String s2, int i3) {
		sField = s1;
		sCompare = s2;
		sValue = String.valueOf(i3);
		iType = keyType.NO_DOT;
	}
	
	public WhereItem(String s1, String s2, long i3) {
		sField = s1;
		sCompare = s2;
		sValue = String.valueOf(i3);
		iType = keyType.NO_DOT;
	}
}
