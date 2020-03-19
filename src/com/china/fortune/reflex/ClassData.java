package com.china.fortune.reflex;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

import java.lang.reflect.Field;

public abstract class ClassData<E> {
	abstract String getString(E data, String sKey);
	abstract long getLong(E data, String sKey);
	abstract int getInt(E data, String sKey);

	abstract void setString(E data, String sKey, String sValue);
	abstract void setLong(E data, String sKey, long lValue);
	abstract void setInt(E data, String sKey, int iValue);

	protected void setField(Object obj, Field f, E data) {
		String sKey = f.getName();
		try {
			Class<?> cType = f.getType();
			if (cType == String.class) {
				f.set(obj, getString(data, sKey));
			} else if (cType == Long.class || cType == long.class) {
				f.set(obj, getLong(data, sKey));
			} else if (cType == Integer.class || cType == int.class) {
				f.set(obj, getInt(data, sKey));
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	protected void getField(Object obj, Field f, E data) {
		String sKey = f.getName();
		try {
			Class<?> cType = f.getType();
			if (cType == String.class) {
				setString(data, sKey, (String) f.get(obj));
			} else if (cType == Long.class || cType == long.class) {
				setLong(data, sKey, f.getLong(obj));
			} else if (cType == Integer.class || cType == int.class) {
				setInt(data, sKey, f.getInt(obj));
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public void toData(Object o, E data) {
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				getField(o, f, data);
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
	}

	public void toData(Object o, E data, String[] lsField) {
		Class<?> c = o.getClass();
		try {
			for (String sField : lsField) {
				Field f = c.getField(sField);
				if (f != null) {
					getField(o, f, data);
				}
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
	}

	public void toDataExclude(Object o, E data, String[] lsField) {
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				String sKey = f.getName();
				if (StringAction.findString(lsField, sKey) < 0) {
					getField(o, f, data);
				}
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
	}

	public void toClass(E data, Object o, String[] lsField) {
		Class<?> c = o.getClass();
		try {
			for (String sField : lsField) {
				Field f = c.getField(sField);
				if (f != null) {
					setField(o, f, data);
				}
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public void toClass(E data, Object o) {
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				setField(o, f, data);
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}
}
