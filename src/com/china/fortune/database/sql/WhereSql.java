package com.china.fortune.database.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class WhereSql {
	private String sOperation = "and";
	private ArrayList<WhereItem> lsWhere = new ArrayList<WhereItem>();

	public WhereSql() {
	}

	// "and", "or"
	public WhereSql(String sOper) {
		sOperation = sOper;
	}

	public int size() {
		return lsWhere.size();
	}

	public void append(WhereSql ws) {
		if (ws != null) {
			for (WhereItem wi : ws.lsWhere) {
				lsWhere.add(wi);
			}
		}
	}

	public void add(String s1, String s2, String s3, keyType kt) {
		lsWhere.add(new WhereItem(s1, s2, s3, kt));
	}

	public void add(String s1, String s2, String s3) {
		lsWhere.add(new WhereItem(s1, s2, s3));
	}

	public void add(String s1, String s2, int s3) {
		lsWhere.add(new WhereItem(s1, s2, s3));
	}

	public void equal(String s1, long s3) {
		lsWhere.add(new WhereItem(s1, "=", s3));
	}

	public void equal(String s1, int s3) {
		lsWhere.add(new WhereItem(s1, "=", s3));
	}

	public void equal(String s1, String s3) {
		lsWhere.add(new WhereItem(s1, "=", s3));
	}

	public void largerEqual(String s1, int s3) {
		lsWhere.add(new WhereItem(s1, ">=", s3));
	}

	public void largerEqual(String s1, long s3) {
		lsWhere.add(new WhereItem(s1, ">=", s3));
	}

	public void larger(String s1, int s3) {
		lsWhere.add(new WhereItem(s1, ">", s3));
	}

	public void larger(String s1, long s3) {
		lsWhere.add(new WhereItem(s1, ">", s3));
	}

	public void smaller(String s1, int s3) {
		lsWhere.add(new WhereItem(s1, "<", s3));
	}

	public void smallerEqual(String s1, long s3) {
		lsWhere.add(new WhereItem(s1, "<=", s3));
	}

	public void smallerEqual(String s1, int s3) {
		lsWhere.add(new WhereItem(s1, "<=", s3));
	}

	public void smaller(String s1, long s3) {
		lsWhere.add(new WhereItem(s1, "<", s3));
	}

	public void inLong(String sKey, Collection<Long> lsIn) {
		add(InSql.createLong(sKey, lsIn));
	}

	public void inString(String sKey, Collection<String> lsIn) {
		add(InSql.createString(sKey, lsIn));
	}

	public void inInt(String sKey, Collection<Integer> lsIn) {
		add(InSql.createInt(sKey, lsIn));
	}

	public void add(String s1, String s2, long s3) {
		lsWhere.add(new WhereItem(s1, s2, s3));
	}

	public void add(String s1) {
		if (s1 != null) {
			lsWhere.add(new WhereItem(null, null, s1));
		}
	}

	public void add(Object o, String sField) {
		try {
			Class<?> c = o.getClass();
			Field f = c.getField(sField);
			f.setAccessible(true);
			Object value = f.get(o);
			if (f.getType() == String.class) {
				add(f.getName(), "=", (String) value);
			} else if (f.getType() == int.class || f.getType() == Integer.class) {
				add(f.getName(), "=", (Integer) value);
			} else if (f.getType() == long.class || f.getType() == Long.class) {
				add(f.getName(), "=", (Long) value);
			} else if (f.getType() == char.class) {
				add(f.getName(), "=", String.valueOf(value));
			}
		} catch (Exception e) {
			Log.logClassError(e.getMessage());
		}
	}

	public void add(Object o, String[] lsField) {
		try {
			Class<?> c = o.getClass();
			for (String sField : lsField) {
				Field f = c.getField(sField);
				f.setAccessible(true);
				Object value = f.get(o);
				if (f.getType() == String.class) {
					add(f.getName(), "=", (String) value);
				} else if (f.getType() == int.class || f.getType() == Integer.class) {
					add(f.getName(), "=", (Integer) value);
				} else if (f.getType() == long.class || f.getType() == Long.class) {
					add(f.getName(), "=", (Long) value);
				}
			}
		} catch (Exception e) {
			Log.logClassError(e.getMessage());
		}
	}

	public void add(Object o) {
		try {
			Class<?> c = o.getClass();
			Field[] lsFields = c.getFields();
			for (Field f : lsFields) {
				f.setAccessible(true);
				Object value = f.get(o);
				if (f.getType() == String.class) {
					add(f.getName(), "=", (String) value);
				} else if (f.getType() == int.class || f.getType() == Integer.class) {
					add(f.getName(), "=", (Integer) value);
				} else if (f.getType() == long.class || f.getType() == Long.class) {
					add(f.getName(), "=", (Long) value);
				}
			}
		} catch (Exception e) {
			Log.logClassError(e.getMessage());
		}
	}

	public void clear() {
		lsWhere.clear();
	}

	private enum WhereType {
		EQUE, NOTEQUE, IN
	};

	public String toSql() {
		if (lsWhere.size() > 0) {
			boolean bNotFirstTime = false;
			StringBuilder sSql = new StringBuilder();
			sSql.append(" where ");
			for (WhereItem sWhere : lsWhere) {
				if (bNotFirstTime) {
					sSql.append(' ');
					sSql.append(sOperation);
					sSql.append(' ');
				} else {
					bNotFirstTime = true;
				}
				WhereType wt = WhereType.EQUE;
				if (sWhere.sCompare == null) {
					sSql.append(sWhere.sValue);
				} else {
					if ("in".equals(sWhere.sCompare)) {
						wt = WhereType.IN;
					}
					sSql.append(sWhere.sField);
					if (wt == WhereType.IN) {
						sSql.append(' ');
					}

					sSql.append(sWhere.sCompare);

					if (wt == WhereType.IN) {
						sSql.append(" (");
					}

					if (sWhere.iType == keyType.DOT) {
						sSql.append("'");
						sSql.append(sWhere.sValue);
						sSql.append("'");
					} else {
						sSql.append(sWhere.sValue);
					}

					if (wt == WhereType.IN) {
						sSql.append(')');
					}
				}
			}
			return sSql.toString();
		} else {
			return "";
		}
	}
}
