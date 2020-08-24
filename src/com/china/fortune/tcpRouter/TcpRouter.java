package com.china.fortune.tcpRouter;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.selectorManager.NioRWSerial;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TcpRouter extends NioRWSerial implements TargetInterface {
    private String outServer = "127.0.0.1";
    private int outPort = 9999;

    public void startAndBlock(int iPort, String remoteIP, int remotePort) {
        outServer = remoteIP;
        outPort = remotePort;
        Log.logClass("inPort:" + iPort + " outServer:" + remoteIP + " outPort:" + remotePort);
        if (openAndStart(iPort)) {
            join();
        }
        stop();
    }

    @Override
    protected void onClose(SelectionKey key) {
        SocketChannel sc = (SocketChannel)key.channel();
        if (sc != null) {
            SelectionKey skTo = (SelectionKey)  key.attachment();
            if (skTo != null) {
                freeKeyAndSocket(skTo);
            }
        }
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        boolean rs = false;
        ByteBuffer bb = (ByteBuffer) objForThread;
        SelectionKey skTo = (SelectionKey)  key.attachment();
        if (skTo != null) {
            SocketChannel sc = (SocketChannel) key.channel();
            SocketChannel to = (SocketChannel) skTo.channel();
            try {
                bb.clear();
                if (sc.read(bb) > 0) {
                    bb.flip();
                    do {
                        to.write(bb);
                    } while (bb.remaining() > 0);
                    rs = true;
                }
            } catch (Exception e) {
                rs = false;
                Log.logClass(e.getMessage());
            }
        }
        if (rs) {
            return NioSocketActionType.NSA_READ;
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }

    @Override
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        if (finishConnect(key)) {
            SelectionKey other = (SelectionKey)key.attachment();
            if (other != null) {
                other.interestOps(SelectionKey.OP_READ);
                return NioSocketActionType.NSA_READ;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected Object createObjectInThread() {
        return ByteBuffer.allocate(64 * 1024);
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        SelectionKey skFrom = register(sc, 0);
        SelectionKey skTo = addConnect(outServer, outPort, skFrom);
        if (skTo != null) {
            skFrom.attach(skTo);
            return skFrom;
        } else {
            freeKeyAndSocket(skFrom);
            return null;
        }
    }

    public class RouterConfig {
        public int inPort;
        public String outServer;
        public int outPort;
    }

    @Override
    public boolean doAction(ProcessAction self, XmlNode cfg) {
        RouterConfig rf = new RouterConfig();
        ClassXml.toObject(cfg, rf);
        if (rf.inPort > 0 && rf.outServer != null && rf.outPort > 0) {
            startAndBlock(rf.inPort, rf.outServer, rf.outPort);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        TcpRouter obj = new TcpRouter();
        obj.startAndBlock(8989, "20.21.1.133", 8989);
        Log.log("TcpRouter waitToStop");
    }

}
