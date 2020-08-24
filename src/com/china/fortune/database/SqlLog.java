package com.china.fortune.database;

import com.china.fortune.os.log.FileLogSwitchAction;
import com.china.fortune.string.StringUtils;

public class SqlLog {
	public interface SqlInterface {
		void onSql(String sSql);
	}

	static public void clearHistory(int days) {
		obj.clearHistory(days);
	}
	
	static private FileLogSwitchAction obj = new FileLogSwitchAction();
	static private SqlInterface onSqlAction = null;
	static {
		obj.setLog("log", "mySql");
	}

	static public void attach(SqlInterface osa) {
		onSqlAction = osa;
	}
	
	static public void close() {
		obj.closeFile();
	}

	static public void showLog(boolean bShow) {
		obj.showLog(bShow);
	}

	static public void setLog(String sDir, String sFile) {
		obj.setLog(sDir, sFile);
	}

	static public void log(String sSql) {
		obj.log(sSql);
		if (onSqlAction != null) {
			if (StringUtils.startWithIgnoreCase(sSql, "insert") || StringUtils.startWithIgnoreCase(sSql, "update")
					|| StringUtils.startWithIgnoreCase(sSql, "delete")) {
				onSqlAction.onSql(sSql);
			}
		}
	}

}
