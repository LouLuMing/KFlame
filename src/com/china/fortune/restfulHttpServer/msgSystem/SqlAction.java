package com.china.fortune.restfulHttpServer.msgSystem;

import com.china.fortune.os.database.DbAction;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;

public class SqlAction implements MsgInterface {
	public String sSql;

	@Override
	public void doAction(Object o) {
		DbAction dbObj = (DbAction)o;
		dbObj.execute(sSql);
	}
}
