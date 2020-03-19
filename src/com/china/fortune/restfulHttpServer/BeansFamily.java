package com.china.fortune.restfulHttpServer;

import com.china.fortune.global.Log;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassRraverse;
import com.china.fortune.restfulHttpServer.annotation.AsComponent;
import com.china.fortune.restfulHttpServer.annotation.AsServlet;
import com.china.fortune.restfulHttpServer.annotation.Autowired;
import com.china.fortune.struct.FastList;
import com.china.fortune.struct.HitCacheManager;

import java.lang.reflect.Field;
import java.util.List;

public class BeansFamily {
    private FastList<String> lsTag = new FastList<String>();
    private FastList<Object> lsBean = new FastList<Object>();
    private HitCacheManager hcm = new HitCacheManager();

    public void put(String key, Object o) {
        if (key != null && o != null) {
            if (!lsTag.contains(key)) {
                lsTag.add(key);
                lsBean.add(o);
            } else {
                Log.logClassError(key + " is exist");
            }
        }
    }

    public void put(Class<?> cls, Object o) {
        if (cls != null) {
            put(cls.getName(), o);
        }
    }

    public void put(Object o) {
        if (o != null) {
            put(o.getClass().getName(), o);
        }
    }

    public void remove(String clsName) {
        int index = lsTag.indexOf(clsName);
        if (index >= 0) {
            lsTag.remove(index);
            lsBean.remove(index);
        }
    }

    public int initHitCache() {
        return hcm.init(lsTag);
    }

    public Object get(String sTag) {
        if (sTag != null) {
            int i = hcm.find(sTag);
            if (i >= 0) {
                return lsBean.get(i);
            }
        }
        return null;
    }

    public <T>T get(Class<T> c) {
        return (T)get(c.getName());
    }

    public void removeBean(Class<?> cls) {
        remove(cls.getName());
    }

    public void removeBean(String packagePath) {
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            remove(clsName);
        }
    }

    public void scanBean(String packagePath) {
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            Object obj = ClassUtils.create(clsName);
            if (obj != null) {
                put(clsName, obj);
            }
        }
    }

    public void injectField(Object obj) {
        Class<?> cls = obj.getClass();
        do {
            injectField(cls, obj);
            cls = cls.getSuperclass();
        } while (cls != Object.class);
    }

    public void injectField(Class<?> cls, Object obj) {
        try {
            Field[] lsFields = cls.getDeclaredFields();
            for (Field f : lsFields) {
                f.setAccessible(true);
                Object bean = f.get(obj);
                if (bean == null) {
                    Class<?> clsType = f.getType();
                    bean = get(clsType);
                    if (bean != null) {
                        f.set(obj, bean);
//                    } else {
//                        Log.logClassError(cls.getName() + ":" + clsType.getName() + " is null");
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    public void injectSelfAutowired() {
        for (int i = 0; i < lsBean.size(); i++) {
            Object o = lsBean.get(i);
            injectFieldAutowired(o);
        }
    }

    public void injectFieldAutowired(Object obj) {
        Class<?> cls = obj.getClass();
        do {
            injectFieldAutowired(cls, obj);
            cls = cls.getSuperclass();
        } while (cls != Object.class);
    }

    public void injectFieldAutowired(Class<?> cls, Object obj) {
        try {
            Field[] lsFields = cls.getDeclaredFields();
            for (Field f : lsFields) {
                f.setAccessible(true);
                Object bean = f.get(obj);
                if (bean == null) {
                    Class<?> clsType = f.getType();
                    if (clsType.isAnnotationPresent(Autowired.class)) {
                        bean = get(clsType);
                        if (bean != null) {
                            f.set(obj, bean);
                        } else {
                            Log.logClassError(cls.getName() + ":" + clsType.getName() + " is null");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static public void main(String[] args) {
        BeansFamily bf = new BeansFamily();
        bf.scanBean("com.china.fortune.target.onenet.tools");
        bf.initHitCache();
        bf.injectField(bf);
    }
}
