package com.china.fortune.target.userSystem;

import com.china.fortune.cache.Cacher;
import com.china.fortune.cache.DBCacheMap;

public class UserCacheMap extends DBCacheMap<Integer> {

	public UserCacheMap() {
		super("userId");
	}

	@Override
	protected Cacher newCacher(Integer key) {
		Token user = new Token();
		user.userId = key;
		return user;
	}
	
}
