package com.china.fortune.target.tcpRouter;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.os.log.LogAction;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.pointToPoint.P2PAccept;
import com.china.fortune.socket.selectorManager.NioRWAttach;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.struct.ReuseArrayList;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ChannelServer extends NioRWAttach implements TargetInterface {
    private ReuseArrayList lsSC = new ReuseArrayList();
    private P2PAccept channelObj = new P2PAccept() {
        protected boolean onChannelAccept(SocketChannel sc) {
            return true;
        }

        protected boolean onRead(int index, ByteBuffer bb) {
            int len = -1;
            SocketChannelAndByteBuffer sb = (SocketChannelAndByteBuffer)lsSC.get(index);
            if (sb != null && bb != null) {
                len = sb.sendBuffer(sb.sc, bb);
                Log.logClass("5. Send Port " + index + " Len " + len);
            }
            return len != 0;
        }

        protected void onClose(int port) {
            SocketChannelAndByteBuffer sc = (SocketChannelAndByteBuffer)lsSC.free(port);
            if (sc != null) {
                freeKeyAndSocket(sc.sc);
            }
        }
    };

    @Override
    protected SelectionKey acceptSocket(SocketChannel sc) {
        SelectionKey sk = super.acceptSocket(sc);
        if (sk != null) {
            SocketChannelAndByteBuffer sb = new SocketChannelAndByteBuffer(sc, 16 * 1024 * 1024);
            int port = lsSC.set(sb);
            sk.attach(port);
            channelObj.sendOpenEvent(port);
            Log.logClass("0. Accept Port " + port);
        }
        return sk;
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        int len = 0;
        SocketChannel sc = (SocketChannel) key.channel();
        int port = (Integer) key.attachment();
        ByteBuffer bb = channelObj.getBuffer(port);
        try {
            len = sc.read(bb);
            Log.logClass("1. Read Port " + port + " Len " + len);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        if (len > 0) {
            channelObj.sendBuffer(bb);
            return NioSocketActionType.OP_READ;
        } else {
            channelObj.freeBuffer(bb);
            return NioSocketActionType.OP_CLOSE;
        }
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        return NioSocketActionType.OP_READ;
    }

    @Override
    protected void onClose(SelectionKey key) {
        Object objForClient = key.attachment();
        Integer port = (Integer)objForClient;
        channelObj.sendCloseEvent(port);
        lsSC.free(port);
        Log.logClass("6. Close Port " + port);
    }

    @Override
    protected Object createObjectInThread() {
        return null;
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }

    public boolean start(int iInPort, int iChannelPort) {
        if (this.openAndStart(iInPort)) {
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
    }

    @Override
    public boolean doAction(XmlNode cfg, ProcessAction self) {
        Log.init(LogAction.iNull);
        ChannelServerConfig rf = new ChannelServerConfig();
        ClassXml.toObject(cfg, rf);
        if (rf.inPort > 0 && rf.channelPort > 0) {
            if (start(rf.inPort, rf.channelPort)) {
                join();
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ChannelServer cs = new ChannelServer();
        cs.start(8900, 8901);
        cs.join();
    }

}
