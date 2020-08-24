package com.china.fortune.file;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.string.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class IniFileUtils {
    static public ArrayList<String2Struct> toArrayList(String sFile) {
        ArrayList<String2Struct> lsData = new ArrayList<String2Struct>();
        toArrayList(sFile, lsData);
        return lsData;
    }

    static public void toArrayList(String sFile, ArrayList<String2Struct> lsData) {
        ReadLineFileAction rlfa = new ReadLineFileAction();
        if (rlfa.open(sFile)) {
            do {
                String sLine = rlfa.readLine();
                if (StringUtils.length(sLine) > 0) {
                    if (!sLine.startsWith("//")) {
                        int index = sLine.indexOf('=');
                        if (index > 0) {
                            String sTag = sLine.substring(0, index).trim();
                            String sValue = sLine.substring(index + 1, sLine.length()).trim();
                            lsData.add(new String2Struct(sTag, sValue));
                        }
                    }
                } else {
                    break;
                }
            } while (true);
            rlfa.close();
        }
    }

    static public HashMap<String, String> toHashMap(String sFile) {
        HashMap<String, String> lsData = new HashMap<String, String>();
        toHashMap(sFile, lsData);
        return lsData;
    }

    static public void toHashMap(String sFile, HashMap<String, String> lsData) {
        ReadLineFileAction rlfa = new ReadLineFileAction();
        if (rlfa.open(sFile)) {
            do {
                String sLine = rlfa.readLine();
                if (StringUtils.length(sLine) > 0) {
                    int index = sLine.indexOf('=');
                    if (index > 0) {
                        String sTag = sLine.substring(0, index).trim();
                        String sValue = sLine.substring(index + 1, sLine.length()).trim();
                        lsData.put(sTag, sValue);
                    }
                } else {
                    break;
                }
            } while (true);
            rlfa.close();
        }
    }
}
