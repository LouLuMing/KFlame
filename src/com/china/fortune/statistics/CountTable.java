package com.china.fortune.statistics;

import com.china.fortune.database.sql.WhereSql;
import com.china.fortune.easy.Int2Struct;
import com.china.fortune.os.database.DbAction;
import com.china.fortune.statistics.count.DistinctData;
import com.china.fortune.statistics.family.DayFamily;

import java.util.ArrayList;

public class CountTable {
    public enum TicketType {
        Milsecond, Second, Hour, Day
    };

    public String sTable;
    public String sField;
    public String sTicket;
    public TicketType tType = TicketType.Second;

    public ArrayList<Int2Struct> countDistinctDay(DbAction dbObj, WhereSql ws) {
        DistinctData dd = new DistinctData();
        DayFamily wf = new DayFamily();
        return dbObj.countPerSpan(dd, wf, "IOU", "lendUserId", "confirmTicket", ws);
    }


}
