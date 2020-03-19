package com.china.fortune.json;

import com.china.fortune.global.Log;
import com.china.fortune.struct.FastList;

public class JSONDataCenter {
	private String sJson;
	private int iCurrent;
	private int iJsonLen;
	private FastList<Object> skJson = new FastList<Object>();
	private int iSubStringStart;
	private Object oRoot = null;
	private String sKey;

	public Object getRootObject() {
		return oRoot;
	}

	public JSONDataCenter(String s, Object root) {
		sJson = s;
		iCurrent = -1;
		iJsonLen = s.length();
		iSubStringStart = 0;
		oRoot = root;
	}

	public JSONDataCenter(String s) {
		sJson = s;
		iCurrent = -1;
		iJsonLen = s.length();
		iSubStringStart = 0;
	}

	public char getAndFilter() {
		char curChar = 0;
		iCurrent++;
		while (iCurrent < iJsonLen) {
			curChar = sJson.charAt(iCurrent);
			if (curChar > 32 && curChar < 127) {
				// Log.logClass(iCurrent + ":" + curChar);
				break;
			} else {
				iCurrent++;
			}
		}
		return curChar;
	}

	public boolean goAndFilter() {
		iCurrent++;
		while (iCurrent < iJsonLen) {
			char curChar = sJson.charAt(iCurrent);
			if (curChar > 32 && curChar < 127) {
				// Log.logClass(iCurrent + ":" + curChar);
				break;
			} else {
				iCurrent++;
			}
		}
		return iCurrent < iJsonLen;
	}

	public void setSubStringStart(int iAdd) {
		iSubStringStart = iCurrent + iAdd;
		bNotDequote = true;
	}

	private boolean bNotDequote = true;
	private boolean bQuote = true;

	public boolean isDoubleQuotes() {
		if (iCurrent < iJsonLen) {
			if (bQuote && sJson.charAt(iCurrent) == '"') {
				return true;
			}
		}
		return false;
	}

	public char getCharAndSetQuote() {
		if (iCurrent < iJsonLen) {
			char c = sJson.charAt(iCurrent);
			if (c == '\\' && bQuote) {
				bNotDequote = false;
				bQuote = false;
			} else {
				bQuote = true;
			}
			return c;
		} else {
			return 0;
		}
	}

	public char getChar() {
		if (iCurrent < iJsonLen) {
			return sJson.charAt(iCurrent);
		} else {
			return 0;
		}
	}

	public void backChar() {
		iCurrent--;
	}

	public void addChild(Object child) {
		if (skJson.size() > 0) {
			Object o = skJson.peek();
			if (o instanceof JSONObject) {
				((JSONObject) o).put(sKey, child);
			} else {
				((JSONArray) o).put(child);
			}
		} else {
			if (oRoot == null) {
				oRoot = child;
			} else {
				child = oRoot;
			}
		}
		skJson.push(child);
	}

	public void popJSON() {
		if (skJson.size() > 0) {
			skJson.pop();
		}
	}

	public void saveKey() {
		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
			sKey = sJson.substring(iSubStringStart, iCurrent);
		}
	}

	public void saveStringValue() {
		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
			String sValue = null;
			if (bNotDequote) {
				sValue = sJson.substring(iSubStringStart, iCurrent);
			} else {
				sValue = JSONString.dequote(sJson, iSubStringStart, iCurrent);
			}
			if (skJson.size() > 0) {
				JSONObject json = (JSONObject) skJson.peek();
				json.put(sKey, sValue);
			}
			// Log.logClass(skJson.iSize() + ":" + sKey + ":" + sValue);
		}
	}

	public void saveArrayString() {
		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
			String sValue = null;
			if (bNotDequote) {
				sValue = sJson.substring(iSubStringStart, iCurrent);
			} else {
				sValue = JSONString.dequote(sJson, iSubStringStart, iCurrent);
			}
			if (skJson.size() > 0) {
				JSONArray jarr = (JSONArray) skJson.peek();
				jarr.put(sValue);
			}
			Log.logClass(skJson.size() + ":" + sValue);
		}
	}

	public void saveOtherValue() {
		char[] sValue = filterData();
		if (sValue != null && skJson.size() > 0) {
			JSONObject json = (JSONObject) skJson.peek();
			// Integer iValue = StringHelper.toNumber(sValue);
			json.put(sKey, sValue);
//			Log.logClass(skJson.iSize() + ":" + sKey + ":" + sValue);
		}
	}

//	private String filterString() {
//		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
//			int iEnd = iCurrent - 1;
//			for (; iEnd >= iSubStringStart; iEnd--) {
//				char curChar = sJson.charAt(iEnd);
//				if (curChar > 32 && curChar < 127) {
//					return sJson.substring(iSubStringStart, iEnd + 1);
//				}
//			}
//		}
//		return null;
//	}

	private char[] filterData() {
		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
			int iEnd = iCurrent - 1;
			for (; iEnd >= iSubStringStart; iEnd--) {
				char curChar = sJson.charAt(iEnd);
				if (curChar > 32 && curChar < 127) {
					int iLen = iEnd + 1 - iSubStringStart;
					if (iLen > 0) { 
						char[] cDes = new char[iLen];
						sJson.getChars(iSubStringStart, iEnd + 1, cDes, 0);
						return cDes;
					}
				}
			}
		}
		return null;
	}

	public void saveArrayOther() {
		char[] sValue = filterData();
		if (sValue != null && skJson.size() > 0) {
			JSONArray json = (JSONArray) skJson.peek();
			// Integer iValue = StringHelper.toNumber(sValue);
			json.put(sValue);
		}
		// Log.logClass(skJson.iSize() + ":" + sValue);
	}
}
