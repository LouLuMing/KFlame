package com.china.fortune.json;

import com.china.fortune.struct.FastList;

public class JSONArray extends JSONBase {
	private FastList<Object> lsChild = new FastList<Object>();
	
	public Object remove(int i) {
		return lsChild.remove(i);
	}
	
	public void copy(JSONArray from) {
		if (from != null) {
            for (int i = 0; i < from.lsChild.size(); i++) {
                lsChild.add(copy(from.lsChild.get(i)));
            }
//			for (Object o : from.lsChild) {
//				lsChild.add(copy(o));
//			}
		}
	}
	
	public void set(int i, int iValue) {
		lsChild.set(i, new Integer(iValue));
	}
	
	public void put(Object o) {
		lsChild.add(o);
	}

	public void set(int i, Object o) {
		lsChild.set(i, o);
	}
	
	public void put(int i, Object o) {
		lsChild.add(i, o);
	}

	public void put(String sValue) {
		lsChild.add(sValue);
	}

	public void put(int iValue) {
		lsChild.add(new Integer(iValue));
	}
	
	public void put(long lValue) {
		lsChild.add(new Long(lValue));
	}

	public void put(boolean value) {
		lsChild.add(value ? Boolean.TRUE : Boolean.FALSE);
	}

	public float optFloat(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optFloat(lsChild.get(index));
		}
		return 0;
	}

	public double optDouble(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optDouble(lsChild.get(index));
		}
		return 0;
	}

	public int optInt(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optInt(lsChild.get(index));
		}
		return 0;
	}

	public long optLong(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optLong(lsChild.get(index));
		}
		return 0;
	}

	public String optString(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optString(lsChild.get(index));
		}
		return null;
	}

	public boolean optBoolean(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return JSONString.isBoolean(lsChild.get(index));
		}
		return false;
	}

	public JSONObject optJSONObject(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optJSONObject(lsChild.get(index));
		}
		return null;
	}

	public JSONArray optJSONArray(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return optJSONArray(lsChild.get(index));
		}
		return null;
	}
	
	public Object opt(int index) {
		if (index >= 0 && index < lsChild.size()) {
			return lsChild.get(index);
		}
		return null;
	}

	public JSONArray() {
	}

	public JSONArray(String sJson) {
		if (sJson != null) {
			JSONStateMachine jsonSM = new JSONStateMachine();
			jsonSM.parseJSONArray(sJson, this);
		}
	}

	public int length() {
		return lsChild.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		int iCount = 0;
		sb.append('[');
//		for (Object o : lsChild) {
//			if (o != null) {
//				iCount++;
//				assembleString(sb, o);
//			}
//		}
        for (int i = 0; i < lsChild.size(); i++) {
            Object o = lsChild.get(i);
            if (o != null) {
                iCount++;
                toString(sb, o);
            }
        }
		if (iCount > 0) {
			sb.setCharAt(sb.length() - 1, ']');
		} else {
			sb.append(']');
		}
	}

	public void toYaml(StringBuilder sb, int space) {
		for (int i = 0; i < lsChild.size(); i++) {
			Object o = lsChild.get(i);
			if (o != null) {
				for (int j = 0; j < space; j++) {
					sb.append(' ');
				}
				sb.append('-');
				toYaml(sb, space, o);
			}
		}
	}
}
