package com.china.fortune.cache;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ExpireCacheReload<E> {
	abstract protected E reloadData();

	private long lExpireTime = 0;
	private AtomicLong lActiveTime = new AtomicLong(0);
	private AtomicReference<E> obj = new AtomicReference<E>();
	
	public ExpireCacheReload() {
	}

	public ExpireCacheReload(long l) {
		lExpireTime = l;
	}

	public void setExpire(long l) {
		lExpireTime = l;
	}

	public long getExpire() {
		return lExpireTime;
	}

	public E getOrLoad() {
		if (lExpireTime > 0) {
			long lLimitTime = System.currentTimeMillis() - lExpireTime;
			if (lLimitTime > lActiveTime.get()) {
				synchronized (this) {
					if (lLimitTime > lActiveTime.get()) {
						E o = reloadData();
						if (o != null) {
							lActiveTime.set(System.currentTimeMillis());
							obj.set(o);
						}
					}
				}
			}
			return obj.get();
		} else {
			return reloadData();
		}
	}
}
