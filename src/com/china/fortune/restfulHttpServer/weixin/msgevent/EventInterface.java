package com.china.fortune.restfulHttpServer.weixin.msgevent;

import com.china.fortune.os.database.DbAction;
import com.china.fortune.xml.XmlNode;

public interface EventInterface {
	public XmlNode doAction(DbAction dbObj, XmlNode xmlObj);
}
