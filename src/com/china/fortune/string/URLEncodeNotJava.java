package com.china.fortune.string;

import java.net.URLEncoder;

import com.china.fortune.global.ConstData;

public class URLEncodeNotJava {
	static private void encodeSpecialKey(StringBuilder sb, char c) {
		switch (c) {
		case '+':
			sb.append("%2B");
			break;
		case ' ':
			sb.append("%20");
			break;
		case '/':
			sb.append("%2F");
			break;
		case '?':
			sb.append("%3F");
			break;
		case '%':
			sb.append("%25");
			break;
		case '#':
			sb.append("%23");
			break;
		case '&':
			sb.append("%26");
			break;
		case '=':
			sb.append("%3D");
			break;
		default:
			sb.append(c);
		}
	}

	static public void encode(StringBuilder sb, String sData, String sCode) {
		char[] lsChar = sData.toCharArray();
		StringBuilder sbChinese = new StringBuilder();
		for (int i = 0; i < lsChar.length; i++) {
			if (lsChar[i] < 0xff) {
				if (sbChinese.length() > 0) {
					try {
						sb.append(URLEncoder.encode(sbChinese.toString(), sCode));
					} catch (Exception e) {
					}
					sbChinese.setLength(0);
				}
				encodeSpecialKey(sb, lsChar[i]);
			} else {
				sbChinese.append(lsChar[i]);
			}
		}
		if (sbChinese.length() > 0) {
			try {
				sb.append(URLEncoder.encode(sbChinese.toString(), ConstData.sHttpCharset));
			} catch (Exception e) {
			}
		}
	}

	static public String encode(String sData, String sCode) {
		StringBuilder sb = new StringBuilder();
		encode(sb, sData, sCode);
		return sb.toString();
	}
	
}
