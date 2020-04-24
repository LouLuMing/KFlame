package com.china.fortune.proxy.host;

import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringAction;

import java.io.File;

public class Host {
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
            sSubResource = StringAction.urlDecode(sSubResource, "utf-8");
            if (File.separatorChar != '/') {
                sSubResource = sSubResource.replace('/', File.separatorChar);
            }
        } else {
            sSubResource = sDefFile;
        }
        return sPath + sSubResource;
    }

    public void parseURL(String sURL) {
        if (sURL != null) {
            iPort = 80;
            int iIndex = sURL.indexOf("://");
            if (iIndex > 0) {
                String sTmp = sURL.substring(iIndex+3);
                iIndex = sTmp.indexOf('/');
                if (iIndex > 0) {
                    sTmp = sTmp.substring(0, iIndex);
                }
                iIndex = sTmp.indexOf(':');
                if (iIndex > 0) {
                    sServer = sTmp.substring(0, iIndex);
                    String sPort = sTmp.substring(iIndex+1);
                    iPort = StringAction.toInteger(sPort);
                } else {
                    sServer = sTmp;
                }
            } else {
                Log.logError(sURL);
            }
        }
    }

    public void update(String path) {
        if (path.startsWith("http")
                || path.startsWith("ws")) {
            parseURL(path);
            sPath = path;
        } else {
            sPath = PathUtils.getFullPath(path);
            sPath = PathUtils.delSeparator(path);
        }
        iError = 0;
    }

    public static void main(String[] args) {
        String sUrl = "http://127.0.0.1:8080";
        Host py = new Host();
        py.parseURL(sUrl);
    }
}
