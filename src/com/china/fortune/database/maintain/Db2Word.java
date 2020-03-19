package com.china.fortune.database.maintain;

import com.china.fortune.database.mySql.MySqlDbAction;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;

public class Db2Word {
    private void addTable(MySqlDbAction dbObj, String sTable, XWPFDocument document) {
        String sRemark = dbObj.getTableComment(sTable);
        XWPFParagraph firstParagraph = document.createParagraph();
        firstParagraph.createRun().addBreak();

        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("表名：" + sTable);
        firstRun.setFontFamily("仿宋");
        firstRun.setFontSize(16);
        //换行
        firstParagraph.createRun().addBreak();

        firstRun = firstParagraph.createRun();
        firstRun.setText("备注：" + sRemark);
        firstRun.setFontFamily("仿宋");
        firstRun.setFontSize(16);


        XWPFTable table = document.createTable();

        CTTblWidth twidth = table.getCTTbl().addNewTblPr().addNewTblW();
        twidth.setType(STTblWidth.DXA);
        twidth.setW(BigInteger.valueOf(8300));

        XWPFTableRow th = table.getRow(0);
        th.getCell(0).setText("列");
        th.addNewTableCell().setText("类型");
        th.addNewTableCell().setText("长度");
        th.addNewTableCell().setText("是否为空");
        th.addNewTableCell().setText("备注");

        ArrayList<ArrayList<String>> llsData = dbObj.selectColumnInfo(sTable);
        for (int i = 0; i < llsData.size(); i++) {
            XWPFTableRow row = table.createRow();
            ArrayList<String> lsData = llsData.get(i);
            for (int j = 0; j < lsData.size(); j++) {
                row.getCell(j).setText(lsData.get(j));
            }
        }
    }

    public void toWord(String sMySqlIP, String sMySqlDBName, String sMySqlUser, String sMySqlPasswd, String sFile) {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph titleParagraph = document.createParagraph();
        // 设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("数据库设计说明书");
        titleRun.setFontSize(20);
        titleRun.setFontFamily("仿宋");
        titleRun.setBold(true);

        MySqlDbAction dbObj = MySqlDbAction.getDatabaseObject(sMySqlIP, sMySqlDBName, sMySqlUser, sMySqlPasswd);
        Db2Word dw = new Db2Word();
        ArrayList<String> lsData = dbObj.selectAllTableName();
        for (int i = 0; i < lsData.size(); i++) {
            dw.addTable(dbObj, lsData.get(i), document);
        }

        dbObj.close();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(sFile));
            document.write(fos);
            document.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        Db2Word dw = new Db2Word();
        dw.toWord("20.21.1.140", "visitor_v3", "root", "1qaz@WSX", "z:\\数据库设计.docx");
    }
}
