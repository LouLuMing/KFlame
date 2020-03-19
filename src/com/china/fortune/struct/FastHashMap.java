package com.china.fortune.struct;

public class FastHashMap<E> {
    private FastList<String> lsTag = new FastList<String>();
    private FastList<E> lsBean = new FastList<E>();
    private HitCacheManager hcm = new HitCacheManager();

    public int initHitCache() {
        return hcm.init(lsTag);
    }

    public void put(String key, E o) {
        if (key != null && o != null) {
            if (!lsTag.contains(key)) {
                lsTag.add(key);
                lsBean.add(o);
            }
        }
    }

    public void put(Class<?> cls, E o) {
        if (cls != null) {
            put(cls.getName(), o);
        }
    }

    public E get(String sTag) {
        if (sTag != null) {
            int i = hcm.find(sTag);
            if (i >= 0) {
                return lsBean.get(i);
            }
        }
        return null;
    }

    public E get(Class<?> c) {
        return get(c.getName());
    }
}
