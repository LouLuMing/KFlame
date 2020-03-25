package com.china.fortune.nginx;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.nginx.action.CommandAction;
import com.china.fortune.nginx.proxy.ProxyList;
import com.china.fortune.struct.FastList;

public class ProxyManager {
    public FastList<ProxyList> lsResourceMap = new FastList<>();

    public ProxyList get(String sResource) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            ProxyList pl = lsResourceMap.get(i);
            if (pl.isMatch(sResource)) {
                return pl;
            }
        }
        return null;
    }

    public int size() {
        return lsResourceMap.size();
    }

    public ProxyList get(int i) {
        return lsResourceMap.get(i);
    }

    public void add(String resource, String path) {
        ProxyList pl = get(resource);
        if (pl == null) {
            pl = new ProxyList(resource);
            lsResourceMap.add(pl);
        }
        pl.add(path);
    }

    private CommandAction commandAction = new CommandAction(this);
    public void doCommand(String sResource, HttpServerRequest hReq, HttpResponse hRes) {
        if (commandAction.isMatch(sResource)) {
            commandAction.doAction(sResource, hReq, hRes);
        } else {
            hRes.setResponse(404);
        }
    }
}
