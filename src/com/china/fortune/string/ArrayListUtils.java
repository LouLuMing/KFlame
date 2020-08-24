package com.china.fortune.string;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayListUtils {
    public void sortString(ArrayList<String> lsData) {
        Collections.sort(lsData, String.CASE_INSENSITIVE_ORDER);
    }
}
