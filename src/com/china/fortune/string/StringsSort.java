package com.china.fortune.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.china.fortune.json.JSONObject;
import com.china.fortune.string.StringAction;

public class StringsSort {
	
	public static String toASC(HashMap<String, String> map) {
		StringBuilder sb = new StringBuilder();

		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = map.get(key);
			if (StringAction.length(value) > 0) {
				sb.append(key);
				sb.append('=');
				sb.append(value);
				sb.append('&');
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public static String toASC(JSONObject jsonObject) {
		StringBuilder sb = new StringBuilder();

		List<String> keys = new ArrayList<String>(jsonObject.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = jsonObject.optString(key);
			if (StringAction.length(value) > 0) {
				sb.append(key);
				sb.append('=');
				sb.append(value);
				sb.append('&');
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}
