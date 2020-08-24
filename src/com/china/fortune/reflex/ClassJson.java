package com.china.fortune.reflex;

import com.china.fortune.global.Log;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClassJson {
    static public Class<?> getClass(JSONObject json) {
        String sClass = json.optString("class");
        if (sClass != null) {
            return ClassUtils.getClass(sClass);
        } else {
            return null;
        }
    }

    static public void setClass(JSONObject json, Class<?> cls) {
        String sClassName = cls.getName();
        json.put("class", sClassName);
    }

    static public void setFieldToObject(Object o, Field f, JSONObject json) {
        String sKey = f.getName();
        try {
            Class<?> cType = f.getType();
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                if (cType == String.class) {
                    f.set(o, json.optString(sKey));
                } else if (cType == Integer.class || cType == int.class) {
                    f.setInt(o, json.optInt(sKey));
                } else if (cType == Long.class || cType == long.class) {
                    f.setLong(o, json.optLong(sKey));
                } else if (cType == AtomicInteger.class) {
                    f.set(o, new AtomicInteger(json.optInt(sKey)));
                } else if (cType == AtomicLong.class) {
                    f.set(o, new AtomicLong(json.optInt(sKey)));
                } else if (f.getType() == int[].class || f.getType() == Integer[].class) {
                    JSONArray list = json.optJSONArray(sKey);
                    if (list != null && list.length() > 0) {
                        Object co = f.get(o);
                        int[] lsObj = null;
                        if (co == null) {
                            lsObj = new int[list.length()];
                            f.set(o, lsObj);
                        } else {
                            lsObj = (int[]) co;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            lsObj[i] = list.optInt(i);
                        }
                    }
                } else if (f.getType() == long[].class || f.getType() == Long[].class) {
                    JSONArray list = json.optJSONArray(sKey);
                    if (list != null && list.length() > 0) {
                        Object co = f.get(o);
                        long[] lsObj = null;
                        if (co == null) {
                            lsObj = new long[list.length()];
                            f.set(o, lsObj);
                        } else {
                            lsObj = (long[]) co;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            lsObj[i] = list.optLong(i);
                        }
                    }
                } else if (f.getType() == String[].class) {
                    JSONArray list = json.optJSONArray(sKey);
                    if (list != null && list.length() > 0) {
                        Object co = f.get(o);
                        String[] lsObj = null;
                        if (co == null) {
                            lsObj = new String[list.length()];
                            f.set(o, lsObj);
                        } else {
                            lsObj = (String[]) co;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            lsObj[i] = list.optString(i);
                        }
                    }
                } else if (f.getType() == ArrayList.class) {
                    JSONArray list = json.optJSONArray(sKey);
                    if (list != null && list.length() > 0) {
                        Type type = f.getGenericType();
                        ParameterizedType ptype = (ParameterizedType)type;
                        Class<?> cls = (Class)ptype.getActualTypeArguments()[0];

                        Object co = f.get(o);
                        ArrayList<Object> lsObj = null;
                        if (co == null) {
                            lsObj = new ArrayList<>();
                            f.set(o, lsObj);
                        } else {
                            lsObj = (ArrayList<Object>) co;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            lsObj.add(listToObject(list.opt(i), cls));
                        }
                    }
                } else if (f.getType() == FastList.class) {
                    JSONArray list = json.optJSONArray(sKey);
                    if (list != null && list.length() > 0) {
                        Type type = f.getGenericType();
                        ParameterizedType ptype = (ParameterizedType)type;
                        Class<?> cls = (Class)ptype.getActualTypeArguments()[0];

                        Object co = f.get(o);
                        FastList<Object> lsObj = null;
                        if (co == null) {
                            lsObj = new FastList<Object>();
                            f.set(o, lsObj);
                        } else {
                            lsObj = (FastList<Object>) co;
                        }
                        for (int i = 0; i < list.length(); i++) {
                            lsObj.add(listToObject(list.opt(i), cls));
                        }
                    }
                } else if (f.getType() == HashMap.class) {
                    JSONObject map = json.optJSONObject(sKey);
                    if (map != null) {
                        JSONArray keys = map.optJSONArray("keys");
                        JSONArray values = map.optJSONArray("values");
                        if (keys != null && keys.length() > 0 && values != null && values.length() > 0) {
                            Type type = f.getGenericType();
                            ParameterizedType ptype = (ParameterizedType) type;
                            Class<?> clsKey = (Class) ptype.getActualTypeArguments()[0];
                            Class<?> clsValue = (Class) ptype.getActualTypeArguments()[1];

                            Object co = f.get(o);
                            HashMap<Object, Object> lsObj = null;
                            if (co == null) {
                                lsObj = new HashMap<Object, Object>();
                                f.set(o, lsObj);
                            } else {
                                lsObj = (HashMap<Object, Object>) co;
                            }
                            for (int i = 0; i < keys.length() && i < values.length(); i++) {
                                lsObj.put(listToObject(keys.opt(i), clsKey)
                                        , listToObject(values.opt(i), clsValue));
                            }
                        }
                    }
                }else {
                    Object co = f.get(o);
                    if (co == null) {
                        co = cType.newInstance();
                        toObject(json.optJSONObject(sKey), co);
                        f.set(o, co);
                    } else {
                        toObject(json.optJSONObject(sKey), co);
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static public void getFieldToJson(Object o, Field f, JSONObject json) {
        if ((f.getModifiers() & Modifier.STATIC) == 0) {
            f.setAccessible(true);
            try {
                Class<?> cType = f.getType();
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    String sKey = f.getName();
                    Object oValue = f.get(o);
                    if (cType == String.class
                            || cType == Integer.class || cType == int.class
                            || cType == Long.class || cType == long.class) {
                        json.put(sKey, oValue);
                    } else if (cType == AtomicInteger.class) {
                        json.put(sKey, ((AtomicInteger) oValue).get());
                    } else if (cType == AtomicLong.class) {
                        json.put(sKey, ((AtomicLong) oValue).get());
                    } else if (f.getType() == int[].class || f.getType() == Integer[].class) {
                        int[] lsObj = (int[]) oValue;
                        JSONArray list = new JSONArray();
                        if (lsObj != null && lsObj.length > 0) {
                            for (int i = 0; i < lsObj.length; i++) {
                                list.put(lsObj[i]);
                            }
                        }
                        json.put(sKey, list);
                    } else if (f.getType() == long[].class || f.getType() == Long[].class) {
                        long[] lsObj = (long[]) oValue;
                        JSONArray list = new JSONArray();
                        if (lsObj != null && lsObj.length > 0) {
                            for (int i = 0; i < lsObj.length; i++) {
                                list.put(lsObj[i]);
                            }
                        }
                        json.put(sKey, list);
                    } else if (f.getType() == String[].class) {
                        String[] lsObj = (String[]) oValue;
                        JSONArray list = new JSONArray();
                        if (lsObj != null && lsObj.length > 0) {
                            for (int i = 0; i < lsObj.length; i++) {
                                list.put(lsObj[i]);
                            }
                        }
                        json.put(sKey, list);
                    } else if (f.getType() == ArrayList.class) {
                        ArrayList<?> lsObj = (ArrayList<?>) oValue;
                        JSONArray list = new JSONArray();
                        if (lsObj != null && lsObj.size() > 0) {
                            for (int i = 0; i < lsObj.size(); i++) {
                                list.put(listtoJSONObject(lsObj.get(i)));
                            }
                        }
                        json.put(sKey, list);
                    } else if (f.getType() == FastList.class) {
                        FastList<?> lsObj = (FastList<?>) oValue;
                        JSONArray list = new JSONArray();
                        if (lsObj != null && lsObj.size() > 0) {
                            for (int i = 0; i < lsObj.size(); i++) {
                                list.put(listtoJSONObject(lsObj.get(i)));
                            }
                        }
                        json.put(sKey, list);
                    } else if (f.getType() == HashMap.class) {
                        HashMap<?, ?> lsObj = (HashMap<?, ?>) oValue;
                        JSONArray keys = new JSONArray();
                        JSONArray values = new JSONArray();
                        if (lsObj != null && lsObj.size() > 0) {
                            for (Map.Entry<?,?> et : lsObj.entrySet()) {
                                keys.put(listtoJSONObject(et.getKey()));
                                values.put(listtoJSONObject(et.getValue()));
                            }
                        }
                        JSONObject map = new JSONObject();
                        map.put("keys", keys);
                        map.put("values", values);
                        json.put(sKey, map);
                    } else {
                        json.put(sKey, toJSONObject(oValue));
                    }

                }
            } catch (Exception e) {
                Log.logException(e);
            }
        }
    }

    static public void toJSONObject(Object o, JSONObject json) {
        if (o != null && json != null) {
            Class<?> c = o.getClass();
            try {
                Field[] lsFields = c.getFields();
                for (Field f : lsFields) {
                    getFieldToJson(o, f, json);
                }
            } catch (Exception e) {
                Log.logClass(c.getSimpleName() + ":" + e.getMessage());
            }
        }
    }

    static private Object listtoJSONObject(Object o) {
        Class<?> c = o.getClass();
        if (c == String.class || c == Integer.class || c == int.class || c == Long.class) {
            return o;
        } else if (c == AtomicInteger.class) {
            return ((AtomicInteger) o).get();
        } else if (c == AtomicLong.class) {
            return ((AtomicLong) o).get();
        } else {
            JSONObject json = new JSONObject();
            toJSONObject(o, json);
            return json;
        }
    }

    static public JSONObject toJSONObject(Object o) {
        JSONObject json = new JSONObject();
        toJSONObject(o, json);
        return json;
    }

    static public JSONObject toJSONObject(Object o, String sFileds) {
        String[] lsFields = StringUtils.split(sFileds, ',');
        return toJSONObject(o, lsFields);
    }

    static public JSONObject toJSONObjectExpect(Object o, String[] lsField) {
        Class<?> c = o.getClass();
        JSONObject json = new JSONObject();
        try {
            Field[] lsFields = c.getFields();
            for (Field f : lsFields) {
                String sKey = f.getName();
                if (StringUtils.findString(lsField, sKey) < 0) {
                    getFieldToJson(o, f, json);
                }
            }
        } catch (Exception e) {
            Log.logClass(c.getSimpleName() + ":" + e.getMessage());
        }
        return json;
    }

    static public JSONObject toJSONObject(Object o, String[] lsField) {
        Class<?> c = o.getClass();
        JSONObject json = new JSONObject();
        try {
            for (String sField : lsField) {
                Field f = c.getField(sField);
                if (f != null) {
                    getFieldToJson(o, f, json);
                }
            }
        } catch (Exception e) {
            Log.logClass(c.getSimpleName() + ":" + e.getMessage());
        }
        return json;
    }

    static public void toObject(JSONObject json, Object o, String[] lsField) {
        Class<?> c = o.getClass();
        try {
            for (String sField : lsField) {
                Field f = c.getField(sField);
                if (f != null) {
                    setFieldToObject(o, f, json);
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static private Object listToObject(Object from, Class<?> cls) {
        Object o = null;
        if (cls == String.class || cls == Integer.class || cls == int.class || cls == Long.class) {
            return from;
        } else if (cls == AtomicInteger.class) {
            return new AtomicInteger((int) from);
        } else if (cls == AtomicLong.class) {
            return new AtomicLong((long) from);
        } else {
            return toObject((JSONObject) from, cls);
        }
    }

    static public <T>T toObject(JSONObject json, Class<T> cls) {
        T o = null;
        if (json != null && cls != null) {
            try {
                o = cls.newInstance();
                Field[] lsFields = cls.getFields();
                for (Field f : lsFields) {
                    setFieldToObject(o, f, json);
                }
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        return o;
    }

    static public void toObject(JSONObject json, Object o) {
        if (json != null && o != null) {
            Class<?> c = o.getClass();
            try {
                Field[] lsFields = c.getFields();
                for (Field f : lsFields) {
                    setFieldToObject(o, f, json);
                }
            } catch (Exception e) {
                Log.logException(e);
            }
        }
    }

    static public Object toObject(JSONObject json) {
        if (json != null) {
            Class<?> c = getClass(json);
            if (c != null) {
                return toObject(json, c);
            }
        }
        return null;
    }
}
