package com.china.fortune.http.webservice.servlet;

import java.util.ArrayList;
import java.util.HashMap;

import com.china.fortune.http.PairBuilder;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.string.StringUtils;

public class CheckKeys {
	protected String[] lsKey = null;

	public boolean contain(String sKey) {
		return find(sKey) >= 0;
	}

	public void remove(String sKey) {
		int index = find(sKey);
		if (index >= 0) {
			String[] lsTmpKey = new String[lsKey.length - 1];
			int j = 0;
			for (int i = 0; i < lsKey.length; i++) {
				if (i != index) {
					lsTmpKey[j++] = lsKey[i];
				}
			}
			lsKey = lsTmpKey;
		}
	}

	public String[] getAll() {
		return lsKey;
	}

	public String get(int i) {
		if (lsKey != null && i >= 0 && i < lsKey.length) {
			return lsKey[i];
		}
		return null;
	}
	
	public String getKey(int i) {
		if (lsKey != null && i >= 0 && i < lsKey.length) {
			return lsKey[i];
		}
		return null;
	}

	public void append(String[] key1) {
		if (key1 != null) {
			lsKey = StringUtils.appendStrings(lsKey, key1);
		}
	}

	public void append(Class<?> o) {
		ArrayList<String> lsKey = ClassUtils.getFieldName(o);
		append(StringUtils.arrayListToStrings(lsKey));
	}

	public void append(Class<?> o, int count) {
		ArrayList<String> alKey = ClassUtils.getFieldName(o);
		while (alKey.size() > count) {
			alKey.remove(alKey.size() - 1);
		}
		append(StringUtils.arrayListToStrings(alKey));
	}

	public int size() {
		if (lsKey != null) {
			return lsKey.length;
		} else {
			return 0;
		}
	}

	public int find(String sKey) {
		if (lsKey != null) {
			for (int i = 0; i < lsKey.length; i++) {
				if (sKey.equals(lsKey[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public String toParam() {
		PairBuilder pb = new PairBuilder();
		if (lsKey != null) {
			for (int i = 0; i < lsKey.length; i++) {
				pb.add(lsKey[i], null);
			}
		}
		return pb.toString();
	}
	
	public String toJson() {
		JSONObject json = new JSONObject();
		if (lsKey != null) {
			for (int i = 0; i < lsKey.length; i++) {
				json.put(lsKey[i], "");
			}
		}
		return json.toString();
	}
	
	public int checkNull(JSONObject json) {
		if (lsKey != null && json != null) {
			for (int i = 0; i < lsKey.length; i++) {
				Object o = json.opt(lsKey[i]);
				if (o == null) {
					return i;
				} else if (o instanceof String) {
					if (StringUtils.length((String)(o)) == 0) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	public int checkNull(HashMap<String, String> map) {
		if (lsKey != null && map != null) {
			for (int i = 0; i < lsKey.length; i++) {
				if (StringUtils.length(map.get(lsKey[i])) == 0) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int checkNull(String[] lsValues) {
		if (lsKey != null && lsValues != null) {
			for (int i = 0; i < lsKey.length; i++) {
				if (StringUtils.length(lsValues[i]) == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public CheckKeys clone() {
        CheckKeys ck = new CheckKeys();
        if (lsKey != null && lsKey.length > 0) {
            ck.lsKey = new String[lsKey.length];
            for (int i = 0; i < lsKey.length; i++) {
                ck.lsKey[i] = lsKey[i];
            }
        }
        return ck;
    }
}
