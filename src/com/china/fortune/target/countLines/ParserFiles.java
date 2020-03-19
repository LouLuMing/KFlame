package com.china.fortune.target.countLines;

import com.china.fortune.file.ReadLineFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringAction;

import java.io.File;

public class ParserFiles {
    static private int iLines = 0;
    static private int iFiles = 0;
    static private int iTotalLines = 0;
    static public void countLines(String sPath) {
        iLines = 0;
        iFiles = 0;
        PathUtils.serachDir(sPath, (File file)->{
            ReadLineFileAction fra = new ReadLineFileAction();
            if (fra.open(file)) {
                iFiles++;
                do {
                    String sSql = fra.readLine();
                    if (sSql != null) {
                        if (StringAction.length(sSql) > 0) {
                            iLines++;
                        }
                    } else {
                        break;
                    }
                } while (true);
            }
            return true;
        });
        iTotalLines += iLines;
        Log.logNoDate(sPath + " files:" + iFiles + " lines:" + iLines + " total:" + iTotalLines);
    }

    public static void main(String[] args) {
        countLines("C:\\Work\\OneDrive\\Code\\Java\\PcLib\\src");

        countLines("C:\\Work\\Visitor\\visitor2.0\\src");
        countLines("C:\\Work\\Visitor\\visitor-app\\app\\src");

        countLines("C:\\Work\\Visitor\\visitor-wechat\\scripts");
        countLines("C:\\Work\\Visitor\\visitor-wechat\\styles");
        countLines("C:\\Work\\Visitor\\visitor-wechat\\views");

        countLines("C:\\Work\\Code\\Vue\\visitor-client\\pages");
        countLines("C:\\Work\\Code\\Vue\\visitor-client\\scripts");
        countLines("C:\\Work\\Code\\Vue\\visitor-client\\styles");

    }
}
