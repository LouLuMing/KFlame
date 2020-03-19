package com.china.fortune.database.maintain;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.global.Log;

import java.util.ArrayList;

public class ConnectDB {
    public static void main(String[] args) {
        String sMySqlIP1 = "rm-bp19666x87po95fpp4o.mysql.rds.aliyuncs.com";
        String sMySqlUser1 = "rdsxwyk";
        String sMySqlPasswd1 = "xwyk#2019@zjrc";
        String sMySqlDBName1 = "xwyk";

        MySqlDbAction dbObj1 = new MySqlDbAction();
        dbObj1.open(sMySqlIP1, sMySqlDBName1, sMySqlUser1, sMySqlPasswd1);
        ArrayList<String> ls = dbObj1.selectAllTableName();
        for (String s : ls) {
            Log.log(s);
        }
    }

}
