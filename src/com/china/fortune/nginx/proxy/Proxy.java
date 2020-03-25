package com.china.fortune.nginx.proxy;

import java.io.File;

public class Proxy {
    static private String sDefFile = File.separatorChar + "index.html";

    public String sPath;
    public String sServer;
    public int iPort;
    public int iError;

    public void setResult(boolean b) {
        if (b) {
            iError=0;
        } else {
            iError++;
        }
    }

    public boolean isActive() {
        return iError < 3;
    }

    public String getLocation(String sSubResource) {
        int index = sSubResource.indexOf('?', 0);
        if (index > 0) {
            sSubResource = sSubResource.substring(0, index);
        }
        if (sSubResource.length() > 1) {
            if (File.separatorChar != '/') {
                sSubResource = sSubResource.replace('/', File.separatorChar);
            }
        } else {
            sSubResource = sDefFile;
        }
        return sPath + sSubResource;
    }
}
