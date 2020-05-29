package com.china.fortune.database.mySql;

import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;

public class SyncDatabase {
	private String sName1;
	private String sName2;
	private DbAction dbObj1;
	private DbAction dbObj2;

	public SyncDatabase(String s1, DbAction to, String s2, DbAction from) {
		sName1 = s1;
		sName2 = s2;
		dbObj1 = to;
		dbObj2 = from;
	}

	private void showMiss(String sTable1, ArrayList<String> lsTable1, String sTable2, ArrayList<String> lsTable2) {
		for (String table : lsTable1) {
			if (!lsTable2.contains(table)) {
				Log.log(sTable2 + " " + table + " miss");
			}
		}

		for (String table : lsTable2) {
			if (!lsTable1.contains(table)) {
				Log.log(sTable1 + " " + table + " miss");
			}
		}
	}

	public void showMissTables() {
		ArrayList<String> lsTable1 = dbObj1.selectAllTableName();
		ArrayList<String> lsTable2 = dbObj2.selectAllTableName();

		showMiss(sName1, lsTable1, sName2, lsTable2);

		Log.logClass("finish");
	}

	public void showMissTableAndColnum() {
		ArrayList<String> lsTable1 = dbObj1.selectAllTableName();
		ArrayList<String> lsTable2 = dbObj2.selectAllTableName();

		showMiss(sName1, lsTable1, sName2, lsTable2);

		for (String table : lsTable1) {
			if (lsTable2.contains(table)) {
				ArrayList<String> lsColName1 = dbObj1.selectColumnName(table);
				ArrayList<String> lsColName2 = dbObj2.selectColumnName(table);
				if (lsColName1.size() != lsColName2.size()) {
					showMiss(sName1 + ":" + table, lsColName1, sName2 + ":" + table, lsColName2);
				}
				long iCount1 = dbObj1.countData(table);
				long iCount2 = dbObj2.countData(table);
				if (iCount1 != iCount2) {
					Log.log(sName1 + " " + table + " " + iCount1);
					Log.log(sName2 + " " + table + " " + iCount2);
				}
			}
		}

		Log.logClass("finish");
	}

	public void syncTable(String table) {
		String sSql = dbObj1.createTableSql(table);
		dbObj2.execute(sSql);
		String sDataSql = dbObj1.showDataSql(table);
		dbObj2.execute(sDataSql);
		Log.log("sync " + sName2 + ":" + table);

		Log.logClass("finish");
	}

	public void syncTables() {
		ArrayList<String> lsTable1 = dbObj1.selectAllTableName();
		ArrayList<String> lsTable2 = dbObj2.selectAllTableName();

		for (String table : lsTable1) {
			if (!lsTable2.contains(table)) {
				String sSql = dbObj1.createTableSql(table);
				dbObj2.execute(sSql);
				String sDataSql = dbObj1.showDataSql(table);
				dbObj2.execute(sDataSql);
				Log.log("sync " + sName2 + ":" + table);
			}
		}

		for (String table : lsTable2) {
			if (!lsTable1.contains(table)) {
				String sSql = dbObj2.createTableSql(table);
				dbObj1.execute(sSql);
				String sDataSql = dbObj2.showDataSql(table);
				dbObj1.execute(sDataSql);
				Log.log("sync " + sName1 + ":" + table);
			}
		}

		Log.logClass("finish");
	}

	public void copyFrom1To2() {
		ArrayList<String> lsTable1 = dbObj1.selectAllTableName();
		ArrayList<String> lsTable2 = dbObj2.selectAllTableName();

		for (String table : lsTable1) {
			if (!lsTable2.contains(table)) {
				String sSql = dbObj1.createTableSql(table);
				dbObj2.execute(sSql);
				String sDataSql = dbObj1.showDataSql(table);
				dbObj2.execute(sDataSql);
				Log.log("copyFrom1To2 " + sName1 + ":" + sName2 + " " + table);
			}
		}
	}
}
