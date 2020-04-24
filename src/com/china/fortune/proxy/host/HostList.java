package com.china.fortune.proxy.host;

import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.struct.FastList;

public class HostList {
    public String sResource;
    public FastList<Host> lsHost = new FastList();
    private int iIndex = 0;
    public HostList(String s) {
        sResource = s;
    }

    public int getSize() {
        return lsHost.size();
    }

    public int getStart() {
        return (iIndex++) & 0xff;
    }

    public Host get(int iStart) {
        if (lsHost.size() > 0) {
            if (lsHost.size() == 1) {
                Host ph = lsHost.get(0);
                if (ph.isActive()) {
                    return ph;
                }
            } else {
                for (int i = 0; i < lsHost.size(); i++) {
                    Host ph = lsHost.get((iStart + i) % lsHost.size());
                    if (ph.isActive()) {
                        return ph;
                    }
                }
            }
        }
        return null;
    }

    public Host get() {
        if (lsHost.size() > 0) {
            if (lsHost.size() == 1) {
                Host ph = lsHost.get(0);
                if (ph.isActive()) {
                    return ph;
                }
            } else {
                int iStart = (iIndex++) & 0xff;
                for (int i = 0; i < lsHost.size(); i++) {
                    Host ph = lsHost.get((iStart + i) % lsHost.size());
                    if (ph.isActive()) {
                        return ph;
                    }
                }
            }
        }
        return null;
    }

    public Host get(String sPath) {
        Host exist = null;
        for (int i = 0; i < lsHost.size(); i++) {
            Host ph = lsHost.get(i);
            if (sPath.equals(ph.sPath)) {
                exist = ph;
                break;
            }
        }
        return exist;
    }

    public void add(String sPath) {
        if (sPath != null) {
            Host exist = get(sPath);
            if (exist == null) {
                exist = new Host();
                lsHost.add(exist);
            }
            if (exist != null) {
                exist.update(sPath);
            }
        }
    }

    public HostList clone() {
        HostList phl = new HostList(sResource);
        for (int i = 0; i < lsHost.size(); i++) {
            phl.lsHost.add(lsHost.get(i));
        }
        return phl;
    }

    public void del(String path) {
        for (int i = 0; i < lsHost.size(); i++) {
            Host ph = lsHost.get(i);
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
            Host ph = lsHost.get(i);
            jarr.put(ph.sPath);
        }
        json.put("host", jarr);
        return json;
    }
}
