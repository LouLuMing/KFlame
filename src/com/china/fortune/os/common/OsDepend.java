package com.china.fortune.os.common;

import com.china.fortune.global.Log;

//java -cp myAnt.jar com.china.fortune.os.common.OsDepend

public class OsDepend {
	static public boolean isWin() {
		String sOsName = System.getProperties().getProperty("os.name");
		return sOsName.startsWith("Win");
	}
	
	static public boolean isMac() {
		String sOsName = System.getProperties().getProperty("os.name");
		return sOsName.startsWith("Mac");
	}
	
	static public boolean isLinux() {
		String sOsName = System.getProperties().getProperty("os.name");
		return sOsName.startsWith("Linux");
	}
	
	public static void main(String[] args) {
		Log.log(System.getProperties().getProperty("os.name"));
	}
}
