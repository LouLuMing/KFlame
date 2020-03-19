package com.china.fortune.cache;

import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.os.database.DbAction;
import com.china.fortune.reflex.ClassDatabase;
import com.china.fortune.reflex.ClassSerialize;
import com.china.fortune.reflex.ClassSync;

public abstract class DBNewCacheMap<E> {
	private String csKey = null;
	private HashMap<E, Cacher> mapCacher = new HashMap<E, Cacher>();

	abstract protected Cacher newCacher();

	public DBNewCacheMap(String sKey) {
		csKey = sKey;
	}

	public Cacher loadCacher(DbAction dbObj, E key) {
		Cacher cc = newCacher();
		ClassSerialize.setObject(cc, csKey, key);
		if (ClassDatabase.selectWhere(dbObj, cc, csKey) > 0) {
			put(key, cc);
			return cc;
		} else {
			return null;
		}
	}

	public void updateCacher(DbAction dbObj, Cacher cc, String[] lsUpdate) {
		ClassDatabase.update(dbObj, cc, lsUpdate, csKey);
	}

	public void updateCacher(DbAction dbObj, Cacher cc, String sUpdate) {
		ClassDatabase.update(dbObj, cc, sUpdate, csKey);
	}

	public void updateCacher(DbAction dbObj, Cacher cc) {
		ClassDatabase.update(dbObj, cc, csKey);
	}
	
	public int clearInactive(int days) {
		int iCount = 0;
		long lMilSecond = days * 24 * 3600 * 1000;
		for (Entry<E, Cacher> en : mapCacher.entrySet()) {
			Cacher cc = en.getValue();
			if (cc != null) {
				if (cc.isInactive(lMilSecond)) {
					en.setValue(null);
					cc.clear();
					iCount++;
				}
			}
		}
		return iCount;
	}

	public Cacher get(E key) {
		return mapCacher.get(key);
	}

	public void put(E key, Cacher cc) {
		synchronized (this) {
			mapCacher.put(key, cc);
		}
	}

	public void putAndSave(DbAction dbObj, E key, Cacher cc) {
		put(key, cc);
		ClassDatabase.insert(dbObj, cc);
	}

	public Cacher getOrLoad(DbAction dbObj, E key) {
		Cacher cc = mapCacher.get(key);
		if (cc == null) {
			cc = loadCacher(dbObj, key);
		}
		return cc;
	}

	public Cacher getOrPut(Cacher in) {
		E key = (E) ClassSerialize.getObject(in, csKey);
		return getOrPut(key, in);
	}
	
	public Cacher getOrPut(E key, Cacher in) {
		Cacher cc = mapCacher.get(key);
		if (cc == null) {
			put(key, in);
			cc = in;
		}
		return cc;
	}
	
	public Cacher getOrLoadOrNew(DbAction dbObj, E key) {
		Cacher cc = getOrLoad(dbObj, key);
		if (cc == null) {
			cc = newCacher();
			ClassSerialize.setObject(cc, csKey, key);
			putAndSave(dbObj, key, cc);
		}
		return cc;
	}
	
	public Cacher syncCacher(Cacher cc) {
		E key = (E) ClassSerialize.getObject(cc, csKey);
		Cacher exist = getOrPut(key, cc);
		if (exist != cc) {
			ClassSync.sync(cc, exist);
		}
		return exist;
	}
	
	public Cacher syncCacher(E key, Cacher cc) {
		Cacher exist = getOrPut(key, cc);
		if (exist != cc) {
			ClassSync.sync(cc, exist);
		}
		return exist;
	}

	public Cacher syncCacher(Cacher cc, String sUpdate) {
		E key = (E) ClassSerialize.getObject(cc, csKey);
		Cacher exist = getOrPut(key, cc);
		if (exist != cc) {
			ClassSync.sync(cc, exist, sUpdate);
		}
		return exist;
	}
	
	public Cacher syncCacher(E key, Cacher cc, String sUpdate) {
		Cacher exist = getOrPut(key, cc);
		if (exist != cc) {
			ClassSync.sync(cc, exist, sUpdate);
		}
		return exist;
	}
	
	public Cacher syncCacher(Cacher cc, String[] lsUpdate) {
		E key = (E) ClassSerialize.getObject(cc, csKey);
		Cacher exist = getOrPut(key, cc);
		if (exist != cc) {
			ClassSync.sync(cc, exist, lsUpdate);
		}
		return exist;
	}

}
