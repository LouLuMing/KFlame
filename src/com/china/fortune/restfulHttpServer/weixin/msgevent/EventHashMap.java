package com.china.fortune.restfulHttpServer.weixin.msgevent;

import java.util.HashMap;

public class EventHashMap {
	private HashMap<String, EventInterface> mapEvent = new HashMap<String, EventInterface>();
	
	public EventInterface get(String sTag) {
		EventInterface be = null;
		if (sTag != null) {
			be = mapEvent.get(sTag.toLowerCase());
		}
		return be;
	}
	
	public void put(String sTag, EventInterface be) {
		if (sTag != null) {
			mapEvent.put(sTag.toLowerCase(), be);
		}
	}
}
