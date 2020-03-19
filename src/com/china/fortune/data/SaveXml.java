package com.china.fortune.data;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.ConstData;
import com.china.fortune.os.xml.XmlParser;
import com.china.fortune.xml.XmlNode;

public class SaveXml {
	public void set(String sFile, XmlNode xmlObj) {
		if (xmlObj != null) {
			String sXml = xmlObj.createXML(ConstData.sFileCharset);
			if (sXml != null) {
				FileHelper.writeSmallFile(sFile, sXml, ConstData.sFileCharset);
			}
		}
	}
	
	public XmlNode get(String sFile) {
		if (sFile != null) {
			String sXml = FileHelper.readSmallFile(sFile, ConstData.sFileCharset);
			if (sXml != null) {
				return XmlParser.parse(sXml, ConstData.sFileCharset);
			}
		}
		return null;
	}
}
