package com.china.fortune.nginx.proxy;

import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.struct.FastList;

public class ProxyList {
    public String sResource;
    public FastList<Proxy> lsHost = new FastList();
    private int iIndex = 0;
    public ProxyList(String s) {
        sResource = s;
    }

    public int getSize() {
        return lsHost.size();
    }

    public int getStart() {
        return (iIndex++) & 0xff;
    }

    public Proxy get(int iStart) {
        if (lsHost.size() > 0) {
            if (lsHost.size() == 1) {
                Proxy ph = lsHost.get(0);
                if (ph.isActive()) {
                    return ph;
                }
            } else {
                for (int i = 0; i < lsHost.size(); i++) {
                    Proxy ph = lsHost.get((iStart + i) % lsHost.size());
                    if (ph.isActive()) {
                        return ph;
                    }
                }
            }
        }
        return null;
    }

    public Proxy get() {
        if (lsHost.size() > 0) {
            if (lsHost.size() == 1) {
                Proxy ph = lsHost.get(0);
                if (ph.isActive()) {
                    return ph;
                }
            } else {
                int iStart = (iIndex++) & 0xff;
                for (int i = 0; i < lsHost.size(); i++) {
                    Proxy ph = lsHost.get((iStart + i) % lsHost.size());
                    if (ph.isActive()) {
                        return ph;
                    }
                }
            }
        }
        return null;
    }

    public Proxy get(String sPath) {
        Proxy exist = null;
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get(i);
            if (sPath.equals(ph.sPath)) {
                exist = ph;
                break;
            }
        }
        return exist;
    }

    public void add(String sPath) {
        if (sPath != null) {
            Proxy exist = get(sPath);
            if (exist == null) {
                exist = new Proxy();
                lsHost.add(exist);
            }
            if (exist != null) {
                exist.update(sPath);
            }
        }
    }

    public ProxyList clone() {
        ProxyList phl = new ProxyList(sResource);
        for (int i = 0; i < lsHost.size(); i++) {
            phl.lsHost.add(lsHost.get(i));
        }
        return phl;
    }

    public void del(String path) {
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get(i);
            if (path.equals(ph.sPath)) {
                lsHost.remove(i);
                iIndex = 0;
                break;
            }
        }
    }

    public boolean equals(String resource) {
        return resource.equals(sResource);
    }

    public boolean isMatch(String resource) {
        return resource.startsWith(sResource);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("resource", sResource);
        JSONArray jarr = new JSONArray();
        for (int i = 0; i < lsHost.size(); i++) {
            Proxy ph = lsHost.get(i);
            jarr.put(ph.sPath);
        }
        json.put("proxy", jarr);
        return json;
    }
}
