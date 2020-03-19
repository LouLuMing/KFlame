package com.china.fortune.database.sql;

public class OrderByItem {
	public String sField;
	public boolean bDesc;
	
	public OrderByItem(String s, boolean b) {
		sField = s;
		bDesc = b;
	}
}
