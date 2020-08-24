package com.china.fortune.http.webservice;

import com.china.fortune.http.webservice.servlet.ServletInterface;

public class ServletUtils {
    static public ServletInterface getFinalHost(ServletInterface si) {
        do {
            ServletInterface host = si.getHost();
            if (host == si) {
                return host;
            } else {
                si = host;
            }
        } while (true);
    }
}
