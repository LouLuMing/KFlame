package com.china.fortune.secure;

import com.china.fortune.global.Log;

public class StringSecret {
	static public String Encrypt(String s) {
		char[] lsChar = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lsChar.length; i++) {
			sb.append((char) (lsChar[i] ^ (0xab + i)));
		}
		return sb.toString();
	}
	
	static public void main(String[] args) {
		String sMsg = "1234567890qwertyuiopasdfghjklzxcvbnm";
		String sO = Encrypt(sMsg);
		Log.logClass(sO);
		sMsg = Encrypt(sO);
		Log.logClass(sMsg);
	}
}
