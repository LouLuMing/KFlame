package com.china.fortune.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.global.ConstData;

public class KeyValueBuilder {
	private ArrayList<String2Struct> lsS2S = new ArrayList<String2Struct>();
	
	public void clear() {
		lsS2S.clear();
	}
	
	public void add(String s1, String s2) {
		if (s1 != null && s2 != null) {
			lsS2S.add(new String2Struct(s1, s2));
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			if (lsS2S.size() > 0) {
				for (String2Struct s2s : lsS2S) {
					sb.append(s2s.s1);
					sb.append('=');
					encodeUTF(sb, s2s.s2);
					//sb.append(s2s.s2);
					sb.append('&');
				}
				sb.setLength(sb.length() - 1);
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	
	private void encodeSpecialKey(StringBuilder sb, char c) {
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
	public void encodeUTF(StringBuilder sb, String sData) throws UnsupportedEncodingException {
		char[] lsChar = sData.toCharArray();
		StringBuilder sbChinese = new StringBuilder();
		for (int i = 0; i < lsChar.length; i++) {
			if (lsChar[i] < 0xff) {
				if (sbChinese.length() > 0) {
					sb.append(URLEncoder.encode(sbChinese.toString(), ConstData.sHttpCharset));
					sbChinese.setLength(0);
				}
				encodeSpecialKey(sb, lsChar[i]);
			} else {
				sbChinese.append(lsChar[i]);
			}
		}
		if (sbChinese.length() > 0) {
			sb.append(URLEncoder.encode(sbChinese.toString(), ConstData.sHttpCharset));
		}
	}
}
