package com.china.fortune.proxy.host;

import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringUtils;

import java.io.File;
import java.net.InetSocketAddress;

public class Host {
    static private String sDefFile = File.separatorChar + "index.html";

    public String sPath;
    public InetSocketAddress isaRemote;

    public boolean isCache = false;
    public boolean isGZip = false;
    public int iError;

    public void setResult(boolean b) {
        if (b) {
            iError=0;
        } else {
            iError++;
        }
    }

    public boolean isActive() {
        return iError < 16;
    }

    public String getLocation(String sSubResource) {
        int index = sSubResource.indexOf('?', 0);
        if (index > 0) {
            sSubResource = sSubResource.substring(0, index);
        }
        if (sSubResource.length() > 1) {
            sSubResource = StringUtils.urlDecode(sSubResource, "utf-8");
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
            int iIndex = sURL.indexOf("://");
            if (iIndex > 0) {
                int iPort = 80;
                String sServer;
                String sTmp = sURL.substring(iIndex+3);
                iIndex = sTmp.indexOf('/');
                if (iIndex > 0) {
                    sTmp = sTmp.substring(0, iIndex);
                }
                iIndex = sTmp.indexOf(':');
                if (iIndex > 0) {
                    sServer = sTmp.substring(0, iIndex);
                    String sPort = sTmp.substring(iIndex+1);
                    iPort = StringUtils.toInteger(sPort);
                } else {
                    sServer = sTmp;
                }
                isaRemote = new InetSocketAddress(sServer, iPort);
            } else {
                Log.logError(sURL);
            }
        }
    }

    public void update(String path, boolean cache, boolean gzip) {
        if (path.startsWith("http")
                || path.startsWith("ws")) {
            sPath = path;
            parseURL(path);
        } else {
            sPath = PathUtils.getFullPath(path);
            sPath = PathUtils.delSeparator(path);
            isCache = cache;
            isGZip = gzip;
            isaRemote = null;
        }
        iError = 0;
    }
    public void update(String path) {
        if (path.startsWith("http")
                || path.startsWith("ws")) {
            sPath = path;
            parseURL(path);
        } else {
            sPath = PathUtils.getFullPath(path);
            sPath = PathUtils.delSeparator(path);
            isaRemote = null;
        }
        iError = 0;
    }

    public static void main(String[] args) {
        String sUrl = "http://127.0.0.1:8080";
        Host py = new Host();
        py.parseURL(sUrl);
    }
}
