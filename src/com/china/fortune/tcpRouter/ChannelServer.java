package com.china.fortune.tcpRouter;

import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.socket.pointToPoint.P2PAccept;
import com.china.fortune.socket.selectorManager.NioRWParallel;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.ReuseArrayList;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChannelServer extends NioRWParallel implements TargetInterface {
    private String secretKey;
    private ReuseArrayList lsSC = new ReuseArrayList();
    private P2PAccept channelObj = new P2PAccept() {
        protected boolean onChannelAccept(SocketChannel sc) {
            Log.logClass("start");
            if (StringUtils.length(secretKey) > 0) {
                ByteBuffer bbRecv = ByteBuffer.allocate(128);
                int iRecv = SocketChannelUtils.read(sc, bbRecv);
                if (iRecv > 0) {
                    String sKey = ByteBufferUtils.toString(bbRecv);
                    if (StringUtils.compareTo(secretKey, sKey) == 0) {
                        return true;
                    } else {
                        Log.logClass(sKey + " is error");
                        return false;
                    }
                } else {
                    Log.logClass("secretKey is miss");
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean onRead(int port, ByteBuffer bb) {
            SelectionKey sb = (SelectionKey)lsSC.get(port);
            if (sb != null && bb != null) {
                sendData(sb, bb);
                Log.logClass("C 1. Recv By Channel Port " + port + " Len " + bb.remaining());
                return true;
            } else {
                return false;
            }
        }

        protected void onClose(int port) {
            SelectionKey key = (SelectionKey)lsSC.free(port);
            if (key != null) {
                KeyAttach ka = (KeyAttach)key.attachment();
                if (ka != null) {
                    channelObj.freeBuffer(ka.lsSend);
                }
                freeKeyAndSocket(key);
            }
            Log.logClass("D 0. Close By Channel Port " + port);
        }

        @Override
        protected void onOpen(int port) {
        }
    };

    private void sendData(SelectionKey sk, ByteBuffer bb) {
        KeyAttach ka = (KeyAttach)sk.attachment();
        if (ka != null) {
            ka.lsSend.add(bb);
            addWriteEvent(sk);
        }
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        KeyAttach ka = new KeyAttach();
        SelectionKey sk = registerRead(sc, ka);
        int port = lsSC.set(sk);
        if (port > 0) {
            ka.iPort = port;
            Log.logClass("A 0. Accept Client Port " + port);
            channelObj.sendOpenEvent(port);
            return sk;
        } else {
            return null;
        }
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            int len = 0;
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = channelObj.getBuffer(ka.iPort);
            try {
                len = sc.read(bb);
                Log.logClass("B 0. Read Client Port " + ka.iPort + " Len " + len);
            } catch (Exception e) {
                Log.logClass(e.getMessage());
            }
            if (len > 0) {
                ka.setActive();
                channelObj.sendBuffer(bb);
                return NioSocketActionType.NSA_READ;
            } else {
                Log.logClass("D 1. Close By Client Port " + ka.iPort);
                channelObj.freeBuffer(bb);
                return NioSocketActionType.NSA_CLOSE;
            }
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    @Override
    protected ByteBuffer getWrite(ConcurrentLinkedQueue<ByteBuffer> lsSend) {
        do {
            ByteBuffer bb = lsSend.peek();
            if (bb != null) {
                if (bb.remaining() == 0) {
                    lsSend.remove(bb);
                    channelObj.freeBuffer(bb);
                } else {
                    return bb;
                }
            } else {
                return null;
            }
        } while (true);
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        SocketChannel sc = (SocketChannel) key.channel();
        KeyAttach ka = (KeyAttach)key.attachment();
        return write(sc, ka.lsSend);
    }

    @Override
    protected void onClose(SelectionKey key) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            channelObj.freeBuffer(ka.lsSend);
            ka.lsSend.clear();
            channelObj.sendCloseEvent(ka.iPort);
            lsSC.free(ka.iPort);
            Log.logClass("D Close Port " + ka.iPort);
        }
    }

    @Override
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }

    @Override
    protected Object createObjectInThread() {
        return null;
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }

    public boolean start(int iListenPort, int iChannelPort, String sKey) {
        secretKey = sKey;
        if (this.openAndStart(iListenPort)) {
            if (channelObj.listenAndStart(iChannelPort)) {
                return true;
            } else {
                this.stop();
            }
        }
        return false;
    }

    public class ChannelServerConfig {
        public int inPort;
        public int channelPort;
        public String secretKey;
    }

    @Override
    public boolean doAction(ProcessAction self, XmlNode cfg) {
        ChannelServerConfig rf = new ChannelServerConfig();
        ClassXml.toObject(cfg, rf);
        if (rf.inPort > 0 && rf.channelPort > 0) {
            if (start(rf.inPort, rf.channelPort, rf.secretKey)) {
                join();
                stop();
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ChannelServer cs = new ChannelServer();
        cs.start(8989, 8990, null);
        cs.join();
        cs.stop();
    }

}
