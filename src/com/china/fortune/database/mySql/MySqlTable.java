package com.china.fortune.database.mySql;

import java.util.ArrayList;

import com.china.fortune.database.Table;
import com.china.fortune.os.database.DbAction;

public class MySqlTable extends Table {
	@Override
	public boolean init(DbAction dbObj, String tbs) {
		sTable = tbs;
		lsFields.clear();
		String sSelect = "show columns from " + sTable + ";";
		ArrayList<ArrayList<String>> lsMatrix = dbObj.selectStringMatrix(sSelect);
		for (ArrayList<String> item : lsMatrix) {
			if (item.size() > 0) {
				lsFields.add(item.get(0));
			}
		}
		return lsFields.size() > 0;
	}

}
