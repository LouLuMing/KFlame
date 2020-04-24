package com.china.fortune.http.server;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioRWAttach;
import com.china.fortune.socket.selectorManager.NioSocketActionType;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class HttpServerNioAttach extends NioRWAttach {
    protected abstract boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);

    protected ConcurrentLinkedQueue<HttpServerRequest> qObjsForClient = new ConcurrentLinkedQueue<HttpServerRequest>();
    protected int iMaxHttpHeadLength = 2 * 1024;
    protected int iMaxHttpBodyLength = 2 * 1024 * 1024;

    @Override
    protected SelectionKey acceptSocket(SocketChannel sc) {
        SelectionKey sk = super.acceptSocket(sc);
        if (sk != null) {
            Object obj = onAccept(sc);
            sk.attach(obj);
        }
        return sk;
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpServerRequest hRequest = (HttpServerRequest)objForClient;
            SocketChannel sc = (SocketChannel) key.channel();
            NioSocketActionType op = hRequest.readHttpHead(sc, iMaxHttpHeadLength, iMaxHttpBodyLength);
            if (op == NioSocketActionType.OP_READ_COMPLETED) {
                if (hRequest.parseRequestAndHeader()) {
                    HttpResponse hResponse = new HttpResponse();
                    if (service(hRequest, hResponse, objForThread)) {
                        hRequest.toByteBuffer(hResponse);
                        if (SocketChannelHelper.write(sc, hRequest.bbData) >= 0) {
                            if (hRequest.bbData.remaining() == 0) {
                                hRequest.reset();
                                return NioSocketActionType.OP_READ;
                            } else {
                                return NioSocketActionType.OP_WRITE;
                            }
                        }
                    }
                }
            } else {
                return op;
            }
        }
        return NioSocketActionType.OP_CLOSE;
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpServerRequest hs = (HttpServerRequest) objForClient;
            SocketChannel sc = (SocketChannel) key.channel();
            if (SocketChannelHelper.write(sc, hs.bbData) > 0) {
                if (hs.bbData.remaining() == 0) {
                    hs.bbData.clear();
                    return NioSocketActionType.OP_READ;
                } else {
                    return NioSocketActionType.OP_WRITE;
                }
            }
        }
        return NioSocketActionType.OP_CLOSE;
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
    protected boolean isInvalidSocket(long lLimit, Object objForClient) {
        if (objForClient != null) {
            HttpServerRequest hhb = (HttpServerRequest) objForClient;
            return hhb.lActiveTicket < lLimit;
        } else {
            return true;
        }
    }

    protected Object onAccept(SocketChannel sc) {
        HttpServerRequest hhb = qObjsForClient.poll();
        if (hhb == null) {
            hhb = new HttpServerRequest();
        } else {
            hhb.clear();
        }

        InetSocketAddress isa = (InetSocketAddress) sc.socket().getRemoteSocketAddress();
        if (isa != null) {
            hhb.bRemoteAddr = isa.getAddress().getAddress();
//            isa.getPort();
        }
        return hhb;
    }

    @Override
    protected void onClose(SelectionKey key, Object objForClient) {
        if (objForClient != null) {
            qObjsForClient.add((HttpServerRequest) objForClient);
        }
    }

}
