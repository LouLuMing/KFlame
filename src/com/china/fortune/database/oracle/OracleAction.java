package com.china.fortune.database.oracle;

import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;

public class OracleAction extends DbAction {

	static {
		if (!DbAction.init(DbAction.oracleDriver)) {
			Log.logError("Miss Oracle Driver");
		}
	}

	@Override
	public ArrayList<String> selectAllTableName() {
		return null;
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
	public String showDataSql(String sTable) {
		return null;
	}

	@Override
	public ArrayList<ArrayList<String>> selectColumnInfo(String sTable) {
		return null;
	}

    @Override
	public String showDataSql(String sTable, int iPage, int iPageSize) {
		return null;
	}
}
