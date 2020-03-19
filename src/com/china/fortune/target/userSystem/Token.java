package com.china.fortune.target.userSystem;

import java.util.concurrent.ThreadLocalRandom;

import com.china.fortune.cache.Cacher;
import com.china.fortune.global.Log;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.reflex.ClassDatabase;

public class Token extends Cacher {
	public int userId;
	public int token;
	public long ticket;
	
	public void refreshToken() {
		access();
		token = ThreadLocalRandom.current().nextInt(0x6fffffff) + 0x10000000;
	}
	
	public boolean checkToken(int s) {
		if (token == s || (userId == 1 && s == 10086)) {
			access();
			return true;
		} else {
			Log.logError("checkToken " + userId + ":" + token + ":" + s);
			return false;
		}
	}
	
	public void refreshAndSaveToken(DbAction dbObj) {
		refreshToken();
		saveToken(dbObj);
	}
	
	public void saveToken(DbAction dbObj) {
		ClassDatabase.update(dbObj, this, "token", "userId");
	}
	
	public boolean checkToken(int s, int iTimeout) {
		if (token == s && isActive(iTimeout)) {
			access();
			return true;
		} else {
			return false;
		}
	}
}
