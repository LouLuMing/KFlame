package com.china.fortune.target.miniWeb.table;

import com.china.fortune.global.Log;
import com.china.fortune.target.userSystem.Token;

public class User extends Token {
	public String phone;
	public String password;
	public String nickname;
	public String avatar;
	public int sex;
	public long forbiddenTicket;
	public long authTicket;

	@Override
	public boolean checkToken(int s) {
		if (token == s) {
			access();
			return true;
		} else {
			Log.logClass(userId + ":" + token + ":" + s);
			return false;
		}
	}

	public boolean checkPassword(String pwd) {
		return password.equals(pwd);
	}
}
