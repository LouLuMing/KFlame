package com.china.fortune.json;

public class JSONString {
	static public boolean isBoolean(Object o) {
		if (o != null) {
			Class<?> cls = o.getClass();
			if (o == Boolean.TRUE) {
				return true;
			} else if (cls == char[].class) {
				char[] lsData = (char[])o;
				if (lsData.length == 4) {
					return (lsData[0] == 't' || lsData[0] == 'T') 
							&& (lsData[1] == 'r' || lsData[1] == 'R')
							&& (lsData[2] == 'u' || lsData[2] == 'U')
							&& (lsData[3] == 'e' || lsData[3] == 'E');
				}
			} else if (cls == String.class) {
				return ((String)o).equalsIgnoreCase("true");
			}
		}
		return false;
	}

	static public String dequote(String sValue, int iStart, int iEnd) {
		StringBuilder sb = new StringBuilder();
		char c = 0;
		boolean bEscape = false;
		for (int i = iStart; i < iEnd; i++) {
			c = sValue.charAt(i);
			if (c == '\\' && !bEscape) {
				bEscape = true;
			} else {
				if (bEscape) {
					if (c == 'b') {
						sb.append("\b");
					} else if (c == 't') {
						sb.append("\t");
					} else if (c == 'n') {
						sb.append("\n");
					} else if (c == 'f') {
						sb.append("\f");
					} else if (c == 'r') {
						sb.append("\r");
					} else {
						sb.append(c);
					}
					bEscape = false;
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	static public void quote(StringBuilder sb, String sValue) {
		char c = 0;
		int len = sValue.length();
		for (int i = 0; i < len; i++) {
			c = sValue.charAt(i);
			if (c == '\\' || c == '"') {
				sb.append('\\');
				sb.append(c);
			} else if (c == '\b') {
				sb.append("\\b");
			} else if (c == '\t') {
				sb.append("\\t");
			} else if (c == '\n') {
				sb.append("\\n");
			} else if (c == '\f') {
				sb.append("\\f");
			} else if (c == '\r') {
				sb.append("\\r");
			} else {
				sb.append(c);
			}
		}
	}
}
