package com.china.fortune.http;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.string.URLEncodeNotJava;

public class UrlBuilder extends PairBuilder {
	private String sServerURL = null;
	private String sAction = null;

	public UrlBuilder() {
	}

	public UrlBuilder(String s) {
		sServerURL = s;
	}

	public void setUrl(String s) {
		sServerURL = s;
	}

	public void setAction(String s) {
		sAction = s;
	}

	public String toStringNoUrlDecode() {
		StringBuilder sb = new StringBuilder();
		sb.append(sServerURL);
		if (sAction != null) {
			sb.append(sAction);
		}
		if (lsS2S.size() > 0) {
			sb.append('?');
			for (String2Struct s2s : lsS2S) {
				sb.append(s2s.s1);
				sb.append('=');
				if (s2s.s2 != null) {
					sb.append(s2s.s2);
				}
				sb.append('&');
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sServerURL);
		if (sAction != null) {
			sb.append(sAction);
		}
		if (lsS2S.size() > 0) {
			sb.append('?');
			for (String2Struct s2s : lsS2S) {
				sb.append(s2s.s1);
				sb.append('=');
				if (s2s.s2 != null) {
					URLEncodeNotJava.encode(sb, s2s.s2, "utf-8");
				}
				sb.append('&');
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

}
