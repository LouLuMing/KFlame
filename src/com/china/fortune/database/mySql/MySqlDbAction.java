package com.china.fortune.database.mySql;

import com.china.fortune.database.sql.DeleteSql;
import com.china.fortune.database.sql.InsertMultiSql;
import com.china.fortune.database.sql.TruncateSql;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.os.database.DbAction;

import java.util.ArrayList;

public class MySqlDbAction extends DbAction {
	static {
		if (!DbAction.init(DbAction.mysqlDriver)) {
			Log.logError("Miss MySql Driver");
		}
	}
	
	static final private String sMySqlJdbcHead = "jdbc:mysql://";
	static final private String sMySqlJdbcParam = "?autoReconnect=true&useUnicode=true&useSSL=false&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=UTC";

	public String showSelectDatabase() {
		String sSelectDBName = "select database();";
		return selectString(sSelectDBName);
	}

	static private String ipToJdbc(String ip, String sDB) {
		StringBuilder sb = new StringBuilder();
		sb.append(sMySqlJdbcHead);
		sb.append(ip);
		if (sDB != null) {
			sb.append('/');
			sb.append(sDB);
		}
		sb.append(sMySqlJdbcParam);
		return sb.toString();
	}

	public void set(String sIP, String sDB, String sUser, String sPasswd) {
		super.set(ipToJdbc(sIP, sDB), sUser, sPasswd);
	}

	public boolean open(String sIP, String sDB, String sUser, String sPasswd) {
		return super.open(ipToJdbc(sIP, sDB), sUser, sPasswd);
	}

	public MySqlDbAction() {
	}

	public String getTableComment(String sTable) {
		String sDBName = showSelectDatabase();
		String sSelect = "select table_comment from information_schema.tables where table_schema='" + sDBName
				+ "' and table_type='base table' and table_name='" + sTable + "';";
		return this.selectString(sSelect);
	}

	public ArrayList<String> selectAllTableName() {
		String sDBName = showSelectDatabase();
		String sSelect = "select table_name from information_schema.tables where table_schema='" + sDBName
				+ "' and table_type='base table';";
		return this.selectStringOneColumn(sSelect);
	}

	public String createTableSql(String sTable) {
		String sSelect = "show create table " + sTable + ";";
		ArrayList<String> lsData = selectStringOneRow(sSelect);
		if (lsData != null && lsData.size() > 1) {
			return lsData.get(1);
		}
		return null;
	}

	public ArrayList<String> selectColumnName(String sTable) {
		String sSelect = "show columns from " + sTable + ";";
		// String sSelectSql = "select COLUMN_NAME from information_schema.columns
		// where table_name='" + sTable + "';";
		ArrayList<ArrayList<String>> lsMatrix = selectStringMatrix(sSelect);
		ArrayList<String> lsColumns = new ArrayList<String>();
		for (ArrayList<String> item : lsMatrix) {
			if (item.size() > 0) {
				lsColumns.add(item.get(0));
			}
		}
		return lsColumns;
	}

	public ArrayList<ArrayList<String>> selectColumnInfo(String sTable) {
		String sSelect = "select distinct COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS where table_name='" + sTable + "';";
		return this.selectStringMatrix(sSelect);
	}

	public String showTableColumnToString(String sTable) {
		return selectColumnName(sTable).toString();
	}

	public String showAllCreateTableSql() {
		StringBuilder sb = new StringBuilder();
		sb.append("set FOREIGN_KEY_CHECKS=0;\n");
		ArrayList<String> lsTables = selectAllTableName();
		for (String sTable : lsTables) {
			sb.append(createTableSql(sTable));
			sb.append(";\n");
		}
		sb.append("set FOREIGN_KEY_CHECKS=1;\n");
		return sb.toString();
	}

	public String showAllCreateViewsSql() {
		String sDBName = showSelectDatabase();
		String sSelect = "select table_name,view_definition from information_schema.views where table_schema='" + sDBName
				+ "';";
		ArrayList<ArrayList<String>> lslsData = this.selectStringMatrix(sSelect);
		StringBuilder sb = new StringBuilder();
		for (ArrayList<String> lsData : lslsData) {
			sb.append("create view ");
			sb.append(lsData.get(0));
			sb.append(" as ");
			sb.append(lsData.get(1));
			sb.append(";\n");
		}
		return sb.toString();
	}

	public String deleteDataSql(ArrayList<String> lsTables) {
		StringBuilder sb = new StringBuilder();
		for (String sTable : lsTables) {
			sb.append(DeleteSql.toSql(sTable));
			sb.append(";\n");
		}
		return sb.toString();
	}

	public String truncateDataSql(ArrayList<String> lsTables) {
		StringBuilder sb = new StringBuilder();
		for (String sTable : lsTables) {
			sb.append(TruncateSql.toSql(sTable));
			sb.append(";\n");
		}
		return sb.toString();
	}

	public void saveDataSql(String sDataSqlFile, ArrayList<String> lsTables) {
		WriteFileAction wfa = new WriteFileAction();
		if (wfa.open(sDataSqlFile)) {
			for (String sTable : lsTables) {
				int iPage = 0;
				do {
					String sData = showDataSql(sTable, iPage++, 1000);
					if (sData != null) {
						try {
							wfa.write(sData.getBytes());
							wfa.write("\n".getBytes());
						} catch (Exception e) {

						}
					} else {
						break;
					}
				} while (true);
			}
			wfa.close();
		}
	}

	public String showDataSql(ArrayList<String> lsTables) {
		StringBuilder sb = new StringBuilder();
		for (String sTable : lsTables) {
			int iPage = 0;
			do {
				String sData = showDataSql(sTable, iPage++, 1000);
				if (sData != null) {
					sb.append(sData);
					sb.append('\n');
				} else {
					break;
				}
			} while (true);
		}
		return sb.toString();
	}

	public String showDataSql(String sTable) {
		ArrayList<String> lsFields = selectColumnName(sTable);
		InsertMultiSql ims = new InsertMultiSql();
		ims.addFields(sTable, lsFields);

		String sSelect = "select * from " + sTable;
		ArrayList<ArrayList<String>> lsValues = this.selectStringMatrix(sSelect);
		for (ArrayList<String> lsValue : lsValues) {
			ims.addValue(lsValue);
		}
		if (ims.getValues() > 0) {
			return ims.toSql();
		} else {
			return null;
		}
	}

	public String showDataSql(String sTable, int iPage, int iPageSize) {
		ArrayList<String> lsFields = selectColumnName(sTable);
		InsertMultiSql ims = new InsertMultiSql();
		ims.addFields(sTable, lsFields);

		String sSelect = "select * from " + sTable + MySqlLimit.toSql(iPage * iPageSize, iPageSize);
		ArrayList<ArrayList<String>> lsValues = this.selectStringMatrix(sSelect);
		for (ArrayList<String> lsValue : lsValues) {
			ims.addValue(lsValue);
		}
		if (ims.getValues() > 0) {
			return ims.toSql();
		} else {
			return null;
		}
	}

	static public MySqlDbAction getDatabaseObject(String sMySqlIP, String sMySqlDBName, String sMySqlUser, String sMySqlPasswd) {
		if (OsDepend.isLinux()) {
			sMySqlIP = "127.0.0.1";
		}
		MySqlDbAction dbObj = new MySqlDbAction();
		if (dbObj.open(sMySqlIP, sMySqlDBName, sMySqlUser, sMySqlPasswd)) {
			return dbObj;
		}
		return null;
	}
}
