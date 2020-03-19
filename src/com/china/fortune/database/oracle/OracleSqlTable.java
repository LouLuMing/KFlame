package com.china.fortune.database.oracle;

import com.china.fortune.database.Table;
import com.china.fortune.os.database.DbAction;

import java.util.ArrayList;

public class OracleSqlTable extends Table {
	@Override
	public boolean init(DbAction dbObj, String tbs) {
		sTable = tbs;
		lsFields.clear();
		String sSelect = "select t.column_name from user_col_comments t where t.table_name='" + sTable + "';";
		ArrayList<String> lsData = dbObj.selectStringOneColumn(sSelect);
		for (String s : lsData) {
			lsFields.add(s);
		}
		return lsFields.size() > 0;
	}

}
