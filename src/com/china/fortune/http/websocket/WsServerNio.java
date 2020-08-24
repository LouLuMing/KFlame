package com.china.fortune.http.websocket;

import com.china.fortune.global.Log;
import com.china.fortune.socket.selectorManager.NioRWParallel;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.thread.ThreadUtils;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WsServerNio extends NioRWParallel {
    static final private int ciMaxDataLength = 1024 * 1024;

    abstract protected boolean onDataRecv(WsServerRequest hReq, Object objForThread);
    abstract protected boolean onConnected(WsServerRequest hReq, Object objForThread);

    protected ConcurrentLinkedQueue<WsServerRequest> qObjsForClient = new ConcurrentLinkedQueue<WsServerRequest>();

    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }

    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        WsServerRequest hReq = (WsServerRequest)key.attachment();
        if (hReq != null) {
//            return hs.write(key);
            SocketChannel sc = (SocketChannel) key.channel();
            return write(sc, hReq.queueWrite);
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        WsServerRequest hReq = qObjsForClient.poll();
        if (hReq == null) {
            hReq = new WsServerRequest();
        }
        SelectionKey sk = registerRead(sc, hReq);
        hReq.selectionKey = sk;
        return sk;
    }

    @Override
    protected void onClose(SelectionKey key) {
        WsServerRequest hReq = (WsServerRequest)key.attachment();
        if (hReq != null) {
            hReq.clear();
            qObjsForClient.add(hReq);
        }
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        WsServerRequest hReq = (WsServerRequest)key.attachment();
        if (hReq != null) {
            if (hReq.read(key) > 0) {
                if (hReq.inTcpStatus()) {
                    int left;
                    boolean bOK = true;
                    do {
                        if (hReq.iDataLength <= 0) {
                            if (hReq.parseWsHead()) {
                                if (hReq.iDataLength > ciMaxDataLength) {
                                    bOK = false;
                                }
                            }
                        }
                        if (hReq.readCompleted()) {
                            hReq.remaskData();
                            if (hReq.iMsgType < 0x03) {
                                bOK = onDataRecv(hReq, objForThread);
                            } else if (hReq.iMsgType == 0x08) {
                                bOK = false;
                            }
                            left = hReq.removeUsedData();
                        } else {
                            left = 0;
                        }
                    } while (bOK && left > 0);
                    if (bOK) {
                        return NioSocketActionType.NSA_READ;
                    } else {
                        return NioSocketActionType.NSA_CLOSE;
                    }
                } else {
                    if (hReq.findHttpHeadLength()) {
                        if (hReq.parseRequestAndHeader()
                                && hReq.isUpgrade()
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

    protected boolean writeData(WsServerRequest hReq, String sData) {
        if (hReq != null) {
            SelectionKey key = hReq.selectionKey;
            if (key != null && key.isValid()) {
                if (hReq.inTcpStatus()) {
                    hReq.writeData(sData);
                    addWriteEvent(key);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean writeData(SelectionKey key, String sData) {
        if (key != null && key.isValid()) {
            WsServerRequest hReq = (WsServerRequest)key.attachment();
            if (hReq != null) {
                if (hReq.inTcpStatus()) {
                    hReq.writeData(sData);
                    addWriteEvent(key);
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
                    writeData(hReq, sBody);
                }
                return true;
            }
        };
        ws.openAndStart(7788);
        while (true) {
//            ws.broadcastData("Loop, hello world");
            ThreadUtils.sleep(1000);
        }
    }
}
