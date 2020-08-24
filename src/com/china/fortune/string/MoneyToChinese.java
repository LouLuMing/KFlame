package com.china.fortune.string;

import com.china.fortune.global.Log;

public class MoneyToChinese {
	private static final char UNIT[] = { '分', '角', '元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟', '万' };
	private static final char NUM[] = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };

	static public String toChinese(String sMoney) {
		StringBuilder sb = new StringBuilder();
		String sYuanMoney = sMoney;
		int iDot = sMoney.indexOf('.');
		String sFenMoney = null;
		if (iDot > 0) {
			sYuanMoney = sMoney.substring(0, iDot);
			sFenMoney = sMoney.substring(iDot + 1);
			if (StringUtils.toInteger(sFenMoney) == 0) {
				sFenMoney = null;
			}
		}

		boolean bAddZero = false;
		char[] lsChar = sYuanMoney.toCharArray();
		for (int i = 0; i < lsChar.length; i++) {
			char c = lsChar[i];
			int j = c - '0';
			int iUnit = lsChar.length - i + 1;
			if (j >= 0 && j < NUM.length && iUnit >= 0 && iUnit < UNIT.length) {
				if (j == 0) {
					bAddZero = true;
					if (UNIT[iUnit] == '万' || UNIT[iUnit] == '亿') {
						sb.append(UNIT[iUnit]);
					}
				} else {
					if (bAddZero) {
						bAddZero = false;
						sb.append(NUM[0]);
					}
					sb.append(NUM[j]);
					sb.append(UNIT[iUnit]);
				}
			}
		}
		if (sFenMoney == null) {
			sb.append("元整");
		} else {
			bAddZero = false;
			sb.append('元');
			int j = sFenMoney.charAt(0) - '0';
			if (j != 0) {
				sb.append(NUM[j]);
				sb.append(UNIT[1]);
			}
			if (sFenMoney.length() > 1) {
				j = sFenMoney.charAt(1) - '0';
				if (j != 0) {
					sb.append(NUM[j]);
					sb.append(UNIT[0]);
				}
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		Log.log(toChinese("80102500.01"));
	}
}
