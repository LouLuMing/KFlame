package com.china.fortune.database.sql;

import java.util.Collection;

public class InSql {
	static public String createLong(String sKey, Collection<Long> lsIn) {
		if (lsIn.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(sKey);
			sb.append(" in ");
			sb.append('(');
			for (Long i : lsIn) {
				sb.append(i);
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			sb.append(')');
			return sb.toString();
		} else {
			return null;
		}
	}

    static public String createInt(String sKey, String[] lsIn) {
        if (lsIn.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(sKey);
            sb.append(" in ");
            sb.append('(');
            for (String i : lsIn) {
                sb.append(i);
                sb.append(',');
            }
            sb.setLength(sb.length() - 1);
            sb.append(')');
            return sb.toString();
        } else {
            return null;
        }
    }

	static public String createInt(String sKey, Collection<Integer> lsIn) {
		if (lsIn.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(sKey);
			sb.append(" in ");
			sb.append('(');
			for (Integer i : lsIn) {
				sb.append(i);
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			sb.append(')');
			return sb.toString();
		} else {
			return null;
		}
	}

	static public String createString(String sKey, Collection<String> lsIn) {
		if (lsIn.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(sKey);
			sb.append(" in ");
			sb.append('(');
			for (String s : lsIn) {
				sb.append('\'');
				sb.append(s);
				sb.append('\'');
				sb.append(',');
			}
			sb.setLength(sb.length() - 1);
			sb.append(')');
			return sb.toString();
		} else {
			return null;
		}
	}
}
