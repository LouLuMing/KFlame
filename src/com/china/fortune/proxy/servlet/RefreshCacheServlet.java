package com.china.fortune.proxy.servlet;

import com.china.fortune.global.CommonResource;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.proxy.ProxyServer;
import com.china.fortune.restfulHttpServer.ResultJson;

import java.io.File;
import java.util.ArrayList;

public class RefreshCacheServlet extends RestfulStringServlet {
    private String[] lsKey = { "path" };
    public RefreshCacheServlet() {
        ksKey.append(lsKey);
        setUrlDecode(true);
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        String sFolder = lsValues[0];
        File dirFile = new File(sFolder);
        if (dirFile.exists()) {
            ProxyServer proxyServer = CommonResource.get(ProxyServer.class);
            if (dirFile.isFile()) {
                proxyServer.refreshCache(sFolder);
            } else if (dirFile.isDirectory()) {
                ArrayList<String> lsFiles = new ArrayList<>();
                PathUtils.getAllFile(sFolder, lsFiles);
                for (String sFile : lsFiles) {
                    proxyServer.refreshCache(sFile);
                }
            }
            ResultJson.fillOK(json);
        } else {
            ResultJson.fillError(json, "miss " + lsValues[0]);
        }

        return RunStatus.isOK;
    }

    @Override
    protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillData(json, 1, sKey + " is miss", null);
    }
}
