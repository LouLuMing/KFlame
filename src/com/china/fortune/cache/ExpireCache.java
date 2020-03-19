package com.china.fortune.cache;

public class ExpireCache<E> {
	private long lActiveTime;
	private long lExpireTime = 0;
	private E obj = null;

	public ExpireCache() {
	}

	public ExpireCache(long l) {
		lExpireTime = l;
	}

	public void setExpire(long l) {
		lExpireTime = l;
	}

	public long getExpire() {
		return lExpireTime;
	}

	public E get() {
		if (lExpireTime > 0 && System.currentTimeMillis() < lActiveTime + lExpireTime) {
			return obj;
		}
		return null;
	}

	public void set(E o) {
		obj = o;
		lActiveTime = System.currentTimeMillis();
	}

}
