package com.china.fortune.restfulHttpServer.entity;

import com.china.fortune.database.sql.WhereSql;

public class SelectCount {
	public String sSelect;
	public String sTicket;
	public WhereSql ws = new WhereSql();
	
	public SelectCount(String s, String ticket, WhereSql w) {
		sSelect = "select count(1) from " + s;
		sTicket = ticket;
		ws.append(w);
	}
	
	public SelectCount(String s, String field, String ticket, WhereSql w) {
		sSelect = "select sum(" + field + ") from " + s;
		sTicket = ticket;
		ws.append(w);
	}
	
	public String toSql() {
		StringBuilder sb = new StringBuilder();
		sb.append(sSelect);
		if (ws.size() > 0) {
			sb.append(ws.toSql());
		}
		return sb.toString();
	}
	
	public String toSql(WhereSql w) {
		StringBuilder sb = new StringBuilder();
		sb.append(sSelect);
		if (ws.size() > 0) {
			w.append(ws);
		}
		sb.append(w.toSql());
		return sb.toString();
	}
}
