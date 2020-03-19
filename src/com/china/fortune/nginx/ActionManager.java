package com.china.fortune.nginx;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.nginx.action.*;
import com.china.fortune.struct.FastList;

public class ActionManager {
    public FastList<NginxInterface> lsResourceMap = new FastList<>();

    private CommandAction commandAction = new CommandAction(this);
    public NginxInterface get(String sResource) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            NginxInterface nginxInterface = lsResourceMap.get(i);
            if (nginxInterface.isMatch(sResource)) {
                return nginxInterface;
            }
        }
        return null;
    }

    public int size() {
        return lsResourceMap.size();
    }

    public NginxInterface get(int i) {
        return lsResourceMap.get(i);
    }

    public void add(String resource, String path) {
        NginxInterface nginxInterface = get(resource);
        if (nginxInterface == null) {
            if (path.startsWith("http")
                || path.startsWith("ws")) {
                lsResourceMap.add(new ProxyAction(resource, path));
            } else {
                lsResourceMap.add(new FileAction(resource, path));
            }
        } else {
            if (nginxInterface instanceof ProxyAction) {
                ((ProxyAction) nginxInterface).addPath(path);
            } else if (nginxInterface instanceof FileAction) {
                ((FileAction) nginxInterface).updatePath(path);
            }
        }
    }

    public void doCommand(String sResource, HttpServerRequest hReq, HttpResponse hRes) {
        if (commandAction.isMatch(sResource)) {
            commandAction.doAction(sResource, hReq, hRes);
        } else {
            hRes.setResponse(404);
            Log.logError(sResource + " is miss");
        }
    }

    public NginxActionType doAction(HttpServerRequest hReq) {
        HttpResponse hRes = new HttpResponse();
        String sResource = hReq.getResource();
        NginxActionType hat = NginxActionType.NA_WRITE;
        NginxInterface nginxInterface = get(sResource);
        if (nginxInterface != null) {
            hat = nginxInterface.doAction(sResource, hReq, hRes);
            if (hat == NginxActionType.NA_ERROR) {
                doCommand(sResource, hReq, hRes);
            }
        } else {
            doCommand(sResource, hReq, hRes);
        }
        if (hat != NginxActionType.NA_PROXY) {
            hReq.toByteBuffer(hRes);
        }
        return hat;
    }
}
