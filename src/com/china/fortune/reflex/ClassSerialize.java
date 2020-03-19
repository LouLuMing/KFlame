package com.china.fortune.reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;

public class ClassSerialize {
	static final private int ciIntType = 0;
	static final private int ciLongType = 1;
	static final private int ciStringType = 2;
	static final private int ciBooleanType = 3;

	static final private int ciArrayIntType = 10;
	static final private int ciArrayLongType = 11;
	static final private int ciArrayStringType = 12;

	static final private int ciArrayListIntType = 20;
	static final private int ciArrayListLongType = 21;
	static final private int ciArrayListStringType = 22;

	static final private int ciAtomicIntegerType = 30;

	static final private int ciOtherType = 99;

	static private Field getField(Class<?> c, String sField) {
		Field f = null;
		try {
			f = c.getField(sField);
			if (f != null) {
				f.setAccessible(true);
			}
		} catch (Exception e) {
		}
		return f;
	}

	static private void setValue(Object o, Field f, Object value) {
		if (f != null && o != null && value != null) {
			try {
				f.set(o, value);
			} catch (Exception e) {
			}
		}
	}

	static private void setAtomicInteger(Object o, Field f, int iData) {
		if (f != null && o != null) {
			try {
				Object obj = f.get(o);
				AtomicInteger ai = null;
				if (obj != null) {
					ai = (AtomicInteger) f.get(o);
					ai.set(iData);
				} else {
					ai = new AtomicInteger(iData);
					f.set(o, ai);
				}
			} catch (Exception e) {
			}
		}
	}

	static private void setInt(Object o, Field f, int value) {
		if (f != null && o != null && (f.getType() == int.class || f.getType() == Integer.class)) {
			try {
				f.setInt(o, value);
			} catch (Exception e) {
			}
		}
	}

	static private void setBoolean(Object o, Field f, int value) {
		if (f != null && o != null && (f.getType() == boolean.class || f.getType() == Boolean.class)) {
			try {
				f.setBoolean(o, value == 1);
			} catch (Exception e) {
			}
		}
	}

	static private void setLong(Object o, Field f, long value) {
		if (f != null && o != null && (f.getType() == long.class || f.getType() == Long.class)) {
			try {
				f.setLong(o, value);
			} catch (Exception e) {
			}
		}
	}

	static private Object getValue(Object o, Field f) {
		Object value = null;
		if (f != null && o != null) {
			try {
				value = f.get(o);
			} catch (Exception e) {
			}
		}
		return value;
	}

	static private int getInt(Object o, Field f) {
		int value = 0;
		if (f != null && o != null) {
			try {
				value = f.getInt(o);
			} catch (Exception e) {
			}
		}
		return value;
	}

	static private boolean getBoolean(Object o, Field f) {
		boolean value = false;
		if (f != null && o != null) {
			try {
				value = f.getBoolean(o);
			} catch (Exception e) {
			}
		}
		return value;
	}

	static private long getLong(Object o, Field f) {
		long value = 0;
		if (f != null && o != null) {
			try {
				value = f.getLong(o);
			} catch (Exception e) {
			}
		}
		return value;
	}

	static public void setObject(Object o, String sField, Object v) {
		if (o != null) {
			Class<?> c = o.getClass();
			try {
				Field f = c.getField(sField);
				if (f != null) {
					f.setAccessible(true);
					f.set(o, v);
				}
			} catch (Exception e) {
			}
		}
	}
	
	static public Object getObject(Object o, String sField) {
		Object value = null;
		if (o != null) {
			Class<?> c = o.getClass();
			try {
				Field f = c.getField(sField);
				if (f != null) {
					f.setAccessible(true);
					value = f.get(o);
				}
			} catch (Exception e) {
			}
		}
		return value;
	}

	static public void showField(Object o, String sField) {
		if (o != null) {
			Class<?> c = o.getClass();
			try {
				Field f = c.getField(sField);
				if (f != null) {
					f.setAccessible(true);
					Log.log(f.getName() + ":" + f.get(o));
				} else {
					Log.logClass(c.getSimpleName() + ":" + sField + ":miss");
				}
			} catch (Exception e) {
				Log.logClass(c.getSimpleName() + ":" + e.getMessage());
			}
		}
	}

	static private void saveNull(WriteFileAction wfa, Field f) {
		wfa.writeString(f.getName());
		wfa.writeInt(ciOtherType);
	}
	
	static private void saveField(WriteFileAction wfa, Object o, Field f) {
//		Log.logClass(o.getClass().getSimpleName() + ":" + f.getName());
		boolean rs = false;
		wfa.writeString(f.getName());
		if (f.getType() == String.class) {
			wfa.writeInt(ciStringType);
			Object value = getValue(o, f);
			if (value != null) {
				wfa.writeString((String) value);
			} else {
				wfa.writeString(null);
			}
			rs = true;
		} else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
			wfa.writeInt(ciBooleanType);
			wfa.writeInt(getBoolean(o, f) ? 1 : 0);
			rs = true;
		} else if (f.getType() == int.class || f.getType() == Integer.class) {
			wfa.writeInt(ciIntType);
			wfa.writeInt(getInt(o, f));
			rs = true;
		} else if (f.getType() == long.class || f.getType() == Long.class) {
			wfa.writeInt(ciLongType);
			wfa.writeLong(getLong(o, f));
			rs = true;
		} else if (f.getType() == int[].class || f.getType() == Integer[].class) {
			wfa.writeInt(ciArrayIntType);
			Object value = getValue(o, f);
			if (value != null) {
				wfa.writeInts((int[]) value);
			} else {
				wfa.writeInts(null);
			}
			rs = true;
		} else if (f.getType() == long[].class || f.getType() == Long[].class) {
			wfa.writeInt(ciArrayLongType);
			Object value = getValue(o, f);
			if (value != null) {
				wfa.writeLongs((long[]) value);
			} else {
				wfa.writeLongs(null);
			}
			rs = true;
		} else if (f.getType() == String[].class) {
			wfa.writeInt(ciArrayStringType);
			Object value = getValue(o, f);
			if (value != null) {
				wfa.writeStrings((String[]) value);
			} else {
				wfa.writeStrings(null);
			}
			rs = true;
		} else if (f.getType() == ArrayList.class) {
			Object value = getValue(o, f);
			ArrayList<Object> lsObj = null;
			int iSize = 0;
			if (value != null) {
				lsObj = (ArrayList<Object>) value;
				iSize = lsObj.size();
			}
			if (iSize > 0) {
				Object obj = lsObj.get(0);
				if (obj.getClass() == int.class || obj.getClass() == Integer.class) {
					wfa.writeInt(ciArrayListIntType);
					wfa.writeArrayListInteger(lsObj);
					rs = true;
				} else if (obj.getClass() == long.class || obj.getClass() == Long.class) {
					wfa.writeInt(ciArrayListLongType);
					wfa.writeArrayListLong(lsObj);
					rs = true;
				} else if (obj.getClass() == String.class) {
					wfa.writeInt(ciArrayListStringType);
					wfa.writeArrayListString(lsObj);
					rs = true;
				}
			}
		} else if (f.getType() == AtomicInteger.class) {
			wfa.writeInt(ciAtomicIntegerType);
			Object value = getValue(o, f);
			if (value != null) {
				wfa.writeInt(((AtomicInteger) value).get());
			} else {
				wfa.writeInt(0);
			}
			rs = true;
		}

		if (!rs) {
			wfa.writeInt(ciOtherType);
		}
	}

	static public boolean loadObject(ReadFileAction rfa, Object o) {
		boolean rs = false;
		int iCount = rfa.readInt();
		Class<?> c = o.getClass();
//		Log.logClass(c.getSimpleName() + ":" + iCount);
		if (iCount > 0) {
			for (int i = 0; i < iCount; i++) {
				String sField = rfa.readString();
				Field f = getField(c, sField);
				int iType = rfa.readInt();
				// Log.logClass(sField + ":" + iType);
				if (iType == ciIntType) {
					int iData = rfa.readInt();
					setInt(o, f, iData);
				} else if (iType == ciLongType) {
					long lData = rfa.readLong();
					setLong(o, f, lData);
				} else if (iType == ciStringType) {
					String sData = rfa.readString();
					setValue(o, f, sData);
				} else if (iType == ciBooleanType) {
					int iData = rfa.readInt();
					setBoolean(o, f, iData);
				} else if (iType == ciArrayIntType) {
					int[] lsData = rfa.readInts();
					setValue(o, f, lsData);
				} else if (iType == ciArrayLongType) {
					long[] lsData = rfa.readLongs();
					setValue(o, f, lsData);
				} else if (iType == ciArrayStringType) {
					String[] lsData = rfa.readStrings();
					setValue(o, f, lsData);
				} else if (iType == ciArrayListIntType) {
					ArrayList<Integer> lsInt = rfa.readArrayListInteger();
					setValue(o, f, lsInt);
				} else if (iType == ciArrayListLongType) {
					ArrayList<Long> lsLong = rfa.readArrayListLong();
					setValue(o, f, lsLong);
				} else if (iType == ciArrayListStringType) {
					ArrayList<String> lsString = rfa.readArrayListString();
					setValue(o, f, lsString);
				} else if (iType == ciAtomicIntegerType) {
					int iData = rfa.readInt();
					setAtomicInteger(o, f, iData);
				}
			}
			rs = true;
		}
		return rs;
	}

	static public void saveObject(WriteFileAction wfa, Object o, String[] lsField) {
		Class<?> c = o.getClass();
		if (lsField != null) {
			wfa.writeInt(lsField.length);
			for (String sField : lsField) {
				try {
					Field f = c.getField(sField);
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						f.setAccessible(true);
						saveField(wfa, o, f);
					}
				} catch (Exception e) {
					Log.logClass(c.getSimpleName() + ":" + e.getMessage());
				}
			}
		} else {
			wfa.writeInt(0);
		}
	}

	static public void saveObject(WriteFileAction wfa, Object o) {
		Field[] lsFields = null;
		if (o != null) {
			Class<?> c = o.getClass();
			try {
				lsFields = c.getFields();
				if (lsFields != null) {
//					Log.logClass(c.getSimpleName() + ":" + lsFields.size);
					int iField = lsFields.length;
					wfa.writeInt(iField);
					for (Field f : lsFields) {
						if ((f.getModifiers() & Modifier.STATIC) == 0) {
							f.setAccessible(true);
							saveField(wfa, o, f);
						} else {
							saveNull(wfa, f);	
						}
					}
				} else {
//					Log.logClass(c.getSimpleName() + ":0");
				}
			} catch (Exception e) {
				Log.logClass(c.getSimpleName() + ":" + e.getMessage());
			}
		}
	}
}
