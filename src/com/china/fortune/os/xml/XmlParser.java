package com.china.fortune.os.xml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.xml.XmlNode;

public class XmlParser {
	static private XmlParserHandler handler = new XmlParserHandler();
	static private SAXParser mParser = null;

	static private boolean createParser() {
		if (mParser == null) {
			try {
				mParser = SAXParserFactory.newInstance().newSAXParser();
//				SAXParserFactory spf = SAXParserFactory.newInstance();
//				spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
//				spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
//				spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//				mParser = spf.newSAXParser();
			} catch (Exception e) {
				mParser = null;
				Log.logException(e);
			}
		}
		return (mParser != null);
	}

	static public XmlNode parse(byte[] pData, int iOff, int iLen) {
		XmlNode rs = null;
		synchronized (XmlParser.class) {
			if (createParser()) {
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(pData,
							iOff, iLen);
					mParser.parse(is, handler);
					rs = handler.getRoot();
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
			}
		}
		return rs;
	}

	static public XmlNode parse(byte[] pData) {
		return parse(pData, 0, pData.length);
	}

	static public XmlNode parse(String xml, String textCode) {
		XmlNode xmlObj = null;
		synchronized (XmlParser.class) {
			if (createParser()) {
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(
							xml.getBytes(textCode));
					mParser.parse(is, handler);
					xmlObj = handler.getRoot();
				} catch (Exception e) {
					Log.logException(e);
				} catch (Error e) {
					Log.logException(e);
				}
			}
		}
		return xmlObj;
	}

	static public XmlNode parseFile(String fileName) {
		byte[] pData = FileUtils.readSmallFile(fileName);
		if (pData != null) {
			return parse(pData, 0, pData.length);
		}
		return null;
	}
}
