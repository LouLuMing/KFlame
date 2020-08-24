package com.china.fortune.database.sql;

import com.china.fortune.database.annotation.KeyField;
import com.china.fortune.global.Log;
import com.china.fortune.struct.FastList;

import java.lang.reflect.Field;

public class CreateTableSql {
    private String sTable;
    private FastList<SqlItem> lsObject = new FastList<SqlItem>();
    private FastList<String> lsIndex = new FastList<String>();

    public CreateTableSql() {
    }

    public CreateTableSql(String table) {
        sTable = table;
    }

    public void setTable(String table) {
        sTable = table;
    }

    public void clear() {
        lsObject.clear();
        lsIndex.clear();
    }

    public void addField(String field) {
        SqlItem s2S = getField(field);
        if (s2S == null) {
            lsObject.add(new SqlItem(field));
        }
    }

    private SqlItem getField(String sfield) {
        for (int i = 0; i < lsObject.size(); i++) {
            SqlItem s2S = lsObject.get(i);
            if (sfield.compareTo(s2S.sField) == 0) {
                return s2S;
            }
        }
        return null;
    }

    private void addItem(String field, String value, keyType t) {
        SqlItem s2S = getField(field);
        if (s2S == null) {
            s2S = new SqlItem(field, value, t);
            lsObject.add(s2S);
        } else {
            s2S.sValue = value;
            s2S.iType = t;
        }
    }

    private void addItem(int i, String value, keyType t) {
        if (i >= 0 && i < lsObject.size()) {
            SqlItem s2S = lsObject.get(i);
            s2S.sValue = value;
            s2S.iType = t;
        }
    }

    public void addString(String field, String value) {
        if (value != null) {
            addItem(field, value, keyType.DOT);
        }
    }

    public void addString(int i, String value) {
        if (value != null) {
            addItem(i, value, keyType.DOT);
        }
    }

    public void addInt(String name, int value) {
        addItem(name, String.valueOf(value), keyType.NO_DOT);
    }

    public void addInt(int i, int value) {
        addItem(i, String.valueOf(value), keyType.NO_DOT);
    }

    public void addKey(String field, String value) {
        if (value != null) {
            addItem(field, value, keyType.NO_DOT);
        }
    }

    public void addKey(int i, String value) {
        if (value != null) {
            addItem(i, value, keyType.NO_DOT);
        }
    }

    private void addAnnotationField(Field f) {
        int length = 255;
        boolean isPK = false;
        boolean ignore = false;
        if (f.isAnnotationPresent(KeyField.class)) {
            KeyField kf = f.getAnnotation(KeyField.class);
            length = kf.size();
            isPK = kf.isPK();
            if (kf.isIndex()) {
                lsIndex.add(f.getName());
            }
            ignore = kf.ignore();
        }

        if (!ignore) {
            StringBuilder sb = new StringBuilder();
            if (f.getType() == String.class) {
                sb.append("char(");
                sb.append(length);
                sb.append(")");
            } else if (f.getType() == int.class
                    || f.getType() == Integer.class) {
                sb.append("int");
            } else if (f.getType() == long.class
                    || f.getType() == Long.class) {
                sb.append("bigint");
            }
            if (isPK) {
                sb.append(" primary key");
            }
            addString(f.getName(), sb.toString());
        }
    }

    public void addClass(Class<?> c) {
        try {
            sTable = c.getSimpleName();
            Field[] lsFields = c.getFields();
            for (Field f : lsFields) {
                f.setAccessible(true);
                addAnnotationField(f);
            }
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
    }

    public String toSql() {
        StringBuilder sSqlHead = new StringBuilder();
        sSqlHead.append("create table ");
        sSqlHead.append(sTable);
        sSqlHead.append(" (");

        for (int i = 0; i < lsObject.size(); i++) {
            SqlItem s2S = lsObject.get(i);
            sSqlHead.append(s2S.sField);
            sSqlHead.append(' ');
            sSqlHead.append(s2S.sValue);
            sSqlHead.append(',');
        }
        sSqlHead.setLength(sSqlHead.length() - 1);
        sSqlHead.append(");");

        for (int i = 0; i < lsIndex.size(); i++) {
            String sField = lsIndex.get(i);
            sSqlHead.append("create iPort index_");
            sSqlHead.append(sField);
            sSqlHead.append(" on ");
            sSqlHead.append(sTable);
            sSqlHead.append(" (");
            sSqlHead.append(sField);
            sSqlHead.append(");");
        }

        return sSqlHead.toString();
    }

    public static void main(String[] args) {
        CreateTableSql isa = new CreateTableSql("table");
        isa.addString("key1", "value1");
        isa.addString("key2", "value2");
        Log.log(isa.toSql());
    }
}
