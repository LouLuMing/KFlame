package com.china.fortune.proxy;

import com.china.fortune.data.CacheClass;
import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.proxy.action.CommandAction;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.struct.FastList;

public class ProxyManager {
    public FastList<HostList> lsResourceMap = new FastList<>();

//    private CommandAction commandAction = new CommandAction(this);
    public HostList getMatch(String sResource) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl.isMatch(sResource)) {
                return pl;
            }
        }
        return null;
    }

    public HostList get(String sResource) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl.equals(sResource)) {
                return pl;
            }
        }
        return null;
    }

    public void cloneAndAdd(String sResource, String sPath) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl.equals(sResource)) {
                HostList plNew = pl.clone();
                plNew.add(sPath);
                lsResourceMap.set(i, plNew);
            }
        }
        saveData();
    }

    public void cloneAndDel(String sResource, String sPath) {
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl.equals(sResource)) {
                HostList plNew = pl.clone();
                plNew.del(sPath);
                lsResourceMap.set(i, plNew);
            }
        }
        saveData();
    }

    public int size() {
        return lsResourceMap.size();
    }

    public void add(String resource, String path) {
        HostList pl = get(resource);
        if (pl == null) {
            pl = new HostList(resource);
            lsResourceMap.add(pl);
        }
        pl.add(path);
    }

    public void del(String resource, String path) {
        HostList pl = get(resource);
        if (pl == null) {
            pl = new HostList(resource);
            lsResourceMap.add(pl);
        }
        pl.del(path);
    }

    private CommandAction commandAction = new CommandAction(this);
    public void doCommand(String sResource, HttpServerRequest hReq, HttpResponse hRes) {
        if (commandAction.isMatch(sResource)) {
            commandAction.doAction(sResource, hReq, hRes);
        } else {
            hRes.setResponse(404);
        }
    }

    public JSONArray toJSONArray(String sResource) {
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl != null && pl.equals(sResource)) {
                jarr.put(pl.toJSONObject());
            }
        }
        return jarr;
    }

    public JSONArray toJSONArray() {
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < lsResourceMap.size(); i++) {
            HostList pl = lsResourceMap.get(i);
            if (pl != null) {
                jarr.put(pl.toJSONObject());
            }
        }
        return jarr;
    }

    private CacheClass cacheClass = new CacheClass() {
        @Override
        protected void onSave(WriteFileAction wfa) {
            JSONArray jarr = toJSONArray();
            wfa.writeString(jarr.toString());
        }

        @Override
        protected void onLoad(ReadFileAction rfa) {
            String sJarr = rfa.readString();
            JSONArray jarr = new JSONArray(sJarr);
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject json = jarr.optJSONObject(i);
                String resource = json.optString("resource");
                if (resource != null) {
                    JSONArray proxy = json.optJSONArray("host");
                    if (proxy != null && proxy.length() > 0) {
                        for (int j = 0; j < proxy.length(); j++) {
                            String path = proxy.optString(j);
                            Log.logClass(resource + ":" + path);
                            add(resource, path);
                        }
                    } else {
                        add(resource, null);
                    }
                }
            }
        }
    };

    public void saveData() {
        cacheClass.saveData(this);
    }

    public void loadData() {
        cacheClass.loadData(this);
    }
}
