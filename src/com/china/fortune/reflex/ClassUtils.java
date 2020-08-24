package com.china.fortune.reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;

import com.china.fortune.global.Log;

public class ClassUtils {
	static public Object create(String className) {
		Object obj = null;
		if (className != null) {
			try {
				Class<?> cls = Class.forName(className);
				if (cls != null && !Modifier.isAbstract(cls.getModifiers())) {
//				if (cls != null) {
					obj = cls.newInstance();
				}
			} catch (Error e) {
				Log.logException(e);
			} catch (Exception e) {
				Log.logException(e);
			}
		}
		return obj;
	}

	static public Class<?> getClass(String className) {
		Class<?>  cls = null;
		if (className != null) {
			try {
				cls = Class.forName(className);
			} catch (Error e) {
				Log.logException(e);
			} catch (Exception e) {
				Log.logException(e);
			}
		}
		return cls;
	}

//    static public String[] getFieldName(Class<?> cls) {
//        String[] lsNames = null;
//        try {
//            Field[] lsFields = cls.getFields();
//            if (lsFields.size > 0) {
//                lsNames = new String[lsFields.size];
//                int iPort = 0;
//                for (Field f : lsFields) {
//                    if ((f.getModifiers() & Modifier.STATIC) == 0) {
//                        lsNames[iPort++] = (f.getName());
//                    }
//                }
//            }
//        } catch (Exception e) {
//        }
//        return lsNames;
//    }

	static public ArrayList<String> getFieldName(Class<?> cls) {
		ArrayList<String> lsNames = new ArrayList<String>();
		try {
			Field[] lsFields = cls.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					lsNames.add(f.getName());
				}
			}
		} catch (Exception e) {
		}
		return lsNames;
	}

    static public ArrayList<String> getMethodName(Class<?> cls) {
        ArrayList<String> lsNames = new ArrayList<String>();
        try {
            Method[] lsMethods = cls.getMethods();
            for (Method m : lsMethods) {
                if ((m.getModifiers() & Modifier.STATIC) == 0) {
                    lsNames.add(m.getName());
                }
            }
        } catch (Exception e) {
        }
        return lsNames;
    }

	static public void showAllFields(Object o) {
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					Log.log(f.getName() + ":" + f.get(o));
				}
			}
		} catch (Exception e) {
			Log.logClass(c.getSimpleName() + ":" + e.getMessage());
		}
	}

	static public void showAllFields(Class<?> c) {
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					f.setAccessible(true);
					Log.log(f.getName() + ":" + f.get(c));
				}
			}
		} catch (Exception e) {
		}
	}

	static public boolean checkNoNull(Object o) {
		boolean rs = true;
		Class<?> c = o.getClass();
		try {
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					Object data = f.get(o);
					if (data == null) {
						Log.logError(f.getName() + " is null");
						rs = false;
					} else {
						Log.log(f.getName() + ":" + data);
					}
				}
			}
		} catch (Exception e) {
		}
		return rs;
	}

	static public void setValue(Object o, String field, Object data) {
		Class<?> c = o.getClass();
		try {
			Field f = c.getField(field);
			f.set(o, data);
		} catch (Exception e) {
		}
	}

	static public void calMethod(Object obj, String sMethod) {
        if (obj != null && sMethod != null) {
            Class<?> cls = obj.getClass();
            try {
                Method mt = cls.getDeclaredMethod(sMethod);
                if (mt != null) {
                    mt.invoke(obj);
                }
            } catch (Exception e) {
                Log.logClassError(e.getMessage());
            }
        }
    }
}
