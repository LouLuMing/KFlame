package com.china.fortune.string;

public class FastHtmlPaser {
    static public String getValue(String sText, int iStart, String sPrev, String sKey) {
        int iFrom = sText.indexOf(sPrev, iStart);
        if (iFrom >= 0) {
            return StringAction.findBetween(sText,
                    iFrom, sKey + "=\"", "\"");
        } else {
            return null;
        }
    }

    static public String getValue(String sText, int iStart, String sKey) {
        return StringAction.findBetween(sText,
                iStart, sKey + "=\"", "\"");
    }

    static public String getElementByAttribue(String sText, int iStart, String sAttr) {
        int iFrom = sText.indexOf(sAttr, iStart);
        if (iFrom >= 0) {
            return StringAction.findBetween(sText,
                    iFrom, ">", "</");
        } else {
            return null;
        }
    }

    static public String getElement(String sText, int iStart, String sTag) {
        int iFrom = sText.indexOf(sTag, iStart);
        if (iFrom >= 0) {
            return StringAction.findBetween(sText,
                    iFrom, ">", "</");
        } else {
            return null;
        }
    }

}
