package com.china.fortune.string;

public class ReadString {
	private String sString = null;
	private int iOffset = 0;
	
	public ReadString(String s) {
		sString = s;
	}
	
	public void reset() {
		iOffset = 0;
	}
	public String readLine() {
		String rs = null;
		int i = sString.indexOf('\n', iOffset);
		if (i > iOffset) {
			if (sString.charAt(i-1) == '\r') {
				if (i > (iOffset + 1)) {
					rs = sString.substring(iOffset, i-1);
				}
				else {
					rs = "";
				}
			} else {
				rs = sString.substring(iOffset, i);
			}
			iOffset = (i + 1);
		}
		else if (i == 0) {
			rs = "";
			iOffset++;
		}
		return rs;
	}
	
	public boolean skipLine(int iLine) {
		boolean rs = true;
		for (int i = 0; i < iLine; i++) {
			int iIndex = sString.indexOf('\n', iOffset);
			if (iIndex >= 0) {
				iOffset = (iIndex + 1);
			} else {
				rs = false;
				break;
			}
		}
		return rs;
	}
}
