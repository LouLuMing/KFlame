package com.china.fortune.string;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

import com.china.fortune.common.ByteAction;
import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;

public class StringUtils {
    static public char cStartVisibleChar = ' ';
    static public String urlToFile(String sUrl) {
        StringBuilder sb = new StringBuilder();
        if (sUrl != null) {
            for (int i = 0; i < sUrl.length(); i++) {
                char c = sUrl.charAt(i);
                if ((c < 48 || c > 57) && (c < 65 || c > 122) && c != 46) {
                    if (c == 47) {
                        sb.append('_');
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    static public boolean isAlpha(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    static public boolean isVisibleChar(char ch) {
        return (ch >= ' ' && ch <= '~');
    }

    static public boolean isVisibleChar(String sText) {
        boolean hz = true;
        if (sText != null && sText.length() > 0) {
            for (int i = 0; i < sText.length(); i++) {
                char ch = sText.charAt(i);
                if (ch < ' ' || ch > '~') {
                    hz = false;
                    break;
                }
            }
        }
        return hz;
    }

    static public boolean isNumberOrAlpha(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
    }

    static public boolean isNumberOrAlpha(String sText) {
        boolean hz = false;
        if (sText != null && sText.length() > 0) {
            hz = true;
            for (int i = 0; i < sText.length(); i++) {
                if (!isNumberOrAlpha(sText.charAt(i))) {
                    hz = false;
                    break;
                }
            }
        }
        return hz;
    }

    static public boolean isNumber(char ch) {
        return (ch >= '0' || ch <= '9');
    }

    static public boolean isNumber(String sText) {
        boolean hz = false;
        if (sText != null && sText.length() > 0) {
            hz = true;
            if (isNumber(sText.charAt(0)) || sText.charAt(0) == '-') {
                for (int i = 1; i < sText.length(); i++) {
                    if (!isNumber(sText.charAt(i))) {
                        hz = false;
                        break;
                    }
                }
            }
        }
        return hz;
    }

    static public int getSigner(String sText) {
        int iSigner = 0;
        for (int i = 0; i < sText.length(); i++) {
            iSigner += sText.charAt(i);
        }
        return iSigner;
    }

    static public long toLong(char[] ch) {
        long hz = 0;
        boolean bNormal = true;
        if (ch != null && ch.length > 0) {
            if (ch[0] == '-') {
                bNormal = false;
            }
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] >= '0' && ch[i] <= '9') {
                    hz *= 10;
                    hz += (ch[i] - '0');
                } else if (ch[i] == '.') {
                    break;
                }
            }
        }
        if (bNormal) {
            return hz;
        }
        return -hz;
    }

    static public long toLong(String strText) {
        if (strText != null && strText.length() > 0) {
            return toLong(strText.toCharArray());
        }
        return 0;
    }

    public static int[] stringsToInts(String[] lsNumber) {
        if (lsNumber != null) {
            int[] lsInt = new int[lsNumber.length];
            for (int i = 0; i < lsNumber.length; i++) {
                lsInt[i] = toInteger(lsNumber[i]);
            }
            return lsInt;
        }
        return null;
    }

    static public int toInteger(char[] ch) {
        int hz = 0;
        boolean bNormal = true;
        if (ch != null && ch.length > 0) {
            if (ch[0] == '-') {
                bNormal = false;
            }
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] >= '0' && ch[i] <= '9') {
                    hz *= 10;
                    hz += (ch[i] - '0');
                } else if (ch[i] == '.') {
                    break;
                }
            }
        }
        if (bNormal) {
            return hz;
        }
        return -hz;
    }

    static public int toInteger(String strText) {
        if (strText != null && strText.length() > 0) {
            return toInteger(strText.toCharArray());
        }
        return 0;
    }

    static public String filterNumber(String sText, int iStart) {
        StringBuilder sb = new StringBuilder();
        if (sText != null && sText.length() > 0) {
            char[] ch = sText.toCharArray();
            for (int i = iStart; i < sText.length(); i++) {
                if (ch[i] >= '0' && ch[i] <= '9') {
                    sb.append(ch[i]);
                }
            }
        }
        return sb.toString();
    }

    static public String filterNumberAndDot(String sText, int iStart) {
        StringBuilder sb = new StringBuilder();
        if (sText != null && sText.length() > 0) {
            char[] ch = sText.toCharArray();
            for (int i = iStart; i < sText.length(); i++) {
                if ((ch[i] >= '0' && ch[i] <= '9') || ch[i] == '.' || ch[i] == '-') {
                    sb.append(ch[i]);
                }
            }
        }
        return sb.toString();
    }

    static public String decode(ByteBuffer buffer, String textCode) {
        String rs = null;
        try {
            buffer.flip();
            Charset charset = Charset.forName(textCode);
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            rs = charBuffer.toString();
        } catch (Exception e) {
            String sText = "StringAction:decode:" + e.getMessage();
            Log.log(sText);
        }
        return rs;
    }

    static public String findBetweenOrEnd(String sSrc, String sFrom, String sTo) {
        int iFrom = sSrc.indexOf(sFrom);
        if (iFrom >= 0) {
            iFrom += sFrom.length();
            int iTo = sSrc.indexOf(sTo, iFrom);
            if (iTo > iFrom) {
                return sSrc.substring(iFrom, iTo);
            } else {
                return sSrc.substring(iFrom);
            }
        }
        return null;
    }

    static public String findBetween(String sSrc, String sFrom, String sTo) {
        int iFrom = sSrc.indexOf(sFrom);
        if (iFrom >= 0) {
            iFrom += sFrom.length();
            int iTo = sSrc.indexOf(sTo, iFrom);
            if (iTo > iFrom) {
                return sSrc.substring(iFrom, iTo);
            }
        }
        return null;
    }

    static public String findBetween(String sSrc, int iSrc, String sFrom, String sTo) {
        int iFrom = sSrc.indexOf(sFrom, iSrc);
        if (iFrom >= 0) {
            iFrom += sFrom.length();
            int iTo = sSrc.indexOf(sTo, iFrom);
            if (iTo > iFrom) {
                return sSrc.substring(iFrom, iTo);
            }
        }
        return null;
    }

    static public String findBetween(String sSrc, int iSrc, char sFrom, char sTo) {
        int iFrom = sSrc.indexOf(sFrom, iSrc);
        if (iFrom >= 0) {
            iFrom += 1;
            int iTo = sSrc.indexOf(sTo, iFrom);
            if (iTo > iFrom) {
                return sSrc.substring(iFrom, iTo);
            }
        }
        return null;
    }

    static public String replaceBetween(String strSrc, String strFrom, String strTo, String r) {
        int iFrom = strSrc.indexOf(strFrom);
        if (iFrom >= 0) {
            iFrom += strFrom.length();
            int iTo = strSrc.indexOf(strTo, iFrom);
            if (iTo > iFrom) {
                return strSrc.substring(0, iFrom) + r + strSrc.substring(iTo, strSrc.length());
            }
        }
        return strSrc;
    }

//	static public String getBefore(String sText, String symbol) {
//		int iPos = sText.indexOf(symbol);
//		if (iPos > 0) {
//			return sText.substring(0, iPos);
//		}
//		return null;
//	}

    static public String getBefore(String sText, String symbol) {
        int iPos = sText.indexOf(symbol);
        if (iPos > 0) {
            return sText.substring(0, iPos);
        }
        return sText;
    }

    static public String getAfter(String sText, String symbol) {
        int iPos = sText.indexOf(symbol);
        if (iPos >= 0) {
            return sText.substring(iPos + symbol.length(), sText.length());
        }
        return null;
    }

//	static public String getAfterN(String sText, String symbol) {
//		int iPos = sText.indexOf(symbol);
//		if (iPos >= 0) {
//			return sText.substring(iPos + symbol.size(), sText.size());
//		}
//		return sText;
//	}

    static public String[] split(String sText, char symbol) {
        return split(sText, symbol, -1);
    }

    static public String[] split(String sText, char symbol, int iMax) {
        String[] lsRs = null;
        int iCount = 0;
        boolean bFound = false;

        // char[] lsText = sText.toCharArray();
        // for (char cText : lsText) {
        // if (cText != symbol) {
        // if (bFound == false) {
        // iCount++;
        // bFound = true;
        // }
        // }
        // else if (bFound){
        // bFound = false;
        // }
        // }
        int iText = sText.length();
        for (int i = 0; i < iText; i++) {
            char cText = sText.charAt(i);
            if (cText != symbol) {
                if (bFound == false) {
                    iCount++;
                    bFound = true;
                }
            } else if (bFound) {
                bFound = false;
            }
        }
        if (iCount > 0) {
            lsRs = new String[iCount];
            bFound = false;
            int iPos = -1;
            iCount = 0;
            for (int i = 0; i < iText; i++) {
                char cText = sText.charAt(i);
                if (cText != symbol) {
                    if (bFound == false) {
                        iPos = i;
                        bFound = true;
                    }
                    if (i == iText - 1 || iCount == iMax - 1) {
                        lsRs[iCount] = sText.substring(iPos);
                        break;
                    }
                } else if (bFound) {
                    lsRs[iCount++] = sText.substring(iPos, i);
                    bFound = false;
                }
            }
        }
        return lsRs;
    }

    static public String trim(String sText) {
        if (sText != null) {
            return sText.trim();
        }
        return null;
    }

    static public String intToFloat(int iInt, int iDiv) {
        if (iDiv > 0) {
            StringBuffer sFloat = new StringBuffer();
            int iHead = iInt;
            if (iInt < 0) {
                iHead = -iInt;
                sFloat.append('-');
            }
            char[] lsTail = new char[iDiv];
            for (int i = 0; i < iDiv; i++) {
                lsTail[iDiv - i - 1] = (char) (iHead % 10 + '0');
                iHead = iHead / 10;
            }
            sFloat.append(iHead);
            sFloat.append('.');
            sFloat.append(lsTail);
            return sFloat.toString();
        } else {
            return String.valueOf(iInt);
        }
    }

    static public String floatToInt(String sNumber, int iMul) {
        if (iMul > 0) {
            StringBuffer sFloat = new StringBuffer(sNumber.length() + iMul);
            char[] lsNum = sNumber.toCharArray();
            int iLeftPoint = iMul;
            boolean bSubLeftPoint = false;
            for (int i = 0; i < lsNum.length; i++) {
                if (bSubLeftPoint) {
                    if (iLeftPoint == 0) {
                        break;
                    }
                    iLeftPoint--;
                }
                if (lsNum[i] != '.') {
                    sFloat.append(lsNum[i]);
                } else {
                    bSubLeftPoint = true;
                }
            }
            for (int i = 0; i < iLeftPoint; i++) {
                sFloat.append('0');
            }
            return sFloat.toString();
        }
        return sNumber;
    }

    static public String multi(String sNumber, int iMul) {
        if (iMul > 0) {
            StringBuffer sFloat = new StringBuffer(sNumber.length() + iMul);
            char[] lsNum = sNumber.toCharArray();
            int iLeftPoint = iMul;
            boolean bSubLeftPoint = false;
            for (int i = 0; i < lsNum.length; i++) {
                if (bSubLeftPoint) {
                    if (iLeftPoint == 0) {
                        sFloat.append('.');
                        bSubLeftPoint = false;
                    }
                    iLeftPoint--;
                }
                if (lsNum[i] != '.') {
                    sFloat.append(lsNum[i]);
                } else {
                    bSubLeftPoint = true;
                }
            }
            for (int i = 0; i < iLeftPoint; i++) {
                sFloat.append('0');
            }
            return sFloat.toString();
        }
        return sNumber;
    }

    static public int indexOf(String sSrc, char sFind, int iLoop) {
        int iIndex = 0;
        for (int i = 0; i < iLoop; i++) {
            iIndex = sSrc.indexOf(sFind, iIndex);
            if (iIndex < 0) {
                break;
            }
        }
        return iIndex;
    }

    static public int indexOf(String sSrc, String sFind, int iLoop) {
        int iIndex = 0;
        for (int i = 0; i < iLoop; i++) {
            iIndex = sSrc.indexOf(sFind, iIndex);
            if (iIndex < 0) {
                break;
            }
        }
        return iIndex;
    }

    static public int hexToInt(String sLine) {
        int i = 0;
        if (sLine != null) {
            for (int j = 0; j < sLine.length(); j++) {
                char obj = sLine.charAt(j);
                i *= 16;
                if (obj >= 'a' && obj <= 'f') {
                    i += (obj - 'a' + 10);
                } else if (obj >= '0' && obj <= '9') {
                    i += (obj - '0');
                } else if (obj >= 'A' && obj <= 'F') {
                    i += (obj - 'a' + 10);
                }
            }
        }
        return i;
    }

    static public String toHex(String sLine) {
        byte[] pByte = sLine.getBytes();
        return ByteAction.toHexString(pByte);
    }

    static public int compareTo(String s1, String s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return -1;
        } else if (s2 == null) {
            return 1;
        } else {
            return s1.compareTo(s2);
        }
    }

    static public boolean compareTo(String s1, int iStart, String s2) {
        boolean rs = false;
        int iLen = s2.length();
        if (s1.length() >= iStart + iLen) {
            rs = true;
            for (int i = 0; i < iLen; i++) {
                if (s1.charAt(iStart + i) != s2.charAt(i)) {
                    rs = false;
                }
            }
        }
        return rs;
    }

    static public int compareToIgnoreCase(String s1, String s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return -1;
        } else if (s2 == null) {
            return 1;
        } else {
            return s1.compareToIgnoreCase(s2);
        }
    }

    static public boolean startWithIgnoreCase(String sText, String sCmd) {
        boolean b = true;
        if (sText.length() > sCmd.length()) {
            for (int i = 0; i < sCmd.length(); i++) {
                char cText = sText.charAt(i);
                if (cText >= 'A' && cText <= 'Z') {
                    if (cText + ('a' - 'A') != sCmd.charAt(i)) {
                        b = false;
                        break;
                    }
                } else if (cText != sCmd.charAt(i)) {
                    b = false;
                    break;
                }
            }
        }
        return b;
    }

    static public int length(String s1) {
        int iLen = 0;
        if (s1 != null) {
            iLen = s1.length();
        }
        return iLen;
    }

    static public String max(String s1, String s2) {
        if (s1 == null) {
            return s2;
        } else if (s2 == null) {
            return s1;
        } else {
            if (s1.compareTo(s2) > 0) {
                return s1;
            } else {
                return s2;
            }
        }
    }

    static public int calHashCode(String sTag, int span) {
        int iSigner = 0;
        int i = 0;
        if (sTag != null) {
            while (i < sTag.length()) {
                iSigner += (sTag.charAt(i) - ' ');
                i += span;
            }
        }
        return iSigner;
    }

    static public int calSigner(String sTag) {
        int iSigner = 0;
        for (int i = 0; i < sTag.length(); i++) {
            iSigner += (sTag.charAt(i) - ' ');
        }
        return iSigner;
    }

//    static public String assembleString(int[] lsObj, char dot) {
//        if (lsObj != null) {
//            StringBuilder sb = new StringBuilder();
//            for (int obj : lsObj) {
//                sb.append(obj);
//                sb.append(dot);
//            }
//            if (sb.length() > 0) {
//                return sb.substring(0, sb.length() - 1);
//            } else {
//                return sb.toString();
//            }
//        }
//        return null;
//    }
//
//    static public String assembleString(HashSet<Integer> lsObj, char dot) {
//        StringBuilder sb = new StringBuilder();
//        for (Integer obj : lsObj) {
//            sb.append(obj);
//            sb.append(dot);
//        }
//        if (sb.length() > 0) {
//            return sb.substring(0, sb.length() - 1);
//        } else {
//            return sb.toString();
//        }
//    }
//
//    static public String assembleString(ArrayList<Integer> lsObj, char dot) {
//        StringBuilder sb = new StringBuilder();
//        for (Integer obj : lsObj) {
//            sb.append(obj);
//            sb.append(dot);
//        }
//        if (sb.length() > 0) {
//            return sb.substring(0, sb.length() - 1);
//        } else {
//            return sb.toString();
//        }
//    }

    static public byte[] getBytes(String sData, String sCode) {
        byte[] bData = null;
        if (length(sData) > 0) {
            try {
                bData = sData.getBytes(sCode);
            } catch (Exception e) {
                Log.logClass(e.getMessage());
            }
        }
        return bData;
    }

    static public String toString(byte[] bData, String sCode) {
        String sData = null;
        if (bData != null && bData.length > 0) {
            try {
                sData = new String(bData, 0, bData.length, sCode);
            } catch (Exception e) {
                Log.logClass(e.getMessage());
            }
        }
        return sData;
    }

    static public String toPercent(int iData, int iTotal) {
        StringBuilder sb = new StringBuilder();
        int iLoop = 0;
        if (iData > 0) {
            do {
                iData *= 100;
                iLoop++;
                int iPercent = iData / iTotal;
                if (iPercent > 0) {
                    sb.append(iPercent);
                    if (iPercent < 10) {
                        sb.append('.');
                        sb.append(iData * 10 / iTotal % 10);
                    }
                    for (int i = 0; i < iLoop; i++) {
                        sb.append('%');
                    }
                    break;
                }
            } while (true);
            return sb.toString();
        } else {
            return "0%";
        }
    }

    static public String filterBase64(String sRecv) {
        char[] lsChars = sRecv.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lsChars.length; i++) {
            if ((lsChars[i] >= 'a' && lsChars[i] <= 'z') || (lsChars[i] >= 'A' && lsChars[i] <= 'Z')
                    || (lsChars[i] >= '0' && lsChars[i] <= '9') || lsChars[i] == '+' || lsChars[i] == '/'
                    || lsChars[i] == '=') {
                sb.append(lsChars[i]);
            }
        }
        return sb.toString();
    }

    static public String intToMKB(int iSize) {
        if (iSize < 1000) {
            return String.valueOf(iSize);
        } else if (iSize < 1000 * 1000) {
            int iHead = iSize / 1000;
            int iTail = iSize % 1000;
            if (iHead < 10) {
                return String.format("%d.%02dKB", iHead, (iTail / 10));
            } else if (iHead < 100) {
                return String.format("%d.%01dKB", iHead, (iTail / 100));
            } else {
                return iHead + "KB";
            }
        } else {
            int iHead = iSize / (1000 * 1000);
            int iTail = (iSize % (1000 * 1000)) / 1000;
            if (iHead < 10) {
                return String.format("%d.%02dMB", iHead, (iTail / 10));
            } else if (iHead < 100) {
                return String.format("%d.%01dMB", iHead, (iTail / 10));
            } else {
                return iHead + "MB";
            }
        }
    }

    static public String urlDecode(String sValue, String sCharset) {
        String sResult = null;
        if (sValue != null) {
            try {
                sResult = URLDecoder.decode(sValue, sCharset);
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        return sResult;
    }

    static public String urlDecode(String sValue) {
        return urlDecode(sValue, "utf-8");
    }

    static public String urlEncode(String sValue) {
        return urlEncode(sValue, "utf-8");
    }

    public static String urlEncode(String sUrl, String sCharset) {
        String sData = null;
        if (sUrl != null) {
            try {
                sData = URLEncoder.encode(sUrl, sCharset);
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        return sData;
    }

    public static String[] arrayListToStrings(ArrayList<String> lsKey) {
        String[] lsStr = null;
        if (lsKey != null) {
            int iLen = lsKey.size();
            if (iLen > 0) {
                lsStr = new String[iLen];
                for (int i = 0; lsKey != null && i < iLen; i++) {
                    lsStr[i] = lsKey.get(i);
                }
            }
        }
        return lsStr;
    }

    static public int stringsCount(String[] lsData) {
        int iSize = 0;
        if (lsData != null) {
            for (String s : lsData) {
                if (StringUtils.length(s) > 0) {
                    iSize++;
                }
            }
        }
        return iSize;
    }

    public static String[] appendStrings(String[] key1, String[] key2) {
        if (key1 == null) {
            return key2;
        } else if (key2 == null) {
            return key1;
        } else {
            String[] lsStr = new String[key1.length + key2.length];
            int iIndex = 0;
            for (int i = 0; i < key1.length; i++) {
                lsStr[iIndex++] = key1[i];
            }
            for (int i = 0; i < key2.length; i++) {
                lsStr[iIndex++] = key2[i];
            }
            return lsStr;
        }
    }

    public static int findString(String[] lsData, String s) {
        for (int i = 0; i < lsData.length; i++) {
            if (s.equals(lsData[i])) {
                return i;
            }
        }
        return -1;
    }

    static public String fenToYuan(long money) {
        long yuan = money / 100;
        int fen = (int) (money % 100);
        if (fen == 0) {
            return String.valueOf(yuan);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(yuan);
            sb.append('.');
            if (fen > 9) {
                int iJiao = fen / 10;
                int iFen = fen % 10;
                sb.append(iJiao);
                if (iFen > 0) {
                    sb.append(iFen);
                }
            } else {
                sb.append('0');
                sb.append(fen);
            }
            return sb.toString();
        }
    }

    static public String fenToYuan(int money) {
        return fenToYuan((long) (money));
    }

    static public int yuanToFen(String sYuan) {
        int iFen = 0;
        char[] lsNum = sYuan.toCharArray();
        int iLeftFen = 2;
        boolean bFoundDot = false;
        for (int i = 0; i < lsNum.length && iLeftFen > 0; i++) {
            if (lsNum[i] >= '0' && lsNum[i] <= '9') {
                iFen *= 10;
                iFen += (lsNum[i] - '0');
                if (bFoundDot) {
                    iLeftFen--;
                }
            } else if (lsNum[i] == '.') {
                bFoundDot = true;
            }
        }
        if (iLeftFen > 1) {
            iFen *= 10;
        }
        if (iLeftFen > 0) {
            iFen *= 10;
        }
        return iFen;
    }

    public static String newString(byte[] bData, String sCharaset) {
        String sLine = null;
        try {
            sLine = new String(bData, ConstData.sHttpCharset);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return sLine;
    }

    public static String newString(ByteBuffer bb, int iOff, int iLen) {
        byte[] bData = ByteBufferUtils.toByte(bb, iOff, iLen);
        try {
            return new String(bData);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return null;
    }

    public static String newString(ByteBuffer bb, int iOff, int iLen, String sCharaset) {
        byte[] bData = ByteBufferUtils.toByte(bb, iOff, iLen);
        try {
            return new String(bData, sCharaset);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return null;
    }

    public static String newString(byte[] bData, int iOff, int iLen) {
        String sLine = null;
        try {
            sLine = new String(bData, iOff, iLen, ConstData.sHttpCharset);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return sLine;
    }

    public static String newString(byte[] bData, int iOff, int iLen, String sCharaset) {
        String sLine = null;
        try {
            if (iOff + iLen > bData.length) {
                iLen = bData.length - iOff;
            }
            sLine = new String(bData, iOff, iLen, sCharaset);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return sLine;
    }

    static public String replace(String sDes, char s, char d) {
        char[] lsChars = sDes.toCharArray();
        for (int i=0; i < lsChars.length; i++) {
            if (lsChars[i] == s) {
                lsChars[i] = d;
            }
        }
        return new String(lsChars);
    }

    public static String repeatAlpha(char c, int iLen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iLen; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Log.log(StringUtils.max(null, "1232"));
        Log.log(StringUtils.max("1232", null));
        Log.log(StringUtils.max("132", "1232"));

        String sText = "message";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "r";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "gpmsg";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "action";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "a";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "notify";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "m";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "client";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "exchange";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "servers";
        Log.log(sText + ":" + StringUtils.getSigner(sText));
        sText = "clear";
        Log.log(sText + ":" + StringUtils.getSigner(sText));

        Log.log("\n");
        String sSelect = " userId,phonenum,password,nickname,gender,signature,gold,coin,avatar ";
        String[] lsKey = StringUtils.split(sSelect, ',');
        for (String key : lsKey) {
            Log.log(key);
        }

        Log.log(intToMKB(1021));

        Log.log("" + yuanToFen("0.01"));
        Log.log("" + yuanToFen("11.01"));
        Log.log("" + yuanToFen("11.0111"));
        Log.log("" + yuanToFen("11.0"));
        Log.log("" + yuanToFen("110"));

        Log.log("" + calSigner("insert"));
        Log.log("" + calSigner("update"));
        Log.log("" + calSigner("delete"));
        Log.log("" + calSigner("replac"));
        Log.log("" + calSigner("select"));

        Log.log("" + fenToYuan(1000));
        Log.log("" + fenToYuan(1001));
        Log.log("" + fenToYuan(1011));
        Log.log("" + fenToYuan(11));
        Log.log("" + hexToInt("1a72"));

        String sData = "Get /Hello HTTP 1.1";
        ByteBuffer bb = ByteBuffer.allocate(1000);
        try {
            bb.put(sData.getBytes("utf-8"));

            Log.logClass(newString(bb.array(), 0, bb.position()+1));
            Log.logClass(newString(bb, 0, bb.position()+1));

            Log.logClass(bb.array()[0] + " " + bb.get(0));
        } catch (Exception e) {

        }
    }

}
