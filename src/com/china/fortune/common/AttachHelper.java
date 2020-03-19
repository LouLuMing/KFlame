package com.china.fortune.common;

public class AttachHelper {
	private Object attachObj;
	public void attach(Object o) {
		attachObj = o;
	}
	
	public Object attachment() {
		return attachObj;
	}
}
