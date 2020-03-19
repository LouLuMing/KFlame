package com.china.fortune.database.sqlite;

import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;

public class SqliteAction extends DbAction {
	static {
		if (!DbAction.init(DbAction.sqliteDriver)) {
			Log.logError("Miss Sqlite Driver");
		}
	}
	
	public boolean open(String sFullPath) {
		close();
		String sJdbc = "jdbc:sqlite:" + sFullPath; 
		set(sJdbc, null, null);
		return super.open(sJdbc, null, null);
	}

	public ArrayList<String> selectAllViewsName() {
		String sSelect = "SELECT name FROM sqlite_master WHERE type=\"view\"";
		return this.selectStringOneColumn(sSelect);
	}

	@Override
	public ArrayList<String> selectAllTableName() {
		String sSelect = "SELECT name FROM sqlite_master WHERE type=\"table\"";
		return this.selectStringOneColumn(sSelect);
	}

	@Override
	public String createTableSql(String sTable) {
		return null;
	}

	@Override
	public String getTableComment(String sTable) {
		return null;
	}

	@Override
	public ArrayList<String> selectColumnName(String sTable) {
		return null;
	}

	@Override
	public String showDataSql(String sTable, int iPage, int iPageSize) {
		return null;
	}

	@Override
	public String showDataSql(String sTable) {
		return null;
	}

	@Override
	public ArrayList<ArrayList<String>> selectColumnInfo(String sTable) {
		return null;
	}

}
