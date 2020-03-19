package com.china.fortune.xml;

public class AttrNode {
	private String sKey = null;
	private String sValue = null;
	
	public AttrNode() {
	}
	
	public AttrNode(String key, String value) {
		sKey = key;
		sValue = value;
	}
	
	public String getKey() {
		return sKey;
	}
	
	public String getValue() {
		return sValue;
	}
	
	public void setKey(String s) {
		sKey = s;
	}
	
	public void setValue(String s) {
		sValue = s;
	}
	
	public AttrNode clone() {
		return new AttrNode(sKey, sValue);
	}
}
