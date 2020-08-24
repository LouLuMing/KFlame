package com.china.fortune.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;

// "key,name,value";
public class JSONUtils {
	static public HashSet<Integer> toHashSet(JSONArray ja, String sKey) {
		HashSet<Integer> lsObj = new HashSet<Integer>();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject json = ja.optJSONObject(i);
			lsObj.add(json.optInt(sKey));
		}
		return lsObj;
	}

	static public HashSet<String> toStringHashSet(JSONArray ja, String sKey) {
		HashSet<String> lsObj = new HashSet<String>();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject json = ja.optJSONObject(i);
			lsObj.add(json.optString(sKey));
		}
		return lsObj;
	}

	static public ArrayList<Integer> toArrayList(JSONArray ja, String sKey) {
		ArrayList<Integer> lsObj = new ArrayList<Integer>();
		for (int i = 0; i < ja.length(); i++) {
			JSONObject json = ja.optJSONObject(i);
			lsObj.add(json.optInt(sKey));
		}
		return lsObj;
	}
	
	static public ArrayList<String> toArrayList(JSONObject jsob, String[] lsKey) {
		int iKey = lsKey.length;
		ArrayList<String> lsObj = new ArrayList<String>();
		for (int i = 0; i < iKey; i++) {
			lsObj.add(jsob.optString(lsKey[i]));
		}
		return lsObj;
	}

	static public ArrayList<ArrayList<String>> toArrayList(JSONArray ja, String[] lsKey) {
		ArrayList<ArrayList<String>> lsObj = new ArrayList<ArrayList<String>>();
		toArrayList(lsObj, ja, lsKey);
		return lsObj;
	}

	static public void toArrayList(ArrayList<ArrayList<String>> lsObj, JSONArray ja, String[] lsKey) {
		int iKey = lsKey.length;
		for (int i = 0; i < ja.length(); i++) {
			JSONObject jsob = ja.optJSONObject(i);
			ArrayList<String> item = new ArrayList<String>();
			for (int j = 0; j < iKey; j++) {
				item.add(jsob.optString(lsKey[i]));
			}
			lsObj.add(item);
		}
	}

	static public HashMap<String, String> toHashMap(JSONObject jsob, String[] lsKey) {
		int iKey = lsKey.length;
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < iKey; i++) {
			map.put(lsKey[i], jsob.optString(lsKey[i]));
		}
		return map;
	}

	static public ArrayList<HashMap<String, String>> toHashMap(JSONArray ja, String[] lsKey) {
		ArrayList<HashMap<String, String>> lsObj = new ArrayList<HashMap<String, String>>();
		toHashMap(lsObj, ja, lsKey);
		return lsObj;
	}

	static public HashMap<Integer, JSONObject> toHashMap(JSONArray jarr, String sKey) {
		HashMap<Integer, JSONObject> mapJson = new HashMap<Integer, JSONObject>();
		for (int i = 0; i < jarr.length(); i++) {
			JSONObject item = jarr.optJSONObject(i);
			mapJson.put(item.optInt(sKey), item);
		}
		return mapJson;
	}

	static public HashMap<Long, JSONObject> toHashMapLong(JSONArray jarr, String sKey) {
		HashMap<Long, JSONObject> mapJson = new HashMap<Long, JSONObject>();
		for (int i = 0; i < jarr.length(); i++) {
			JSONObject item = jarr.optJSONObject(i);
			mapJson.put(item.optLong(sKey), item);
		}
		return mapJson;
	}

	static public HashMap<String, JSONObject> toHashMapString(JSONArray jarr, String sKey) {
		HashMap<String, JSONObject> mapJson = new HashMap<String, JSONObject>();
		for (int i = 0; i < jarr.length(); i++) {
			JSONObject item = jarr.optJSONObject(i);
			mapJson.put(item.optString(sKey), item);
		}
		return mapJson;
	}

	static public void toHashMap(ArrayList<HashMap<String, String>> lsObj, JSONArray ja, String[] lsKey) {
		int iKey = lsKey.length;
		for (int i = 0; i < ja.length(); i++) {
			JSONObject jsob = ja.optJSONObject(i);
			HashMap<String, String> map = new HashMap<String, String>();
			for (int j = 0; j < iKey; j++) {
				map.put(lsKey[j], jsob.optString(lsKey[j]));
			}
			lsObj.add(map);
		}
	}

	static public JSONObject toJSONObject(String sJson) {
		JSONObject json = null;
		try {
			json = new JSONObject(sJson);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return json;
	}

	static public JSONArray toJSONArray(HashMap<String, String> map, String sKey, String sValue) {
		JSONArray ja = new JSONArray();
		for (Entry<String, String> en : map.entrySet()) {
			JSONObject json = new JSONObject();
			json.put(sKey, en.getKey());
			json.put(sValue, en.getValue());
			ja.put(json);
		}
		return ja;
	}

	static public JSONArray toJSONArray(String sJson) {
		JSONArray ja = null;
		try {
			ja = new JSONArray(sJson);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return ja;
	}

	static public void put(JSONObject json, String key, Object o) {
		try {
			json.put(key, o);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	static public void sortReverse(JSONArray jarr) {
		if (jarr != null) {
			int iEnd = jarr.length() - 1;
			try {
				for (int i = 0; i < jarr.length() / 2; i++) {
					Object head = jarr.opt(i);
					Object tail = jarr.opt(iEnd - i);
					jarr.set(i, tail);
					jarr.set(iEnd - i, head);
				}
			} catch (Exception e) {
				Log.logException(e);
			}
		}
	}
	
	static public void sortIntAsc(JSONArray jarr, String sKey) {
		if (jarr != null) {
			for (int i = 0; i < jarr.length(); i++) {
				for (int j = i + 1; j < jarr.length(); j++) {
					JSONObject head = jarr.optJSONObject(i);
					JSONObject tail = jarr.optJSONObject(j);
					int iHead = head.optInt(sKey);
					int iTail = tail.optInt(sKey);
					if (iHead > iTail) {
						jarr.set(i, tail);
						jarr.set(j, head);
					}
				}
			}
		}
	}

	static public void sortIntDesc(JSONArray jarr, String sKey) {
		if (jarr != null) {
			for (int i = 0; i < jarr.length(); i++) {
				for (int j = i + 1; j < jarr.length(); j++) {
					JSONObject head = jarr.optJSONObject(i);
					JSONObject tail = jarr.optJSONObject(j);
					int iHead = head.optInt(sKey);
					int iTail = tail.optInt(sKey);
					if (iHead < iTail) {
						jarr.set(i, tail);
						jarr.set(j, head);
					}
				}
			}
		}
	}
	
	static public int sumInt(JSONArray jarr, String sKey) {
		int sum = 0;
		if (jarr != null) {
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject head = jarr.optJSONObject(i);
				sum += head.optInt(sKey);
			}
		}
		return sum;
	}
	
	static public void calIncrease(JSONArray jarr, String sKey, String sNewKey) {
		if (jarr != null) {
			for (int i = 1; i < jarr.length(); i++) {
				JSONObject now = jarr.optJSONObject(i - 1);
				JSONObject next = jarr.optJSONObject(i);
				now.put(sNewKey, now.optInt(sKey) - next.optInt(sKey));
			}
		}
	}
	
	static public void calIncrease(JSONArray jarr, ArrayList<String2Struct> lsKeys) {
		if (jarr != null) {
			for (int i = 1; i < jarr.length(); i++) {
				JSONObject now = jarr.optJSONObject(i - 1);
				JSONObject next = jarr.optJSONObject(i);
				for (String2Struct s2s : lsKeys) {
					now.put(s2s.s1, now.optInt(s2s.s2) - next.optInt(s2s.s2));
				}
			}
		}
	}

	static public JSONObject getObjectInt(JSONArray list, String key, int value) {
		for (int i = 0; i < list.length(); i++) {
			JSONObject json = list.optJSONObject(i);
			if (json.optInt(key) == value) {
				return json;
			}
		}
		return null;
	}

	protected void sumJSONObject(JSONObject des, JSONObject src, String sKey) {
		for (String key : des.keySet()) {
			if (StringUtils.compareTo(key, sKey) != 0) {
				des.put(key, des.optInt(key) + src.optInt(key));
			}
		}
	}

	static public JSONObject toJSONObject(HashMap<String, String> map) {
		JSONObject data = new JSONObject ();
		for (Entry<String, String> en : map.entrySet()) {
			data.put(en.getKey(), en.getValue());
		}
		return data;
	}

	static public void putMap(JSONObject data, HashMap<String, String> map) {
		for (Entry<String, String> en : map.entrySet()) {
			data.put(en.getKey(), en.getValue());
		}
	}

	static public JSONObject optParentJSONObject(JSONObject data, String[] lsTag) {
		for (int i = 0; i < lsTag.length - 1; i++) {
			if (data != null) {
				data = data.optJSONObject(lsTag[i]);
			} else {
				break;
			}
		}
		return data;
	}

	static public int optInt(JSONObject data, String sTag) {
		String[] lsTag = StringUtils.split(sTag, '.');
		data = optParentJSONObject(data, lsTag);
		if (data != null) {
			return data.optInt(lsTag[lsTag.length - 1]);
		} else {
			return 0;
		}
	}

	static public String optString(JSONObject data, String sTag) {
		String[] lsTag = StringUtils.split(sTag, '.');
		data = optParentJSONObject(data, lsTag);
		if (data != null) {
			return data.optString(lsTag[lsTag.length - 1]);
		} else {
			return null;
		}
	}

	static public JSONArray optJSONArray(JSONObject data, String sTag) {
		String[] lsTag = StringUtils.split(sTag, '.');
		data = optParentJSONObject(data, lsTag);
		if (data != null) {
			return data.optJSONArray(lsTag[lsTag.length - 1]);
		} else {
			return null;
		}
	}

	static public Object opt(JSONObject data, String sTag) {
		String[] lsTag = StringUtils.split(sTag, '.');
		data = optParentJSONObject(data, lsTag);
		if (data != null) {
			return data.opt(lsTag[lsTag.length - 1]);
		} else {
			return null;
		}
	}
}
