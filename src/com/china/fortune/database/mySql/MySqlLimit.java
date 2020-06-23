package com.china.fortune.database.mySql;

public class MySqlLimit {
    static public String toSql(int iStart, int iCount) {
        StringBuilder sb = new StringBuilder();
        sb.append(" limit ");
        sb.append(iStart);
        sb.append(',');
        sb.append(iCount);
        return sb.toString();
    }

    static public String toSql(int iTop) {
        StringBuilder sb = new StringBuilder();
        sb.append(" limit ");
        sb.append(iTop);
        return sb.toString();
    }
}
