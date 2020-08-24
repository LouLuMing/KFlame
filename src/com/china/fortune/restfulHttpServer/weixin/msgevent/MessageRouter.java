package com.china.fortune.restfulHttpServer.weixin.msgevent;

import com.china.fortune.os.database.DbAction;
import com.china.fortune.xml.XmlNode;

public class MessageRouter implements EventInterface {
	private EventHashMap mapEvent = new EventHashMap();
	private String sEventKey = null;
	public MessageRouter(String sTag) {
		sEventKey = sTag;
	}
	
	public void put(String sTag, EventInterface be) {
		mapEvent.put(sTag, be);
	}
	
	@Override
	public XmlNode doAction(DbAction dbObj, XmlNode xmlObj) {
		String sEvent = xmlObj.getChildNodeText(sEventKey);
		XmlNode sendObj = null;
		EventInterface be = mapEvent.get(sEvent);
		if (be != null) {
			sendObj = be.doAction(dbObj, xmlObj);
		}
		return sendObj;
	}

}
