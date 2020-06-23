package com.china.fortune.http.server;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.socket.selectorManager.NioMod;
import com.china.fortune.socket.selectorManager.NioSocketActionType;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class HttpServerNioAttach extends NioMod {
    protected abstract boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);

    protected ConcurrentLinkedQueue<HttpServerRequest> qObjsForClient = new ConcurrentLinkedQueue<HttpServerRequest>();
    protected int iMaxHttpHeadLength = 2 * 1024;
    protected int iMaxHttpBodyLength = 2 * 1024 * 1024;

    protected boolean allowAccept(SocketChannel sc) {
        return true;
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        if (allowAccept(sc)) {
            HttpServerRequest hhb = qObjsForClient.poll();
            if (hhb != null) {
                hhb.clear();
            } else {
                hhb = new HttpServerRequest();
            }
            return registerRead(sc, hhb);
        } else {
            return null;
        }
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpServerRequest hReq = (HttpServerRequest)objForClient;
            SocketChannel sc = (SocketChannel) key.channel();
            NioSocketActionType op = hReq.readHttpHead(sc, iMaxHttpHeadLength, iMaxHttpBodyLength);
            if (op == NioSocketActionType.NSA_READ_COMPLETED) {
                if (hReq.parseRequestAndHeader()) {
                    hReq.fetchAddress(sc);
                    HttpResponse hRes = new HttpResponse();
                    if (service(hReq, hRes, objForThread)) {
                        hReq.setByteBuffer(hRes);
                        return hReq.writeOrNo(key);
                    }
                }
            } else {
                return op;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpServerRequest hs = (HttpServerRequest) objForClient;
            return hs.write(key);
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    public void setMaxHttpHeadLength(int iMax) {
        iMaxHttpHeadLength = iMax;
        Log.logClass(String.valueOf(iMaxHttpHeadLength));
    }

    public void setMaxHttpBodyLength(int iMax) {
        iMaxHttpBodyLength = iMax;
        Log.logClass(String.valueOf(iMaxHttpBodyLength));
    }

    @Override
    protected boolean isInvalidSocket(long lLimit, SelectionKey key) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpServerRequest hhb = (HttpServerRequest) objForClient;
            return hhb.lActiveTicket < lLimit;
        } else {
            return true;
        }
    }

    @Override
    protected void onClose(SelectionKey key) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            qObjsForClient.add((HttpServerRequest) objForClient);
        }
    }

    @Override
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }
}
