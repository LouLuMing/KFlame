package com.china.fortune.database.maintain;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.SyncDatabase;

public class DBCompare {
    public static void main(String[] args) {
        String sMySqlIP1 = "20.21.1.140";
        String sMySqlUser1 = "root";
        String sMySqlPasswd1 = "1qaz@WSX";
        String sMySqlDBName1 = "visitor_v4_sgs";

        String sMySqlIP2 = "115.159.71.91:8187";
        String sMySqlUser2 = "root";
        String sMySqlPasswd2 = "zjrc@Xwyk#2019";
        String sMySqlDBName2 = "visitor_v4";
        
        MySqlDbAction dbObj1 = new MySqlDbAction();
        dbObj1.open(sMySqlIP1, sMySqlDBName1, sMySqlUser1, sMySqlPasswd1);
        MySqlDbAction dbObj2 = new MySqlDbAction();
        dbObj2.open(sMySqlIP2, sMySqlDBName2, sMySqlUser2, sMySqlPasswd2);

        SyncDatabase sd = new SyncDatabase(sMySqlIP1 + " " + sMySqlDBName1, dbObj1, sMySqlIP2 + " " + sMySqlDBName2, dbObj2);
//        sd.showMissTables();
        sd.showMissTableAndColnum();

        dbObj1.close();
        dbObj2.close();
    }
}
