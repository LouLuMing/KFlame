package com.china.fortune.cache;

import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.os.database.DbAction;

public abstract class FileCacheMap<E> {
	private HashMap<E, Cacher> mapCacher = new HashMap<E, Cacher>();

	abstract protected Cacher loadCacher(DbAction dbObj, E key);
	abstract protected void saveCacher(DbAction dbObj, Cacher cc);
	
	public int clearInactive(int days) {
		int iCount = 0;
		long lMilSecond = days * 24 * 3600 * 1000;
		for (Entry<E, Cacher> en : mapCacher.entrySet()) {
			Cacher cc = en.getValue();
			if (cc != null) {
				if (cc.isInactive(lMilSecond)) {
					en.setValue(null);
					iCount++;
				}
			}
		}
		return iCount;
	}

	public Cacher get(E key) {
		return mapCacher.get(key);
	}
	
	public Cacher getOrLoad(DbAction dbObj, E key) {
		Cacher cc = mapCacher.get(key);
		if (cc == null) {
			cc = loadCacher(dbObj, key);
		}
		return cc;
	}
	
}
