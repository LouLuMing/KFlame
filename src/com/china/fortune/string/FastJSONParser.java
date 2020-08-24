package com.china.fortune.string;

import com.china.fortune.global.Log;

public class FastJSONParser {
	static public String getStringValue(String sJson, String sKey) {
		String sValue = null;
		if (sJson != null) {
			String sFullKey = '"' + sKey + '"';
			int iStart = sJson.indexOf(sFullKey);
			if (iStart > 0) {
				sValue = StringUtils.findBetween(sJson, iStart + sFullKey.length() + 1, '"', '"');
			}
		}
		return sValue;
	}

	static public String getValue(String sJson, String sKey) {
		String sValue = null;
		if (sJson != null) {
			String sFullKey = '"' + sKey + '"';
			int iStart = sJson.indexOf(sFullKey);
			if (iStart > 0) {
				iStart = sJson.indexOf(':', iStart);
				if (iStart > 0) {
					iStart++;
					int iEnd = sJson.indexOf(',', iStart);
					if (iEnd < 0) {
						iEnd = sJson.indexOf('}', iStart);
						if (iEnd < 0) {
							iEnd = sJson.indexOf(']', iStart);
						}
					}
					if (iEnd > 0) {
						iEnd--;
						for (; iStart < iEnd; iStart++) {
							char s = sJson.charAt(iStart);
							if (s != ' ' && s != '"') {
								break;
							}
						}
						for (; iStart < iEnd; iEnd--) {
							char s = sJson.charAt(iEnd);
							if (s != ' ' && s != '"') {
								break;
							}
						}

						if (iStart <= iEnd) {
							sValue = sJson.substring(iStart, iEnd + 1);
						}
					}
				}
			}
		}
		return sValue;
	}

	static public String replaceValue(String sJson, String sKey, String sValue) {
		String sRetJson = sJson;
		if (sJson != null) {
			String sFullKey = '"' + sKey + '"';
			int iStart = sJson.indexOf(sFullKey);
			if (iStart > 0) {
				iStart = sJson.indexOf(':', iStart);
				if (iStart > 0) {
					iStart++;
					int iEnd = sJson.indexOf(',', iStart);
					if (iEnd < 0) {
						iEnd = sJson.indexOf('}', iStart);
						if (iEnd < 0) {
							iEnd = sJson.indexOf(']', iStart);
						}
					}
					if (iEnd > 0) {
						iEnd--;
						for (; iStart < iEnd; iStart++) {
							char s = sJson.charAt(iStart);
							if (s != ' ' && s != '"') {
								break;
							}
						}
						for (; iStart < iEnd; iEnd--) {
							char s = sJson.charAt(iEnd);
							if (s != ' ' && s != '"') {
								break;
							}
						}

						if (iStart <= iEnd) {
							StringBuilder sb = new StringBuilder();
							sb.append(sJson.substring(0, iStart));
							sb.append(sValue);
							sb.append(sJson.substring(iEnd + 1, sJson.length()));
							sRetJson = sb.toString();
						}
					}
				}
			}
		}
		return sRetJson;
	}

	public static void main(String[] args) {
		String sJson = "{\"ret\": 123456 ,\"msg\":\"fdsg\",\"token\":\"7697948a9ae5eb83ab676913f5f64bc6\" }";
		// Log.log(getStringValue(sJson, "token"));
		Log.log(replaceValue(sJson, "ret", "654321"));
		Log.log(replaceValue(sJson, "msg", "654321"));
		// Log.log(getValue(sJson, "token"));
		Log.log(getValue(sJson, "msg"));
	}
}
