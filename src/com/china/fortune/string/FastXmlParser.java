package com.china.fortune.string;

import com.china.fortune.global.Log;

public class FastXmlParser {
	static public int findAndCalTag(String sXml) {
		char[] lsChar = sXml.toCharArray();
		int iSigner = 0;
		for (int i = 0; i < lsChar.length; i++) {
			if (lsChar[i] == '<') {
				i++;
				for (; i < lsChar.length; i++) {
					if (lsChar[i] == ' ' || lsChar[i] == '/'
							|| lsChar[i] == '>') {
						break;
					}
					iSigner += lsChar[i];
				}
				break;
			}
		}
		return iSigner;
	}

	static public String getTag(String sXml) {
		char[] lsChar = sXml.toCharArray();
		StringBuilder sb = new StringBuilder();
		// boolean bStart = false;
		// for (char c : lsChar) {
		// if (bStart) {
		// if (c == ' ' || c == '/' || c == '>') {
		// break;
		// }
		// sb.append(c);
		// }
		// if (c == '<') {
		// bStart = true;
		// }
		// }
		for (int i = 0; i < lsChar.length; i++) {
			if (lsChar[i] == '<') {
				i++;
				for (; i < lsChar.length; i++) {
					if (lsChar[i] == ' ' || lsChar[i] == '/'
							|| lsChar[i] == '>') {
						break;
					}
					sb.append(lsChar[i]);
				}
				break;
			}
		}
		return sb.toString();
	}

	static public String getAttrValue(String sXml, String sKey) {
		String sValue = null;
		String sFullKey = " " + sKey + "=";
		int index = sXml.indexOf(sFullKey);
		if (index > 0) {
			int iStart = index + (sFullKey.length() + 1);
			char sDot = sXml.charAt(iStart - 1);
			int iEnd = sXml.indexOf(sDot, iStart);
			if (iEnd > 0) {
				sValue = sXml.substring(iStart, iEnd);
			}
		}
		return sValue;
	}

	static public String setAttrValue(String sXml, String sKey, String sValue) {
		String sOut = null;
		int iHead = sXml.indexOf('>');
		if (iHead > 0) {
			boolean bFound = false;
			String sHead = sXml.substring(0, iHead);
			String sFullKey = " " + sKey + "=";
			int iKey = sHead.indexOf(sFullKey);
			if (iKey > 0) {
				int iStart = iKey + (sFullKey.length() + 1);
				char sDot = sXml.charAt(iStart - 1);
				int iEnd = sXml.indexOf(sDot, iStart);
				if (iEnd > 0) {
					sOut = sXml.substring(0, iStart) + sValue
							+ sXml.substring(iEnd, sXml.length());
					bFound = true;
				}
			}
			if (!bFound) {
				if (iHead > 1) {
					char c = sXml.charAt(iHead - 1);
					if (c == '/') {
						iHead--;
					}
				}
				sOut = sXml.substring(0, iHead) + " " + sKey + "='" + sValue
						+ "'" + sXml.substring(iHead, sXml.length());
			}
		}
		return sOut;
	}

	static private int findAttribute(String sXml, String sAttr, int iStart, int iLimit) {
		do {
			int iAttr = sXml.indexOf(sAttr, iStart);
			if (iAttr > 0) {
				int iEnd = iAttr + sAttr.length();
				char cEnd = sXml.charAt(iEnd);
				if (sXml.charAt(iAttr - 1) == ' '
						&& (cEnd == ' ' || cEnd == '=')) {
					return iAttr;
				}
				iStart = iEnd;
			} else {
				return -1;
			}
		} while (true);
	}

	static public String getAttrValue(String sXml, String sTag, String sAttr) {
		int iLeftTagStart = sXml.indexOf(sTag);
		if (iLeftTagStart > 0) {
			iLeftTagStart += sTag.length();
			int iLeftTagEnd = sXml.indexOf('>', iLeftTagStart);
			if (iLeftTagEnd > 0) {
				//int iAttr = sXml.indexOf(sAttr, iLeftTagStart);
				int iAttr = findAttribute(sXml, sAttr, iLeftTagStart, iLeftTagEnd);
				if (iAttr > 0 && iAttr < iLeftTagEnd) {
					int iValueStart = iAttr + sAttr.length() + 1;
					int iValueEnd = 0;
					char cDot = 0;
					for (int i = iValueStart; i < iLeftTagEnd; i++) {
						char c = sXml.charAt(i);
						if (c == '\'' || c == '"') {
							cDot = c;
							iValueStart = i;
							break;
						}
					}
					for (int i = iValueStart + 1; i < iLeftTagEnd; i++) {
						if (sXml.charAt(i) == cDot) {
							iValueEnd = i;
							break;
						}
					}
					return sXml.substring(iValueStart+1, iValueEnd);
				}
			}
		}
		return null;
	}
	
	static public String setAttrValue(String sXml, String sTag, String sAttr,
			String sValue) {
		int iLeftTagStart = sXml.indexOf(sTag);
		if (iLeftTagStart > 0) {
			iLeftTagStart += sTag.length();
			int iLeftTagEnd = sXml.indexOf('>', iLeftTagStart);
			if (iLeftTagEnd > 0) {
				//int iAttr = sXml.indexOf(sAttr, iLeftTagStart);
				int iAttr = findAttribute(sXml, sAttr, iLeftTagStart, iLeftTagEnd);
				if (iAttr > 0 && iAttr < iLeftTagEnd) {
					int iValueStart = iAttr + sAttr.length() + 1;
					int iValueEnd = 0;
					char cDot = 0;
					for (int i = iValueStart; i < iLeftTagEnd; i++) {
						char c = sXml.charAt(i);
						if (c == '\'' || c == '"') {
							cDot = c;
							iValueStart = i;
							break;
						}
					}
					for (int i = iValueStart + 1; i < iLeftTagEnd; i++) {
						if (sXml.charAt(i) == cDot) {
							iValueEnd = i;
							break;
						}
					}
					StringBuilder sb = new StringBuilder();
					sb.append(sXml.substring(0, iValueStart + 1));
					sb.append(sValue);
					sb.append(sXml.substring(iValueEnd, sXml.length()));
					return sb.toString();
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(sXml.substring(0, iLeftTagStart + 1));
					sb.append(sAttr);
					sb.append("=\"");
					sb.append(sValue);
					sb.append("\"");
					sb.append(sXml.substring(iLeftTagStart, sXml.length()));
					return sb.toString();
				}
			}
		}
		return sXml;
	}

	static public String getElement(String sXml, String sTag) {
		int iLeftTagStart = sXml.indexOf(sTag);
		if (iLeftTagStart > 0) {
			int iLeftTagEnd = sXml.indexOf('>', iLeftTagStart);
			if (iLeftTagEnd > 0) {
				int iTagEnd = sXml.indexOf("</" + sTag, iLeftTagEnd);
				if (iTagEnd > 0) {
					return sXml.substring(iLeftTagEnd+1, iTagEnd);
				}
			}
		}
		return null;
	}

	static public String setElement(String sXml, String sTag, String sElement) {
		int iLeftTagStart = sXml.indexOf(sTag);
		if (iLeftTagStart > 0) {
			int iLeftTagEnd = sXml.indexOf('>', iLeftTagStart);
			if (iLeftTagEnd > 0) {
				int iTagEnd = sXml.indexOf('<', iLeftTagEnd);
				if (iTagEnd > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(sXml.substring(0, iLeftTagEnd + 1));
					sb.append(sElement);
					sb.append(sXml.substring(iTagEnd, sXml.length()));
					return sb.toString();
				}
			}
		}
		return sXml;
	}

	public static void main(String[] args) {
		String sXml = " <gpmsg code='1000'>Token is Invalid</gpmsg>";

		// sXml = SimpleXmlParser.setAttrValue(sXml, "id", "1232323");
		// sXml = SimpleXmlParser.setAttrValue(sXml, "d", "ddddd");

		sXml = FastXmlParser.setAttrValue(sXml, "error", "code",
				"34234");
		Log.log(sXml);
		sXml = FastXmlParser.setAttrValue(sXml, "error", "d",
				"d");
		Log.log(sXml);
		sXml = FastXmlParser.setAttrValue(sXml, "error", "adfad",
				"adfad");
		Log.log(sXml);

		
		Log.log(FastXmlParser.setElement(sXml, "error", "hello"));
		Log.log("" + FastXmlParser.findAndCalTag(sXml));
		Log.logClass(FastXmlParser.getAttrValue(sXml, "error", "code"));

		Log.log(getElement(sXml, "gpmsg"));
//		Log.logClass(SimpleXmlParser.getAttrValue(sXml, "d"));
	}
}
