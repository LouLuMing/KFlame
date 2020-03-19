package com.china.fortune.myant;

import java.util.ArrayList;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.os.xml.XmlParser;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.AttrNode;
import com.china.fortune.xml.XmlNode;

public class MyAntXml {
	final private static String cStrFrom = "${";
	final private static String cStrTo = "}";

	static public XmlNode parse(String sFile) {
		XmlNode cfgXmlObj = null;
		if (sFile.startsWith("http:")) {
			String sFileData = HttpSendAndRecv.doGet(sFile);
			if (sFileData != null) {
				cfgXmlObj = XmlParser.parse(sFileData, "utf-8");
			}
		} else {
			if (FileHelper.isExists(sFile)) {
				cfgXmlObj = XmlParser.parseFile(sFile);	
			}
		}
		if (cfgXmlObj != null) {
			setEnvironment(cfgXmlObj);
			initProperty(cfgXmlObj);
			initTarget(cfgXmlObj);
		}
		return cfgXmlObj;
	}

	public static void setEnvironment(XmlNode cfgXmlObj) {
		String sBaseDir = cfgXmlObj.getAttrValue("basedir");
		if (sBaseDir != null && sBaseDir.compareTo("./") != 0) {
			XmlNode xmlObj = cfgXmlObj.addChildNode("property", null);
			xmlObj.addAttrNode("name", "basedir");
			String sFullPath = PathUtils.getFullPath(sBaseDir);
			xmlObj.addAttrNode("value", PathUtils.delSeparator(sFullPath));
			PathUtils.setCurrentDataPath(sFullPath);
		}
	}

	private static String findValueByProperty(ArrayList<XmlNode> lsXmlObj, String sProp, boolean bRecursion) {
		String sValue = null;
		if (lsXmlObj != null) {
			for (XmlNode xmlObj : lsXmlObj) {
				if (sProp.endsWith(xmlObj.getAttrValue("name"))) {
					sValue = xmlObj.getAttrValue("value");
					if (bRecursion) {
						String value = findAndReplace(lsXmlObj, sValue, bRecursion);
						if (value != null) {
							sValue = value;
						}
					}
					break;
				}
			}
		}
		return sValue;
	}

	private static String findAndReplace(ArrayList<XmlNode> lsXmlObj, String sValue, boolean bRecursion) {
		String sReplace = null;
		if (sValue != null) {
			String prop = StringAction.findBetween(sValue, cStrFrom, cStrTo);
			if (prop != null) {
				if (prop.length() > 0) {
					String value = findValueByProperty(lsXmlObj, prop, bRecursion);
					if (value != null) {
						sReplace = sValue.replace(cStrFrom + prop + cStrTo, value);
					} else {
						Log.logClass("property:" + prop);
					}
				}
			}
		}
		return sReplace;
	}

	public static void initProperty(XmlNode xmlObj) {
		if (xmlObj != null) {
			ArrayList<XmlNode> lsXmlObj = xmlObj.getChildNodeSet("property");
			if (lsXmlObj != null) {
				for (XmlNode prop : lsXmlObj) {
					String sValue = prop.getAttrValue("value");
					String sReplace = findAndReplace(lsXmlObj, sValue, true);
					if (sReplace != null) {
						prop.setAttrValue("value", sReplace);
					}
				}
			}
		}
	}

	private static void initATarget(ArrayList<XmlNode> lsXmlProp, XmlNode xmlObj) {
		if (lsXmlProp != null && xmlObj != null) {
			String sText = xmlObj.getText();
			if (sText != null && sText.trim().length() > 0) {
				String sReplace = findAndReplace(lsXmlProp, sText, false);
				if (sReplace != null) {
					xmlObj.setText(sReplace);
				}
			}

			ArrayList<AttrNode> lsAttr = xmlObj.getAttrNodeSet();
			if (lsAttr != null) {
				for (AttrNode attr : lsAttr) {
					String sValue = findAndReplace(lsXmlProp, attr.getValue(), false);
					if (sValue != null) {
						attr.setValue(sValue);
					}
				}
			}

			ArrayList<XmlNode> lsChild = xmlObj.getChildNodeSet();
			if (lsChild != null) {
				for (XmlNode child : lsChild) {
					initATarget(lsXmlProp, child);
				}
			}
		}
	}

	static void initTarget(XmlNode xmlObj) {
		ArrayList<XmlNode> lsXmlProp = xmlObj.getChildNodeSet("property");
		if (lsXmlProp != null) {
			ArrayList<XmlNode> lsTarget = xmlObj.getChildNodeSet("target");
			if (lsTarget != null) {
				for (XmlNode targ : lsTarget) {
					initATarget(lsXmlProp, targ);
				}
			}
		}
	}

	static XmlNode findTarget(ArrayList<XmlNode> lsXmlTarg, String sTarg) {
		XmlNode xmlObj = null;
		for (XmlNode targ : lsXmlTarg) {
			if (sTarg.compareTo(targ.getAttrValue("name")) == 0) {
				xmlObj = targ;
				break;
			}
		}
		return xmlObj;
	}
}
