package com.china.fortune.os.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.china.fortune.easy.Int2Struct;
import com.china.fortune.statistics.family.FamilyIdInterface;
import com.china.fortune.statistics.count.CountInterface;
import com.china.fortune.database.SqlLog;
import com.china.fortune.database.sql.OrderBy;
import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.global.Log;

public abstract class DbAction {
    public interface onSelectListener {
        int onSelect(ResultSet rs);
    }

    abstract public ArrayList<String> selectAllTableName();

    abstract public String createTableSql(String sTable);

    abstract public String getTableComment(String sTable);
    abstract public ArrayList<String> selectColumnName(String sTable);

    abstract public String showDataSql(String sTable, int iPage, int iPageSize);

    abstract public String showDataSql(String sTable);

    abstract public ArrayList<ArrayList<String>> selectColumnInfo(String sTable);
//    abstract public ArrayList<ArrayList<String>> selectAllTableNameAndComment();
    static public final String oracleDriver = "oracle.jdbc.driver.OracleDriver";
    static public final String sqliteDriver = "org.sqlite.JDBC";
    static public final String mysqlDriver = "com.mysql.cj.jdbc.Driver";

    protected Connection mConn = null;
    protected String sJdbc = null;
    protected String sUser = null;
    protected String sPassword = null;

    static public boolean init(String sDriver) {
        boolean rs = false;
        try {
            Class.forName(sDriver);
            rs = true;
        } catch (Exception e) {
            Log.logError(e.getMessage() + ":" + sDriver);
        }
        return rs;
    }

    public void set(String jdbc, String user, String passwd) {
        sJdbc = jdbc;
        sUser = user;
        sPassword = passwd;
    }

    protected boolean check() {
        boolean bClose = true;
        if (mConn != null) {
            try {
                bClose = mConn.isClosed();
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        if (bClose) {
            return openInner();
        } else {
            return true;
        }
    }

    public boolean open(String jdbc) {
        sJdbc = jdbc;
        sUser = null;
        sPassword = null;
        return openInner();
    }

    public boolean open(String jdbc, String user, String passwd) {
        sJdbc = jdbc;
        sUser = user;
        sPassword = passwd;
        return openInner();
    }

    private boolean openInner() {
        close();
        boolean rs = false;
        try {
            SqlLog.log("open " + sJdbc + ":" + sUser + ":" + sPassword);
            if (sUser != null && sPassword != null) {
                mConn = DriverManager.getConnection(sJdbc, sUser, sPassword);
            } else {
                mConn = DriverManager.getConnection(sJdbc);
            }
            rs = true;
        } catch (Exception e) {
            Log.logError(e.getMessage() + ":" + sJdbc + ":" + sUser + ":" + sPassword);
        }
        return rs;
    }

    public void close() {
        if (mConn != null) {
            try {
                mConn.close();
            } catch (Exception e) {
                Log.logException(e);
            }
            mConn = null;
        }
    }

    public Statement createStatement() throws SQLException {
        if (check()) {
            return mConn.createStatement();
        }
        return null;
    }

    public int executeNoLog(String sSql) {
        int count = -1;
        for (int i = 0; i < 2; i++) {
            try {
                Statement sm = mConn.createStatement();
                count = sm.executeUpdate(sSql);
                sm.close();
                break;
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return count;
    }

    public int execute(String sSql) {
        SqlLog.log(sSql);
        int count = -1;
        for (int i = 0; i < 2; i++) {
            try {
                Statement sm = mConn.createStatement();
                count = sm.executeUpdate(sSql);
                sm.close();
                break;
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return count;
    }

    public boolean execute(String sSql, Object... args) {
        SqlLog.log(sSql);
        boolean rs = false;
        for (int k = 0; k < 2; k++) {
            try {
                PreparedStatement ps = mConn.prepareStatement(sSql);
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                ps.executeUpdate();
                ps.close();
                rs = true;
                break;
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return rs;
    }

    public int select(String sSql, onSelectListener oSl) {
        SqlLog.log(sSql);
        int rz = -1;
        for (int i = 0; i < 2; i++) {
            rz = 0;
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        rz = oSl.onSelect(rs);
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
                rz = -2;
            }
            check();
        }
        return rz;
    }

    public ArrayList<ArrayList<String>> selectStringMatrix(String sSql) {
        SqlLog.log(sSql);
        ArrayList<ArrayList<String>> lsS = new ArrayList<ArrayList<String>>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            ArrayList<String> lsItems = new ArrayList<String>();
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsItems.add(rs.getString(i));
                            }
                            lsS.add(lsItems);
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<String> selectStringOneRow(String sSql) {
        SqlLog.log(sSql);
        ArrayList<String> lsS = new ArrayList<String>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        if (rs.next()) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsS.add(rs.getString(i));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<String> selectStringOneColumn(String sSql) {
        SqlLog.log(sSql);
        ArrayList<String> lsS = new ArrayList<String>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            lsS.add(rs.getString(1));
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<Long> selectLongOneColumn(String sSql) {
        SqlLog.log(sSql);
        ArrayList<Long> lsS = new ArrayList<Long>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            lsS.add(rs.getLong(1));
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<Float> selectFloatOneColumn(String sSql) {
        SqlLog.log(sSql);
        ArrayList<Float> lsObj = new ArrayList<Float>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            lsObj.add(rs.getFloat(1));
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsObj;
    }

    public long selectLong(String sSql) {
        SqlLog.log(sSql);
        long obj = 0;
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            obj = rs.getLong(1);
                            break;
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return obj;
    }

    public String selectString(String sSql) {
        SqlLog.log(sSql);
        String obj = null;
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            obj = rs.getString(1);
                            break;
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return obj;
    }

    public ArrayList<ArrayList<Integer>> selectIntegerMatrix(String sSql) {
        SqlLog.log(sSql);
        ArrayList<ArrayList<Integer>> lsS = new ArrayList<ArrayList<Integer>>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            ArrayList<Integer> lsItems = new ArrayList<Integer>();
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsItems.add(rs.getInt(i));
                            }
                            lsS.add(lsItems);
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<ArrayList<Long>> selectLongMatrix(String sSql) {
        SqlLog.log(sSql);
        ArrayList<ArrayList<Long>> lsS = new ArrayList<ArrayList<Long>>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            ArrayList<Long> lsItems = new ArrayList<Long>();
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsItems.add(rs.getLong(i));
                            }
                            lsS.add(lsItems);
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public int selectInt(String sSql) {
        SqlLog.log(sSql);
        int iData = -1;
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            iData = rs.getInt(1);
                            break;
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return iData;
    }

    public ArrayList<Integer> selectIntOneColumn(String sSql) {
        SqlLog.log(sSql);
        ArrayList<Integer> lsInt = new ArrayList<Integer>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        while (rs.next()) {
                            lsInt.add(rs.getInt(1));
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsInt;
    }

    public ArrayList<Long> selectLongOneRow(String sSql) {
        SqlLog.log(sSql);
        ArrayList<Long> lsS = new ArrayList<Long>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        if (rs.next()) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsS.add(rs.getLong(i));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public ArrayList<Integer> selectIntOneRow(String sSql) {
        SqlLog.log(sSql);
        ArrayList<Integer> lsS = new ArrayList<Integer>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        if (rs.next()) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int iColumn = rsmd.getColumnCount();
                            for (int i = 1; i <= iColumn; i++) {
                                lsS.add(rs.getInt(i));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return lsS;
    }

    public int loadMaxId(DbAction dbObj, String sTable, String sIdName) {
        String sSelect = "select max(%s) from %s;";
        return dbObj.selectInt(String.format(sSelect, sIdName, sTable));
    }

    public ArrayList<Integer> countPerSpan(CountInterface dc, String sTable, String sField, String sTicket, long lStart,
                                           long lEnd, int iTimeSpan) {
        String sSql = "select " + sField + "," + sTicket + " from " + sTable;
        WhereSql ws = new WhereSql();
        ws.largerEqual(sTicket, lStart);
        ws.smaller(sTicket, lEnd);
        OrderBy ob = new OrderBy();
        ob.add(sTicket, false);

        sSql = sSql + ws.toSql() + ob.toSql();
        SqlLog.log(sSql);
        ArrayList<Integer> lsS = new ArrayList<Integer>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        long lLimit = lStart + iTimeSpan;
                        if (rs.next()) {
                            int iValue = rs.getInt(1);
                            long ticket = rs.getLong(2);
                            if (lLimit > ticket) {
                                dc.add(iValue);
                            } else {
                                lsS.add(dc.count());
                                lLimit += iTimeSpan;
                                while (lLimit <= ticket) {
                                    lsS.add(0);
                                    lLimit += iTimeSpan;
                                }
                                dc.clear();
                                dc.add(iValue);
                            }
                        }
                        lsS.add(dc.count());
                        while (lLimit < lEnd) {
                            lsS.add(0);
                            lLimit += iTimeSpan;
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
            lsS.clear();
        }
        return lsS;
    }

    public ArrayList<Int2Struct> countPerSpan(CountInterface dc, FamilyIdInterface fi, String sTable, String sField, String sTicket, WhereSql ws) {
        String sSql = "select " + sField + "," + sTicket + " from " + sTable;
        OrderBy ob = new OrderBy();
        ob.add(sTicket, false);

        sSql = sSql + ws.toSql() + ob.toSql();
        SqlLog.log(sSql);
        ArrayList<Int2Struct> lsS = new ArrayList<Int2Struct>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        int currentFamilyId = Integer.MIN_VALUE;
                        boolean bNotFirst = false;
                        while (rs.next()) {
                            int iValue = rs.getInt(sField);
                            long ticket = rs.getLong(sTicket);
//                            int iValue = rs.getInt(1);
//                            long ticket = rs.getLong(2);
                            if (ticket < 9999999999L) {
                                ticket *= 1000;
                            }
                            int familyId = fi.familyId(ticket);
                            if (currentFamilyId == familyId) {
                                dc.add(iValue);
                            } else if (currentFamilyId < familyId) {
                                if (bNotFirst) {
                                    lsS.add(new Int2Struct(currentFamilyId, dc.count()));
                                    currentFamilyId = fi.nextFamilyId(currentFamilyId);
                                    while (currentFamilyId < familyId) {
                                        lsS.add(new Int2Struct(currentFamilyId, 0));
                                        currentFamilyId = fi.nextFamilyId(currentFamilyId);
                                    }
                                    dc.clear();
                                } else {
                                    bNotFirst = true;
                                }
                                currentFamilyId = familyId;
                                dc.add(iValue);
                            }
                        }
                        if (dc.count() > 0) {
                            lsS.add(new Int2Struct(currentFamilyId, dc.count()));
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
            lsS.clear();
        }
        return lsS;
    }

    public HashMap<Integer, Integer> toHashMap(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 1) {
                            while (rs.next()) {
                                map.put(rs.getInt(1), rs.getInt(2));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }

        return map;
    }

    public HashMap<Integer, Long> toHashMapLong(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, Long> map = new HashMap<Integer, Long>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 1) {
                            while (rs.next()) {
                                map.put(rs.getInt(1), rs.getLong(2));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }

        return map;
    }

    public HashMap<Integer, Long[]> toHashMapLongs(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, Long[]> map = new HashMap<Integer, Long[]>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 2) {
                            while (rs.next()) {
                                Long[] lsValues = new Long[iColumn - 1];
                                for (int i = 2; i <= iColumn; i++) {
                                    lsValues[i - 2] = rs.getLong(i);
                                }
                                map.put(rs.getInt(1), lsValues);
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return map;
    }

    public HashMap<Integer, Integer[]> toHashMapIntegers(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, Integer[]> map = new HashMap<Integer, Integer[]>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 2) {
                            while (rs.next()) {
                                Integer[] lsValues = new Integer[iColumn - 1];
                                for (int i = 2; i <= iColumn; i++) {
                                    lsValues[i - 2] = rs.getInt(i);
                                }
                                map.put(rs.getInt(1), lsValues);
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return map;
    }

    public HashMap<Long, String> toHashMapLongString(String sSql) {
        SqlLog.log(sSql);
        HashMap<Long, String> map = new HashMap<Long, String>();

        try {
            Statement sm = mConn.createStatement();
            if (sm != null) {
                ResultSet rs = sm.executeQuery(sSql);
                if (rs != null) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int iColumn = rsmd.getColumnCount();
                    if (iColumn > 1) {
                        while (rs.next()) {
                            map.put(rs.getLong(1), rs.getString(2));
                        }
                    }
                    rs.close();
                }
                sm.close();
            }
        } catch (Exception e) {
            Log.logError(e.getMessage() + ":" + sSql);
        }

        return map;
    }

    public HashMap<Integer, String> toHashMapString(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 1) {
                            while (rs.next()) {
                                map.put(rs.getInt(1), rs.getString(2));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }

        return map;
    }

    public HashMap<Integer, String[]> toHashMapStrings(String sSql) {
        SqlLog.log(sSql);
        HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 2) {
                            while (rs.next()) {
                                String[] lsValues = new String[iColumn - 1];
                                for (int i = 2; i <= iColumn; i++) {
                                    lsValues[i - 2] = rs.getString(i);
                                }
                                map.put(rs.getInt(1), lsValues);
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }

        return map;
    }

    public HashMap<String, String> toHashMapStringString(String sSql) {
        SqlLog.log(sSql);
        HashMap<String, String> map = new HashMap<String, String>();
        for (int k = 0; k < 2; k++) {
            try {
                Statement sm = mConn.createStatement();
                if (sm != null) {
                    ResultSet rs = sm.executeQuery(sSql);
                    if (rs != null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int iColumn = rsmd.getColumnCount();
                        if (iColumn > 1) {
                            while (rs.next()) {
                                map.put(rs.getString(1), rs.getString(2));
                            }
                        }
                        rs.close();
                    }
                    sm.close();
                    break;
                }
            } catch (Exception e) {
                Log.logError(e.getMessage() + ":" + sSql);
            }
            check();
        }
        return map;
    }

    public long countData(String sTable) {
        String sSql = "select count(1) from " + sTable;
        return selectLong(sSql);
    }
}
