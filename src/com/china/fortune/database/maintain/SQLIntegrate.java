package com.china.fortune.database.maintain;

import com.china.fortune.file.ReadLineFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class SQLIntegrate {
    public static void main(String[] args) {
        SQLIntegrate si = new SQLIntegrate();
        StringBuilder sb = new StringBuilder();
        si.integrate(sb, "logSql/myLog2019-08-08.log");
        Log.logNoDate(sb.toString());
    }

    public int integrate(StringBuilder sb, String sFile) {
        int iLines = 0;
        ReadLineFileAction fra = new ReadLineFileAction();
        if (fra.open(sFile)) {
            do {
                String sSql = fra.readLine();
                if (sSql != null) {
                    sSql = sSql.substring(9);
                    if (StringAction.startWithIgnoreCase(sSql, "insert")
                            || StringAction.startWithIgnoreCase(sSql, "update")
                            || StringAction.startWithIgnoreCase(sSql, "delete")) {
                        iLines++;
                        sb.append(sSql);
                        sb.append('\n');
                    }
                } else {
                    break;
                }
            } while (true);
        }
        return iLines;
    }
}
