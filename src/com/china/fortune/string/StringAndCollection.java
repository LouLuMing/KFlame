package com.china.fortune.string;

import java.util.ArrayList;
import java.util.HashSet;

public class StringAndCollection {
    static public HashSet<Integer> toHashSet(String sData, char dot) {
        HashSet<Integer> lsData = new HashSet<Integer>();
        if (sData != null) {
            String[] lsInt = StringAction.split(sData, dot);
            for (String s : lsInt) {
                lsData.add(StringAction.toInteger(s));
            }
        }
        return lsData;
    }

    static public ArrayList<Integer> toArrayList(String sData, char dot) {
        ArrayList<Integer> lsData = new ArrayList<Integer>();
        if (sData != null) {
            String[] lsInt = StringAction.split(sData, dot);
            for (String s : lsInt) {
                lsData.add(StringAction.toInteger(s));
            }
        }
        return lsData;
    }

    static public String assembleString(HashSet<Integer> lsData, char dot) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : lsData) {
            sb.append(i);
            sb.append(dot);
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return null;
        }
    }

    static public String assembleString(ArrayList<Integer> lsData, char dot) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : lsData) {
            sb.append(i);
            sb.append(dot);
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return null;
        }
    }

    static public String assembleString(int[] lsObj, char dot) {
        if (lsObj != null) {
            StringBuilder sb = new StringBuilder();
            for (int obj : lsObj) {
                sb.append(obj);
                sb.append(dot);
            }
            if (sb.length() > 0) {
                return sb.substring(0, sb.length() - 1);
            } else {
                return sb.toString();
            }
        }
        return null;
    }
}
