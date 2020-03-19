package com.china.fortune.target.runSql;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.file.FileHelper;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.xml.XmlNode;

public class SqlTarget implements TargetInterface {
    @Override
    public boolean doAction(XmlNode cfg, ProcessAction self) {
        SqlProp sp = new SqlProp();
        ClassXml.toObject(cfg, sp);
        if (ClassUtils.checkNoNull(sp)) {
            MySqlDbAction dbObj = new MySqlDbAction();
            if (dbObj.open(sp.SqlUrl, sp.SqlDBName, sp.SqlUser, sp.SqlPasswd)) {
                XmlNode SqlFiles = cfg.getChildNode("SqlFiles");
                if (SqlFiles != null) {
                    for (int i = 0; i < SqlFiles.getChildCount(); i++) {
                        XmlNode SqlFile = SqlFiles.getChildNode(i);
                        if (SqlFile != null && "SqlFile".equals(SqlFile.getTag())) {
                            String sFile = SqlFile.getText();
                            if (sFile != null) {
                                String sData = FileHelper.readSmallFile(sFile, "utf-8");
                                if (sData != null) {
                                    dbObj.execute(sData);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String doCommand(String sCmd) {
        return null;
    }

}
