package com.china.fortune.nginx.action;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.file.PathUtils;

import java.io.File;

public class FileAction implements NginxInterface {
    public String sResource;
    public String sPath;
    private String sDefFile = File.separatorChar + "index.html";
    @Override
    public boolean isMatch(String sTag) {
        return sTag.startsWith(sResource);
    }

    @Override
    public NginxActionType doAction(String sTag, HttpServerRequest hReq, HttpResponse hRes) {
        String sSubResource = hReq.getResourceWithoutParam();
        if (sSubResource.length() > 1) {
            if (File.separatorChar != '/') {
                sSubResource = sSubResource.replace('/', File.separatorChar);
            }
        } else {
            sSubResource = sDefFile;
        }
        String sLocalFile = sPath + sSubResource;
        if (hRes.putFile(sLocalFile)) {
            Log.log(sTag + " " + sLocalFile);
            return NginxActionType.NA_WRITE;
        } else {
            Log.logError(sTag + " " + sLocalFile);
            return NginxActionType.NA_ERROR;
        }
    }

    public FileAction(String resource, String path) {
        sResource = resource;
        path = PathUtils.getFullPath(path);
        sPath = PathUtils.delSeparator(path);
    }

    public void updatePath(String path) {
        path = PathUtils.getFullPath(path);
        sPath = PathUtils.delSeparator(path);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("resource", sResource);
        json.put("path", sPath);
        return json;
    }

}
