package com.china.fortune.http;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.global.Log;

public class PairBuilder {
	protected ArrayList<String2Struct> lsS2S = new ArrayList<String2Struct>();

	public int size() {
		return lsS2S.size();
	}

	public void clear() {
		lsS2S.clear();
	}

	public void add(String s1, String s2) {
		if (s1 != null) {
			lsS2S.add(new String2Struct(s1, s2));
		}
	}

	public void add(String s1, int s2) {
		if (s1 != null) {
			lsS2S.add(new String2Struct(s1, String.valueOf(s2)));
		}
	}

	public void add(String s1, long s2) {
		if (s1 != null) {
			lsS2S.add(new String2Struct(s1, String.valueOf(s2)));
		}
	}

    public void add(Class<?> c) {
        if (c != null) {
            try {
                Field[] lsFields = c.getFields();
                if (lsFields != null) {
                    for (Field f : lsFields) {
                        if ((f.getModifiers() & Modifier.STATIC) == 0) {
                            f.setAccessible(true);
                            String sKey = f.getName();
                            add(sKey, null);
                        }
                    }
                }
            } catch (Exception e) {
                Log.logClass(c.getSimpleName() + ":" + e.getMessage());
            }
        }
    }

	public void add(Object obj) {
        if (obj != null) {
            Class<?> c = obj.getClass();
            try {
                Field[] lsFields = c.getFields();
                if (lsFields != null) {
                    for (Field f : lsFields) {
                        if ((f.getModifiers() & Modifier.STATIC) == 0) {
                            f.setAccessible(true);
                            String sKey = f.getName();
                            Class<?> cType = f.getType();
                            try {
                                if (cType == String.class) {
                                    add(sKey, (String) f.get(obj));
                                } else if (cType == Integer.class || cType == int.class) {
                                    add(sKey, f.getInt(obj));
                                } else if (cType == Long.class || cType == long.class) {
                                    add(sKey, f.getLong(obj));
                                }
                            } catch (Exception e) {
                                Log.logException(e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.logClass(c.getSimpleName() + ":" + e.getMessage());
            }
        }
    }

	public String toString() {
		return toString('&');
	}

	public String toString(char cSpan) {
		StringBuilder sb = new StringBuilder();
		if (lsS2S.size() > 0) {
			for (String2Struct s2s : lsS2S) {
				sb.append(s2s.s1);
				sb.append('=');
				if (s2s.s2 != null) {
					sb.append(s2s.s2);
				}
				sb.append(cSpan);
			}
			return sb.substring(0, sb.length() - 1);
		} else {
			return null;
		}
	}
}
