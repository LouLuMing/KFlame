package com.china.fortune.xml;

import com.china.fortune.global.ConstData;

public class XmlString {
	private StringBuilder sbXml = new StringBuilder();
	
	public void addHead() {
		addHead("1.0", ConstData.sFileCharset);
	}
	
	public void addHead(String sCharset) {
		addHead("1.0", sCharset);
	}
	
	
	public void addHead(String sVersion, String sCharset) {
		sbXml.setLength(0);
		sbXml.append("<?xml version=\"");
		sbXml.append(sVersion);
		sbXml.append("\" encoding=\"");
		sbXml.append(sCharset);
		sbXml.append("\"?>");
	}
	
	public void addElement(String sElement) {
		sbXml.append(sElement);
	}
	
	public void addAttribute(String sName, String sValue) {
		sbXml.append(" ");
		sbXml.append(sName);
		sbXml.append("=\"");
		sbXml.append(sValue);
		sbXml.append("\"");
	}
	
	public void addTag(String sTag) {
		sbXml.append("<");
		sbXml.append(sTag);
		sbXml.append("/>");
	}
	
	public void addTagElement(String sTag, String sElement) {
		addTagStart(sTag);
		addElement(sElement);
		addTagEnd(sTag);
	}

	public void addTagStart(String sTag) {
		sbXml.append("<");
		sbXml.append(sTag);
		addTagStartTail();
	}
	
	public void addTagStartHead(String sTag) {
		sbXml.append("<");
		sbXml.append(sTag);
	}
	
	public void addTagStartTail() {
		sbXml.append(">");
	}
	
	public void addTagStartEnd() {
		sbXml.append("/>");
	}
	
	public void addTagEnd(String sTag) {
		sbXml.append("</");
		sbXml.append(sTag);
		sbXml.append(">");
	}
	
	public String toString() {
		return sbXml.toString();
	}
	
	static public String escapeXml(String sXml) {
		String sOut = sXml;
		if (sOut != null) {
			sOut = sOut.replace("&", "&amp;");
			sOut = sOut.replace("<", "&lt;");  
			sOut = sOut.replace(">", "&gt;");  
			sOut = sOut.replace("'", "&apos;");  
			sOut = sOut.replace("\"", "&quot;"); 
		}
		return sOut;
	}
	
	static public String unescapeXml(String sXml) {
		String sOut = sXml;
		if (sOut != null) {
			sOut = sOut.replace("&amp;", "&");
			sOut = sOut.replace("&lt;", "<");  
			sOut = sOut.replace("&gt;", ">");  
			sOut = sOut.replace("&apos;", "'");  
			sOut = sOut.replace("&quot;", "\""); 
		}
		return sOut;
	}
}
