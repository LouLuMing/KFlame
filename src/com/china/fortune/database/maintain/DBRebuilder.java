package com.china.fortune.database.maintain;

import java.io.File;
import java.util.ArrayList;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.reflex.ClassToPath;

public class DBRebuilder {
	private String sPath = null;
	
	public DBRebuilder() {
		sPath = PathUtils.getCurrentDataPath(true);
	}
	
	public DBRebuilder(String s) {
		sPath = PathUtils.addSeparator(s);
		PathUtils.create(sPath);
	}

	public DBRebuilder(Class<?> c) {
		String sNowPath = ClassToPath.parentPath("src", c);
		String sParentPath = PathUtils.getParentPath(sNowPath, true);
		sPath = sParentPath + "doc" + File.separatorChar;
	}

	public void saveViewsSql(MySqlDbAction dbObj) {
		String sCreateTables = dbObj.showAllCreateViewsSql();
		String sCreateSql = "createView.sql";
		String sTableSqlFile = sPath + sCreateSql;
		FileHelper.writeSmallFile(sTableSqlFile, sCreateTables);
		Log.logClass(sTableSqlFile);
	}

	public void saveTableSql(MySqlDbAction dbObj) {
		String sCreateTables = dbObj.showAllCreateTableSql();
		String sCreateSql = "createTable.sql";
		String sTableSqlFile = sPath + sCreateSql;
		FileHelper.writeSmallFile(sTableSqlFile, sCreateTables);
		Log.logClass(sTableSqlFile);
	}
	
	public void saveDataSql(MySqlDbAction dbObj, ArrayList<String> lsTables) {
		String sDataSql = "data.sql";
		String sDataSqlFile = sPath + sDataSql;
		FileHelper.writeSmallFile(sDataSqlFile, dbObj.showDataSql(lsTables));
		Log.logClass(sDataSqlFile);
	}

	public void saveDataSql(MySqlDbAction dbObj) {
		ArrayList<String> lsTables = dbObj.selectAllTableName();
		String sDataSql = "data.sql";
		String sDataSqlFile = sPath + sDataSql;
		//FileHelper.writeSmallFile(sDataSqlFile, dbObj.showDataSql(lsTables));
		dbObj.saveDataSql(sDataSqlFile, lsTables);
		Log.logClass(sDataSqlFile);
	}

	public void truncateTableSql(MySqlDbAction dbObj) {
		ArrayList<String> lsTables = dbObj.selectAllTableName();
		String sDataSql = "truncateTable.sql";
		String sDataSqlFile = sPath + sDataSql;
		FileHelper.writeSmallFile(sDataSqlFile, dbObj.truncateDataSql(lsTables));
		//dbObj.truncateDataSql(sDataSqlFile, lsTables);
		Log.logClass(sDataSqlFile);
	}


}
