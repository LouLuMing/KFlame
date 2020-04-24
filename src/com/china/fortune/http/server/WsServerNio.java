package com.china.fortune.http.server;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.secure.Digest;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioLoginAttach;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WsServerNio extends NioLoginAttach {
    static final private int ciMaxDataLength = 1024 * 1024;

    abstract protected boolean onDataRecv(WsServerRequest hReq, Object objForThread);
    abstract protected boolean onConnected(WsServerRequest hReq, Object objForThread);

    protected ConcurrentLinkedQueue<WsServerRequest> qObjsForClient = new ConcurrentLinkedQueue<WsServerRequest>();

    protected Object onLogin(SocketChannel sc, Object objForThread) {
        WsServerRequest hReq = qObjsForClient.poll();
        if (hReq == null) {
            hReq = new WsServerRequest();
        } else {
            hReq.clear();
        }

        if (SocketChannelHelper.read(sc, hReq.bbData) > 0) {
            if (hReq.findHttpHeadLength(0) && hReq.parseRequestAndHeader()) {
                if (hReq.checkHeaderValue("Upgrade", "websocket")) {
                    if (hReq.checkHeaderValue("Connection", "Upgrade")) {
                        if (onConnected(hReq, objForThread)) {
                            String Sec_WebSocket_Key = hReq.getHeaderValue("Sec-WebSocket-Key");
                            HttpResponse hRes = new HttpResponse(101, "Switching Protocols");

                            hRes.addHeader("Upgrade", "websocket");
                            hRes.addHeader("Connection", "Upgrade");
                            hRes.addHeader("Sec-WebSocket-Accept", ByteAction.toBase64(Digest.toSHA(Sec_WebSocket_Key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));

                            hReq.toByteBuffer(hRes);
                            SocketChannelHelper.write(sc, hReq.bbData);
                            hReq.scChannel = sc;
                            hReq.clearRecvBuffer();
                            return hReq;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onClose(SelectionKey key, Object objForClient) {
        if (objForClient != null) {
            WsServerRequest hReq = (WsServerRequest) objForClient;
            hReq.scChannel = null;
            qObjsForClient.add(hReq);
        }
    }

    @Override
    protected boolean onRead(SocketChannel sc, Object objForClient, Object objForThread) {
        boolean rs = true;
        WsServerRequest hReq = (WsServerRequest) objForClient;
        if (SocketChannelHelper.read(sc, hReq.bbData) > 0) {
            int left;
            do {
                if (hReq.iDataLength == 0) {
                    if (hReq.parseWsHead()) {
                        if (hReq.iDataLength > ciMaxDataLength) {
                            rs = false;
                        }
                    }
                }
                if (hReq.readCompleted()) {
                    hReq.remaskData();
                    if (hReq.iMsgType < 0x03) {
                        onDataRecv(hReq, objForThread);
                    } else if (hReq.iMsgType == 0x08) {
                        rs = false;
                    }
                    left = hReq.removeUsedData();
                } else {
                    left = 0;
                }
            } while (rs && left > 0);
        } else {
            rs = false;
        }
        return rs;
    }

//    @Override
//    protected boolean onRead(SocketChannel toSc, Object objForClient, Object objForThread) {
//        boolean rs = true;
//        WsServerRequest hReq = (WsServerRequest) objForClient;
//        if (hReq.bbData.position() == 0) {
//            hReq.bbData.limit(6);
//        }
//        if (SocketChannelHelper.read(toSc, hReq.bbData) > 0) {
//            if (hReq.iDataLength == 0) {
//                if (hReq.parseWsHead()) {
//                    if (hReq.iDataLength <= ciMaxDataLength) {
//                        hReq.bbData.limit(hReq.iHeadLength + hReq.iDataLength);
//                        rs = SocketChannelHelper.read(toSc, hReq.bbData) >= 0;
//                    } else {
//                        rs = false;
//                    }
//                }
//            }
//            if (rs && hReq.readCompleted()) {
//                hReq.remaskData();
//                if (hReq.iMsgType < 0x03) {
//                    onDataRecv(hReq, objForThread);
//                    hReq.clearRecvBuffer();
//                } else if (hReq.iMsgType == 0x08) {
//                    rs = false;
//                }
//            }
//        } else {
//            rs = false;
//        }
//        return rs;
//    }

    public static void main(String[] args) {
        WsServerNio ws = new WsServerNio(){
            @Override
            protected Object createObjectInThread() {
                return null;
            }
            @Override
            protected void destroyObjectInThread(Object objForThread) {
            }
            @Override
            protected boolean onConnected(WsServerRequest hReq, Object objForThread) {
                return true;
            }
            @Override
            protected boolean onDataRecv(WsServerRequest hReq, Object objForThread) {
                String sBody = hReq.getBody();
                Log.log(sBody);
                hReq.sendData(sBody);
                return true;
            }
        };
        ws.startAndBlock(7788);
    }
}
