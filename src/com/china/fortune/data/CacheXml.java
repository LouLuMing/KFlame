package com.china.fortune.data;

import java.util.HashMap;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.ConstData;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.os.xml.XmlParser;
import com.china.fortune.xml.XmlNode;

public class CacheXml {
	private String sXmlPath = null;
	private HashMap<String, XmlNode> lsObject = new HashMap<String, XmlNode>();
	
	public boolean init(String sPath) {
		if (PathUtils.create(sPath)) {
			sXmlPath = PathUtils.addSeparator(sPath);
			return true;
		}
		return false;
	}
	
	public XmlNode get(String sFile) {
		String sDir = sXmlPath + sFile;
		XmlNode xmlObj = lsObject.get(sDir);
		if (xmlObj == null) {
			String sXml = FileHelper.readSmallFile(sDir, ConstData.sFileCharset);
			if (sXml != null) {
				xmlObj = XmlParser.parse(sXml, ConstData.sFileCharset);
				lsObject.put(sDir, xmlObj);
			}
		}
		return xmlObj;
	}

	public void set(String sFile, XmlNode xmlObj) {
		if (xmlObj != null) {
			String sXml = xmlObj.createXML(ConstData.sFileCharset);
			if (sXml != null) {
				String sDir = sXmlPath + sFile;
				FileHelper.writeSmallFile(sDir, sXml, ConstData.sFileCharset);
				XmlNode prev = lsObject.put(sDir, xmlObj);
				if (prev != null) {
					prev.clear();
				}
			}
		}
	}
	
	public void del(String sFile) {
		String sDir = sXmlPath + sFile;
		XmlNode xmlObj = lsObject.remove(sDir);
		if (xmlObj != null) {
			xmlObj.clear();
		}
		FileHelper.delete(sDir);
	}
	
	public void delAll() {
		if (lsObject.size() > 0) {
			for (XmlNode xmlObj : lsObject.values()) {
				if (xmlObj != null) {
					xmlObj.clear();
				}
			}
			lsObject.clear();
		}
		if (sXmlPath != null) {
			PathUtils.delete(sXmlPath, false);
		}
	}
}
