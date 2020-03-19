package com.china.fortune.database.sql;

import java.util.ArrayList;

public class OrderBy {
	private ArrayList<OrderByItem> lsOrderBy = new ArrayList<OrderByItem>();

	public void add(String sKey, boolean bDesc) {
		lsOrderBy.add(new OrderByItem(sKey, bDesc));
	}

	public String toSql() {
		StringBuilder sb = new StringBuilder();
		sb.append(" order by ");
		for (OrderByItem item : lsOrderBy) {
			sb.append(item.sField);
			if (item.bDesc) {
				sb.append(" desc");
			}
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public int size() {
		return lsOrderBy.size();
	}
	
	public static String toSql(String sKey, boolean bDesc) {
		StringBuilder sb = new StringBuilder();
		sb.append(" order by ");
		sb.append(sKey);
		if (bDesc) {
			sb.append(" desc");
		}
		return sb.toString();
	}
}
