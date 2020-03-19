package com.china.fortune.restfulHttpServer;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.socket.LineSocketAction;

public abstract class ClusterBaseServer extends BaseWebTarget {
    public class Twins {
        public String sServer;
        public int iPort;
    }

    protected long lSetTicket = 0;

    public ClusterBaseServer() {
        lSetTicket = System.currentTimeMillis();
    }

    // 0 to be master
    // 1 slave
    // 2 master
    // 3
    private AtomicInteger aiStatus = new AtomicInteger(0);
    private Twins twins = new Twins();

    // 1. to be master
    // 2. deliver data
    // 3. sync data
    protected boolean deliverData(HttpServerRequest hReq, HttpResponse hRes) {
        boolean rs = false;
        HttpClient hc = new HttpClient();
        LineSocketAction lsa = hc.createLSA(twins.sServer, twins.iPort);
        if (lsa != null) {
            HttpResponse hResRecv = hc.sendDataAndRecvHead(lsa, hReq);
            if (hResRecv != null) {
                hRes.copy(hResRecv);
                rs = true;
            }
            lsa.close();
        }
        return rs;
    }

    protected boolean tryDeliverData(HttpServerRequest hReq, HttpResponse hRes) {
        for (int i = 0; i < 2; i++) {
            if (deliverData(hReq, hRes)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
        boolean rs = true;
        if (aiStatus.get() == 2) {
            rs = super.service(hReq, hRes, objForThread);
        } else if (aiStatus.get() == 1) {
            if (!tryDeliverData(hReq, hRes)) {
                aiStatus.set(2);
            }
        }
        return rs;
    }
}
