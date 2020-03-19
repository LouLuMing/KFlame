package com.china.fortune.reflex;

import com.china.fortune.global.Log;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.struct.FastList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClassSerializeJson {
	private HashMap<String, Class<?>> mapClass = new HashMap<String, Class<?>>();

	private Class<?> getClass(JSONObject json) {
		String sClass = json.optString("class");
		if (sClass != null) {
			return mapClass.get(sClass);
		} else {
			return null;
		}
	}

	private void setClass(JSONObject json, Class<?> cls) {
		String sClassName = cls.getName();
		json.put("class", sClassName);
		mapClass.put(sClassName, cls);
	}

	private void setField(Object o, Field f, JSONObject json) {
		String sKey = f.getName();
		try {
			Class<?> cType = f.getType();
			if (cType == String.class) {
				f.set(o, json.optString(sKey));
			} else if (cType == Integer.class || cType == int.class) {
				f.setInt(o, json.optInt(sKey));
			} else if (cType == Long.class || cType == long.class) {
				f.setLong(o, json.optLong(sKey));
			} else if (cType == ArrayList.class) {
				ArrayList<Object> lsObj = new ArrayList<Object>();
				JSONObject al = json.optJSONObject(sKey);
				if (al != null) {
					Class<?> cls = getClass(al);
					JSONArray list = al.optJSONArray("list");
					if (list != null) {
						for (int i = 0; i < list.length(); i++) {
							Object child = list.opt(i);
							if (child != null) {
								lsObj.add(toObject(child, cls));
							}
						}
					}
				}
				f.set(o, lsObj);
			} else if (cType == FastList.class) {
				FastList<Object> lsObj = new FastList<Object>();
				JSONObject al = json.optJSONObject(sKey);
				if (al != null) {
					Class<?> cls = getClass(al);
					JSONArray list = al.optJSONArray("list");
					if (list != null) {
						for (int i = 0; i < list.length(); i++) {
							Object child = list.opt(i);
							if (child != null) {
								lsObj.add(toObject(child, cls));
							}
						}
					}
				}
				f.set(o, lsObj);
			} else if (cType == HashMap.class) {
				HashMap<Object, Object> lsObj = new HashMap<>();
				JSONObject al = json.optJSONObject(sKey);
				if (al != null) {
					Class<?> cls = getClass(al);
					JSONArray key = al.optJSONArray("key");
					JSONArray value = al.optJSONArray("value");
					if (key != null && value != null) {
						for (int i = 0; i < key.length() && i < value.length(); i++) {
							JSONObject keyJson = key.optJSONObject(i);
							JSONObject valueJson = value.optJSONObject(i);
							if (keyJson != null) {
								lsObj.put(toObject(keyJson, cls), toObject(valueJson));
							}
						}
					}
				}
				f.set(o, lsObj);
			} else if (f.getType() == int[].class || f.getType() == Integer[].class) {
				JSONArray list = json.optJSONArray(sKey);
				if (list != null && list.length() > 0) {
					int[] lsObj = new int[list.length()];
					for (int i = 0; i < list.length(); i++) {
						lsObj[i] = list.optInt(i);
					}
					f.set(o, lsObj);
				}
			} else if (f.getType() == String[].class) {
				JSONArray list = json.optJSONArray(sKey);
				if (list != null && list.length() > 0) {
					String[] lsObj = new String[list.length()];
					for (int i = 0; i < list.length(); i++) {
						lsObj[i] = list.optString(i);
					}
					f.set(o, lsObj);
				}
			} else if (cType == AtomicInteger.class) {
				f.set(o, new AtomicInteger(json.optInt(sKey)));
			} else if (cType == AtomicLong.class) {
				f.set(o, new AtomicLong(json.optInt(sKey)));
			} else {
				JSONObject child = json.optJSONObject(sKey);
				if (child != null) {
					f.set(o, toObject(child));
				}
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	private void getField(Object o, Field f, JSONObject json) {
		if ((f.getModifiers() & Modifier.STATIC) == 0) {
			f.setAccessible(true);
			String sKey = f.getName();
			try {
				Object value = f.get(o);
				Class<?> cType = f.getType();
				if (cType == String.class || cType == Integer.class || cType == int.class || cType == Long.class
						|| cType == long.class) {
					json.put(sKey, value);
				} else if (cType == ArrayList.class) {
					JSONObject al = new JSONObject();
					ArrayList<Object> lsObj = (ArrayList<Object>) value;
					if (lsObj != null && lsObj.size() > 0) {
						setClass(al, lsObj.get(0).getClass());
						JSONArray jarr = new JSONArray();
						for (Object child : lsObj) {
							jarr.put(toJSONObjectOrObject(child));
						}
						al.put("list", jarr);
					}
					json.put(sKey, al);
				} else if (cType == FastList.class) {
					JSONObject al = new JSONObject();
					FastList<Object> lsObj = (FastList<Object>) value;
					if (lsObj != null && lsObj.size() > 0) {
						setClass(al, lsObj.get(0).getClass());
						JSONArray jarr = new JSONArray();
						for (int i = 0; i < lsObj.size(); i++) {
							Object child = lsObj.get(i);
							jarr.put(toJSONObjectOrObject(child));
						}
						al.put("list", jarr);
					}
					json.put(sKey, al);
				} else if (cType == HashMap.class) {
					JSONObject al = new JSONObject();
					HashMap<Object, Object> lsObj = (HashMap<Object, Object>) value;
					if (lsObj != null && lsObj.size() > 0) {
						boolean bFirst = true;
						JSONArray keyJarr = new JSONArray();
						JSONArray valueJarr = new JSONArray();
						for (Entry<Object, Object> child : lsObj.entrySet()) {
							if (bFirst) {
								bFirst = false;
								setClass(al, child.getKey().getClass());
							}
							keyJarr.put(toJSONObjectOrObject(child.getKey()));
							valueJarr.put(toJSONObjectOrObject(child.getValue()));
						}
						al.put("key", keyJarr);
						al.put("value", valueJarr);
					}
					json.put(sKey, al);
				} else if (f.getType() == int[].class || f.getType() == Integer[].class) {
					int[] lsObj = (int[]) value;
					JSONArray list = new JSONArray();
					if (list != null && list.length() > 0) {
						for (int i = 0; i < list.length(); i++) {
							list.put(lsObj[i]);
						}
					}
					json.put(sKey, list);
				} else if (f.getType() == String[].class) {
					String[] lsObj = (String[]) value;
					JSONArray list = new JSONArray();
					if (list != null && list.length() > 0) {
						for (int i = 0; i < list.length(); i++) {
							list.put(lsObj[i]);
						}
					}
					json.put(sKey, list);
				} else if (cType == AtomicInteger.class) {
					json.put(sKey, ((AtomicInteger) value).get());
				} else if (cType == AtomicLong.class) {
					json.put(sKey, ((AtomicLong) value).get());
				} else {
					json.put(sKey, toJSONObject(value));
				}

			} catch (Exception e) {
				Log.logException(e);
			}
		}
	}

	private Object toJSONObjectOrObject(Object o) {
		Class<?> c = o.getClass();
		if (c == String.class || c == Integer.class || c == int.class || c == Long.class) {
			return o;
		} else if (c == AtomicInteger.class) {
			return ((AtomicInteger) o).get();
		} else if (c == AtomicLong.class) {
			return ((AtomicLong) o).get();
		} else {
			JSONObject json = new JSONObject();
			try {
				Field[] lsFields = c.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						getField(o, f, json);
					}
				}
			} catch (Exception e) {
				Log.logClass(c.getSimpleName() + ":" + e.getMessage());
			}
			return json;
		}
	}

	public JSONObject toJSONObject(Object o) {
		Class<?> c = o.getClass();
		JSONObject json = new JSONObject();
		setClass(json, c);
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					getField(o, f, json);
				}
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
		return json;
	}

	public Object toObject(Object from, Class<?> cls) {
		Object o = null;
		if (cls == String.class || cls == Integer.class || cls == int.class || cls == Long.class) {
			return from;
		} else if (cls == AtomicInteger.class) {
			return new AtomicInteger((int) from);
		} else if (cls == AtomicLong.class) {
			return new AtomicLong((long) from);
		} else {
			JSONObject json = (JSONObject) from;
			try {
				o = cls.newInstance();
				Field[] lsFields = cls.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						setField(o, f, json);
					}
				}
			} catch (Exception e) {
				Log.logException(e);
			}
		}

		return o;
	}

	public Object toObject(JSONObject json) {
		Object o = null;
		Class<?> cls = getClass(json);
		if (cls != null) {
			o = toObject(json, cls);
		}
		return o;
	}
}
