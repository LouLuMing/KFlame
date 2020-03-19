package com.china.fortune.json;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.china.fortune.reflex.ClassJson;

public class JSONObject extends JSONBase {
	private HashMap<String, Object> mapItems = new HashMap<String, Object>();

	static public JSONObject fromObject(Object o) {
		return ClassJson.toJSONObject(o);
	}
	
	public Set<String> keySet() {
		return mapItems.keySet();
	}
	
	public int size() {
		return mapItems.size();
	}
	
	public void append(JSONObject from) {
		if (from != null) {
			mapItems.putAll(from.mapItems);
		}
	}

	public void copy(JSONObject from) {
		if (from != null) {
			for (Entry<String, Object> en : from.mapItems.entrySet()) {
				mapItems.put(en.getKey(), copy(en.getValue()));
			}
		}
	}
	
	public void clone(JSONObject from) {
		if (from != null) {
			mapItems = from.mapItems;
		}
	}

	public void removeAll() {
		mapItems.clear();
	}

	public JSONArray removeJSONArray(String key) {
		Object o = mapItems.remove(key);
		if (o != null) {
			return optJSONArray(o);
		}
		return null;
	}

	public long removeLong(String key) {
		Object o = mapItems.remove(key);
		if (o != null) {
			return optLong(o);
		}
		return 0;
	}
	
	public int removeInt(String key) {
		Object o = mapItems.remove(key);
		if (o != null) {
			return optInt(o);
		}
		return 0;
	}

	public String removeString(String key) {
		Object o = mapItems.remove(key);
		if (o != null) {
			return optString(o);
		}
		return null;
	}

	public int getInt(String key) {
		return optInt(key);
	}
	
	public int optInt(String key) {
		return optInt(mapItems.get(key));
	}

	public long getLong(String key) {
		return optLong(key);
	}
	
	public long optLong(String key) {
		return optLong(mapItems.get(key));
	}

	public String getString(String key) {
		return optString(key);
	}
	
	public String optString(String key) {
		return optString(mapItems.get(key));
	}

	public boolean getBoolean(String key) {
		return optBoolean(key);
	}
	
	public boolean optBoolean(String key) {
		return JSONString.isBoolean(mapItems.get(key));
	}

	public JSONArray getJSONArray(String key) {
		return optJSONArray(key);
	}
	
	public JSONArray optJSONArray(String key) {
		return optJSONArray(mapItems.get(key));
	}

	public JSONObject optJSONObject(String key) {
		return optJSONObject(mapItems.get(key));
	}

	public Object opt(String key) {
		return mapItems.get(key);
	}

	public boolean has(String key) {
		return mapItems.containsKey(key);
	}

	public void put(String sKey, String sValue) {
		mapItems.put(sKey, sValue);
	}

	public void put(String sKey, int iValue) {
		mapItems.put(sKey, new Integer(iValue));
	}

	public void put(String sKey, long lValue) {
		mapItems.put(sKey, new Long(lValue));
	}

	public void put(String key, boolean value) {
		mapItems.put(key, value ? Boolean.TRUE : Boolean.FALSE);
	}

	public void put(String sKey, Object oValue) {
		mapItems.put(sKey, oValue);
	}

	public Object remove(String sKey) {
		return mapItems.remove(sKey);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		int iCount = 0;
		sb.append('{');
		for (Entry<String, Object> so : mapItems.entrySet()) {
			Object o = so.getValue();
			if (o != null) {
				iCount++;
				sb.append('"');
				sb.append(so.getKey());
				sb.append("\":");
				toString(sb, o);
			}
		}
		if (iCount > 0) {
			sb.setCharAt(sb.length() - 1, '}');
		} else {
			sb.append('}');
		}
	}

	public void stringToJSONArray(String sKey) {
		String sValue = optString(sKey);
		if (sValue != null) {
			put(sKey, new JSONArray(sValue));
		}
	}

	public JSONObject() {
	}

	public JSONObject(String sJson) {
		if (sJson != null) {
			JSONStateMachine jsonSM = new JSONStateMachine();
			jsonSM.parseJSONObject(sJson, this);
		}
	}

	public void parseJSONObject(String sJson) {
		if (sJson != null) {
			JSONStateMachine jsonSM = new JSONStateMachine();
			jsonSM.parseJSONObject(sJson, this);
		}
	}
}
