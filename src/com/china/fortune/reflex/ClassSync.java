package com.china.fortune.reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.china.fortune.easy.Int2Struct;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class ClassSync {
	static public void syncExcept(Object src, Object des, String sExceptField) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field[] lsFields = cDes.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						if (StringAction.compareTo(sExceptField, f.getName()) != 0) {
							f.setAccessible(true);
							f.set(des, f.get(src));
						}
					}
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static public void syncExcept(Object src, Object des, String[] lsExceptField) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field[] lsFields = cDes.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						if (StringAction.findString(lsExceptField, f.getName()) < 0) {
							f.setAccessible(true);
							f.set(des, f.get(src));
						}
					}
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static public void sum(Object src, Object des) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field[] lsFields = cDes.getFields();
				for (Field f : lsFields) {
					Class<?> cType = f.getType();
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						if (cType == Integer.class || cType == int.class) {
							f.setInt(des, f.getInt(src) + f.getInt(des));
						} else if (cType == Long.class || cType == long.class) {
							f.setLong(des, f.getLong(src) + f.getLong(des));
						}
					}
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static public void sync(Object src, Object des) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field[] lsFields = cDes.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						f.set(des, f.get(src));
					}
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static public void sync(Object src, Object des, String[] lsField) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field[] lsFields = cDes.getFields();
				for (Field f : lsFields) {
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						if (StringAction.findString(lsField, f.getName()) >= 0) {
							f.setAccessible(true);
							f.set(des, f.get(src));
						}
					}
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static public void sync(Object src, Object des, String sField) {
		Class<?> cSrc = src.getClass();
		Class<?> cDes = src.getClass();
		if (cSrc.equals(cDes)) {
			try {
				Field f = cDes.getField(sField);
				if (f != null && (f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					f.set(des, f.get(src));
				}
			} catch (Exception e) {
				Log.logClassError(cSrc.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		Int2Struct i2s = new Int2Struct();
		i2s.i1 = 100;
		Int2Struct des = new Int2Struct();
		sync(i2s, des);
		Log.log(des.i1 + "");
	}

}
