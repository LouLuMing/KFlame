package com.china.fortune.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class CheckDataType {
	static public boolean checkMailAddress(String sText) {
		boolean rs = false;
		if (sText != null && sText.length() > 0) {
			Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = pattern.matcher(sText);
			rs = matcher.find();
		}
		return rs;
	}

	static public boolean checkMobilePhone(String sText) {
		boolean rs = false;
		if (sText != null && sText.length() > 0) {
			Pattern pattern = Pattern.compile("^(1[1-9][0-9])\\d{8}$");
			Matcher matcher = pattern.matcher(sText);
			rs = matcher.find();
		}
		return rs;
	}

	static public String hideMiddle(String sData) {
		if (sData != null && sData.length() > 2) {
			char[] lsChar = sData.toCharArray();
			for (int i = lsChar.length / 3; i < 2 * lsChar.length / 3	; i++) {
				lsChar[i] = '*';
			}
			return new String(lsChar);
		}
		return sData;
	}
	
	static public String hidePhone(String sPhone) {
		if (checkMobilePhone(sPhone)) {
			char[] lsChar = sPhone.toCharArray();
			for (int i = 3; i < 7 && i < lsChar.length; i++) {
				lsChar[i] = '*';
			}
			return new String(lsChar);
		}
		return sPhone;
	}
	
	static public String hideName(String sName) {
		if (StringAction.length(sName) > 1) {
			if (sName.length() == 2) {
				char[] lsName = sName.toCharArray();
				StringBuilder sb = new StringBuilder();
				sb.append(lsName[0]);
				sb.append('*');
				return sb.toString();
			} else {
				return hideMiddle(sName);
			}
		} else {
			return sName;
		}
	}
	
	static public int getSexByIdno(String idno) {
		int sex = 0;
		if (idno.length() == 18) {
			sex = idno.charAt(idno.length() - 2) - '0';
		} else if (idno.length() == 15) {
			sex = idno.charAt(idno.length() - 1) - '0';
		}
		return sex;
	}
	
	static public int getBirthYearByIdno(String idno) {
		return StringAction.toInteger(idno.substring(6,10));
	}
	
	public static void main(String[] args) {
		Log.log(getBirthYearByIdno("330682198209095252") + "");
	}
}
