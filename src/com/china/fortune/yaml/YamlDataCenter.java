package com.china.fortune.yaml;

import com.china.fortune.global.Log;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;

public class YamlDataCenter {
	private String sJson;
	private int iCurrent;
	private int iJsonLen;
	private FastList<Object> flJson = new FastList<>();
	private FastList<Integer> flSpace = new FastList<>();
	private int iSubStringStart;
	private String sKey;

	private boolean bNeedPush = true;

	public void setNeedPush() {
		bNeedPush = true;
	}

	public void pushJSONObjectIfNeed(int space) {
		if (bNeedPush) {
			addChild(new JSONObject(), space);
			bNeedPush = false;
		}
	}

	public void pushJSONArrayIfNeed(int space) {
		if (bNeedPush) {
			addChild(new JSONArray(), space);
			bNeedPush = false;
		}
	}

	public int getSpaceCount() {
		int iSpaceCount = 0;
		while (iCurrent < iJsonLen) {
			char curChar = sJson.charAt(iCurrent);
			if (curChar == '\n' || curChar == '\r') {
				iSpaceCount = 0;
			} else if (curChar == ' ') {
				iSpaceCount++;
			} else {
				break;
			}
			iCurrent++;
		}
		if (iCurrent < iJsonLen) {
			return iSpaceCount;
		} else {
			return -1;
		}
	}

	public void pop(int space) {
		while (flSpace.size() > 0) {
			int i = flSpace.peek();
			if (i > space) {
				flSpace.pop();
				flJson.pop();
			} else {
				break;
			}
		}
	}

	public Object getRootObject() {
		if (flJson.size() > 0) {
			return flJson.get(0);
		} else {
			return null;
		}
	}

	public boolean isSpace() {
		if (iCurrent < iJsonLen) {
			char curChar = sJson.charAt(iCurrent);
			return (curChar == ' ');
		} else {
			return false;
		}
	}

	public boolean isChar() {
		if (iCurrent < iJsonLen) {
			char curChar = sJson.charAt(iCurrent);
			return StringUtils.isVisibleChar(curChar) && curChar != ':' && curChar != ' ' && curChar != '-';
		} else {
			return false;
		}
	}

	public void filterSpace() {
		while (iCurrent < iJsonLen) {
			char curChar = sJson.charAt(iCurrent);
			if (curChar != ' ') {
				break;
			} else {
				iCurrent++;
			}
		}
	}

	public YamlDataCenter(String s) {
		sJson = s;
		iCurrent = 0;
		iJsonLen = s.length();
		iSubStringStart = 0;
	}

	public void setSubStringStart(int iAdd) {
		iSubStringStart = iCurrent + iAdd;
	}


	public boolean nextChar() {
		if (iCurrent < iJsonLen) {
			iCurrent++;
			return true;
		} else {
			return false;
		}
	}
	public char currentChar() {
		if (iCurrent < iJsonLen) {
			return sJson.charAt(iCurrent);
		} else {
			return 0;
		}
	}

	public void addChild(Object child, int space) {
		if (flJson.size() > 0) {
			Object o = flJson.peek();
			if (o instanceof JSONObject) {
				((JSONObject) o).put(sKey, child);
			} else {
				((JSONArray) o).put(child);
			}
		}
		flJson.push(child);
		flSpace.push(space);
	}

	public void saveKey() {
		if (iSubStringStart >= 0 && iCurrent < iJsonLen) {
			sKey = sJson.substring(iSubStringStart, iCurrent);
			Log.logClass(sKey);
		}
	}

	public void saveArrayValue() {
		String sValue = filterData();
		if (sValue != null && flJson.size() > 0) {
			JSONArray jarr = (JSONArray) flJson.peek();
			jarr.put(sValue);
		}
		Log.logClass(sValue);
	}

	public void saveValue() {
		String sValue = filterData();
		if (sValue != null && flJson.size() > 0) {
			JSONObject json = (JSONObject) flJson.peek();
			// Integer iValue = StringHelper.toNumber(sValue);
			json.put(sKey, sValue);
			Log.logClass(sKey + ":" + sValue);
		}
	}

	private String filterData() {
		if (iSubStringStart >= 0 && iSubStringStart < iCurrent) {
			int iEnd = iCurrent;
			if (iEnd >= iJsonLen) {
				iEnd = iJsonLen -1;
			}
			for (int i = iEnd; i > iSubStringStart; i--) {
				char c = sJson.charAt(i);
				if (c == '\r' || c == ' ' || c == '\n') {
					iEnd = i-1;
				} else {
					break;
				}
			}
			return sJson.substring(iSubStringStart, iEnd+1);
		}
		return null;
	}

}
