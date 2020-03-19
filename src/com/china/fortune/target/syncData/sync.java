package com.china.fortune.target.syncData;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.sql.InsertMultiSql;
import com.china.fortune.database.sql.UpdateSql;
import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.global.Log;

import java.util.HashMap;

public class sync {
    public static void main(String[] args) {
        String sServer="218.205.111.38:3399";
        String sTable = "visitor";

        String sUser = "root";
        String sPasswd = "zjrc@Xwyk#2019";

        MySqlDbAction dbObj = MySqlDbAction.getDatabaseObject(sServer, sTable, sUser, sPasswd);

        String sSql = "select phone,wx_openid from tb_person where length(wx_openid) > 3;";
        HashMap<String,String> map = dbObj.toHashMapStringString(sSql);

        for (String sPhone : map.keySet()) {
            UpdateSql us = new UpdateSql("tb_person");
            us.addString("wx_openid", map.get(sPhone));
            WhereSql ws = new WhereSql();
            ws.add("phone", "=", sPhone);
            ws.add("wx_openid is null;");
            String sUpdate = us.toSql() + ws.toSql();
            Log.logNoDate(sUpdate);
        }

//		ClassDatabase.deleteTable(dbObj, TemplateData.class);

        dbObj.close();
    }
}
