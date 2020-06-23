package com.china.fortune.http.server;

import com.china.fortune.global.Log;
import com.china.fortune.socket.selectorManager.NioMod;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.thread.ThreadUtils;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WsServerNio extends NioMod {
    static final private int ciMaxDataLength = 1024 * 1024;

    abstract protected boolean onDataRecv(WsServerRequest hReq, Object objForThread);
    abstract protected boolean onConnected(WsServerRequest hReq, Object objForThread);

    protected ConcurrentLinkedQueue<WsServerRequest> qObjsForClient = new ConcurrentLinkedQueue<WsServerRequest>();

    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }

    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            WsServerRequest hs = (WsServerRequest) objForClient;
            return hs.write(key);
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        WsServerRequest hReq = qObjsForClient.poll();
        if (hReq == null) {
            hReq = new WsServerRequest();
        } else {
            hReq.clear();
        }
        return registerRead(sc, hReq);
    }

    @Override
    protected void onClose(SelectionKey key) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            WsServerRequest hReq = (WsServerRequest) objForClient;
            qObjsForClient.add(hReq);
        }
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            WsServerRequest hReq = (WsServerRequest)objForClient;
            if (hReq.read(key) > 0) {
                if (hReq.inTcpStatus()) {
                    int left;
                    boolean bRead = true;
                    do {
                        if (hReq.iDataLength == 0) {
                            if (hReq.parseWsHead()) {
                                if (hReq.iDataLength > ciMaxDataLength) {
                                    bRead = false;
                                }
                            }
                        }
                        if (hReq.readCompleted()) {
                            hReq.remaskData();
                            if (hReq.iMsgType < 0x03) {
                                bRead = onDataRecv(hReq, objForThread);
                            } else if (hReq.iMsgType == 0x08) {
                                bRead = false;
                            }
                            left = hReq.removeUsedData();
                        } else {
                            left = 0;
                        }
                    } while (bRead && left > 0);
                    if (bRead) {
                        if (hReq.needWrite()) {
                            return NioSocketActionType.NSA_READ_WRITE;
                        } else {
                            return NioSocketActionType.NSA_READ;
                        }
                    } else {
                        return NioSocketActionType.NSA_CLOSE;
                    }
                } else {
                    if (hReq.isHttpComplete()) {
                        if (hReq.isUpgrade()
                                && onConnected(hReq, objForThread)) {
                            hReq.respondUpgrade(key);
                            return NioSocketActionType.NSA_WRITE;
                        }
                    } else {
                        return NioSocketActionType.NSA_READ;
                    }
                }
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    protected boolean writeData(SelectionKey key, String sData) {
        if (key != null && key.isValid()) {
            Object objForClient = key.attachment();
            if (objForClient != null) {
                WsServerRequest hReq = (WsServerRequest) objForClient;
                if (hReq.inTcpStatus()) {
                    hReq.writeData(sData);
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    return true;
                }
            }
        }
        return false;
    }

    public void broadcastData(String sData) {
        Iterator<SelectionKey> it = mSelector.keys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            if (key.isValid()) {
                writeData(key, sData);
            }
        }
    }

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
                if (sBody != null) {
                    Log.log(sBody);
                    hReq.writeData(sBody);
                }
                return true;
            }
        };
        ws.openAndStart(7788);
        while (true) {
            ws.broadcastData("Loop, hello world");
            ThreadUtils.sleep(1000);
        }
    }
}
