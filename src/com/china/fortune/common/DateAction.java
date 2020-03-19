package com.china.fortune.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.china.fortune.global.Log;

public class DateAction {
	static final public String sDefDateFormat = "yyyy-MM-dd";
	static final public String sDefTimeFormat = "HH:mm:ss";
	static final public String sDefDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	static public String getTime() {
		SimpleDateFormat df = new SimpleDateFormat(sDefTimeFormat);
		return df.format(new Date());
	}

	static public String getDate() {
		SimpleDateFormat df = new SimpleDateFormat(sDefDateFormat);
		return df.format(new Date());
	}
	
	static public String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat(sDefDateTimeFormat);
		return df.format(new Date());
	}
	
	static public String getDateTime(String sFormat) {
		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		return df.format(new Date());
	}
	
	static public String getDateAddDay(int iAdd) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sDefDateFormat);
		return df.format(now.getTime());
	}
	
	static public String getDateAddDay(int iAdd, String sFormat) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		return df.format(now.getTime());
	}
	
	static public String getDateAddHour(int iAdd) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sDefDateFormat);
		return df.format(now.getTime());
	}
	
	static public String getDateAddHour(int iAdd, String sFormat) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		return df.format(now.getTime());
	}
	
	static public String getDateAddMinute(int iAdd) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sDefDateFormat);
		return df.format(now.getTime());
	}
	
	static public String getDateAddMinute(int iAdd, String sFormat) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, iAdd);
		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		return df.format(now.getTime());
	}
	
	static public String createDateTime(long iMilSecond, String sFormat) {
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(iMilSecond);
		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		return df.format(gc.getTime());
	}

	static public Date createDateTime(long iMilSecond) {
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(iMilSecond);
		return gc.getTime();
	}
	
	static public int getYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	static public int getMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
	}

	static public int getMonthStartDate(int iYear, int iMonth) {
		String sDate = String.format("%4d-%02d-01", iYear, iMonth);
		long lLastMonth = DateAction.getDateTicket(sDate, sDefDateFormat);
		return DateAction.getDays(lLastMonth);
	}

	static public int getMonthStartDate(int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long) date) * 24 * 3600 * 1000);

		String sDate = String.format("%4d-%02d-01", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
		long lLastMonth = DateAction.getDateTicket(sDate, sDefDateFormat);
		return DateAction.getDays(lLastMonth);
	}

	static public int getYear(int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long) date) * 24 * 3600 * 1000);
		return calendar.get(Calendar.YEAR);
	}
	
	static public int getMonth(int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long) date) * 24 * 3600 * 1000);
		return calendar.get(Calendar.MONTH) + 1;
	}

	static public int getMonths(long ticket) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ticket);
		int year = calendar.get(Calendar.YEAR);
		return (year - 1970) * 12 + calendar.get(Calendar.MONTH);
	}

	static public int getMonths(int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(((long) date) * 24 * 3600 * 1000);
		int year = calendar.get(Calendar.YEAR);
		return (year - 1970) * 12 + calendar.get(Calendar.MONTH);
	}

	static public int getWeekToDate(int week) {
		return week * 7 - 3;
	}
	
	static public int getWeek(int date) {
		return (date + 3) / 7;
	}
	
	static public int getStartDateOfWeek(int week) {
		return week * 7 - 3;
	}
	
	static public int getDayOfWeek(int date) {
		return (date - 4) % 7;
	}
	
	static public Date toDate(String sDate, String sFormat) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
		try {
			date = sdf.parse(sDate);
		} catch (ParseException e) {
		}
		return date;
	}
	
	static public long getDateTicket(String sDate, String sFormat) {
		Date dt = toDate(sDate, sFormat);
		return dt.getTime();
	}
	
	static public long getTodayStartHour() {
		return getTodayStartHour(8);
	}
	
	static public long getTodayStartHour(int off) {
		return (System.currentTimeMillis() / 1000 / 3600 + off) / 24 * 24 - off;
	}
	
	static public int getDays(long lTicket, int off) {
		return (int)((lTicket / 1000 / 3600 + off) / 24);
	}
	
	static public int getDays(long lTicket) {
		return (int)((lTicket / 1000 / 3600 + 8) / 24);
	}
	
	static public int getNowDays() {
		return getDays(System.currentTimeMillis());
	}
	
	static public long dayToSecond(int days) {
		return  (days * 24 - 8) * 3600;
	}
	
	static public long dayToSecond(int days, int off) {
		return  (days * 24 - off) * 3600;
	}
	
	static public int getHours(long lTicket) {
		return (int)(lTicket / 1000 / 3600);
	}
	
//	static public int getHourOfDay() {
//		long lTicket = System.currentTimeMillis();
//		return (int)((lTicket / 1000 / 3600 + 8) % 24);
//	}
	
	static public long daysToHours(int iDays, int off) {
		return (long)(iDays * 24 - off);
	}
	
	static public long daysToHours(int iDays) {
		return (long)(iDays * 24 - 8);
	}
	
	public static void main(String[] args) {
		Log.log(createDateTime(1491962969L * 1000, "yyyy-MM-dd HH:mm:ss"));
		long lTicket = getDateTicket("2017-08-24 23:59:59", "yyyy-MM-dd HH:mm:ss");
		Log.log("" + lTicket);
		Log.log("" + getHours(System.currentTimeMillis()));
		Log.log("" + (getTodayStartHour(8) + 28));
		Log.log("" + getDays(lTicket, 8));
		Log.log("" + getMonth(getNowDays()));
	}
}
