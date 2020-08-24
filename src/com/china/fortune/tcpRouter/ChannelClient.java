package com.china.fortune.tcpRouter;

import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.os.log.LogAction;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.socket.pointToPoint.P2PConnect;
import com.china.fortune.socket.selectorManager.NioRWParallel;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.ReuseArrayList;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChannelClient extends NioRWParallel implements TargetInterface {
    private String secretKey;
    private ReuseArrayList lsSC = new ReuseArrayList();
    private String outServer = "20.21.1.133";
    private int outPort = 8085;

    protected Object createObjectInThread() {
        return null;
    };

    protected void destroyObjectInThread(Object objForThread) {};

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
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            Log.logClass("A 3. Connect Finish Server Port " + ka.iPort);
            if (finishConnect(key)) {
                return NioSocketActionType.NSA_READ_WRITE;
            } else {
                return NioSocketActionType.NSA_CLOSE;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    private SelectionKey connectServer(int port) {
        Log.logClass("A 2. Connect Start Server Port " + port);
        KeyAttach ka = new KeyAttach();
        ka.iPort = port;
        SelectionKey skTo = addConnect(outServer, outPort, ka);
        if (skTo != null) {
            lsSC.set(port, skTo);
            return skTo;
        } else {
            return null;
        }
//        SocketChannel sc = SocketChannelHelper.connect(outServer, outPort);
//        Log.logClass("2. Connect Server Port " + iPort);
//        if (sc != null) {
//            KeyAttach ka = new KeyAttach();
//            ka.iPort = iPort;
//            SelectionKey sk = registerRead(sc, ka);
//            lsSC.set(iPort, sk);
//            return sk;
//        }
//        return null;
    }

    private void sendData(SelectionKey key, ByteBuffer bb) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            ka.lsSend.add(bb);
            addWriteEvent(key);
        }
    }

    private P2PConnect channelObj = new P2PConnect() {
        protected void onConnect(SocketChannel sc) {
            if (StringUtils.length(secretKey) > 0) {
                ByteBuffer bb = ByteBufferUtils.byteToByteBuffer(secretKey.getBytes());
                SocketChannelUtils.write(sc, bb);
            }
        }
        protected boolean onRead(int port, ByteBuffer bb) {
            SelectionKey sk = (SelectionKey)lsSC.get(port);
            if (sk == null) {
                sk = connectServer(port);
            }
            if (sk != null && bb != null) {
                Log.logClass("B 1. Recv Data by Channel Port " + port + " Len " + bb.remaining());
                sendData(sk, bb);
                return true;
            } else {
                return false;
            }
        }

        protected void onOpen(int port) {
            Log.logClass("A 1. Recv Open By Channel Port " + port);
            connectServer(port);
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
    };

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            int len = 0;
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = channelObj.getBuffer(ka.iPort);
            try {
                len = sc.read(bb);
                Log.logClass("C 0. Recv From Client Port " + ka.iPort + " Len " + len);
            } catch (Exception e) {
                Log.logClass(e.getMessage());
            }
            if (len > 0) {
                ka.setActive();
                channelObj.sendBuffer(bb);
                return NioSocketActionType.NSA_READ;
            } else {
                Log.logClass("D 1. Close By Server Port " + ka.iPort);
                channelObj.freeBuffer(bb);
                return NioSocketActionType.NSA_CLOSE;
            }
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }

    }

    @Override
    protected void onClose(SelectionKey key) {
        KeyAttach ka = (KeyAttach)key.attachment();
        if (ka != null) {
            channelObj.sendCloseEvent(ka.iPort);
            lsSC.free(ka.iPort);
            Log.logClass("D Close Port " + ka.iPort);
        }
    }

    public boolean start(String sServer, int iChannelPort) {
        if (this.openAndStart(-1)) {
            if (channelObj.connectAndStart(sServer, iChannelPort)) {
                return true;
            } else {
                this.stop();
            }
        }
        return false;
    }

    public class ChannelClientConfig {
        public String channelServer;
        public int channelPort;

        public String outServer;
        public int outPort;

        public String secretKey;
    }

    @Override
    public boolean doAction(ProcessAction self, XmlNode cfg) {
        ChannelClientConfig rf = new ChannelClientConfig();
        ClassXml.toObject(cfg, rf);
        ClassUtils.checkNoNull(rf);
        if (rf.channelServer != null && rf.channelPort > 0 && rf.outServer != null && rf.outPort > 0) {
            outServer = rf.outServer;
            outPort = rf.outPort;
            secretKey = rf.secretKey;
            if (start(rf.channelServer, rf.channelPort)) {
                join();
                stop();
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        if (OsDepend.isLinux()) {
            Log.init(LogAction.iFile);
        }
        ChannelClient cs = new ChannelClient();
        cs.outServer = "20.21.1.91";
        cs.outPort = 28080;
        cs.secretKey = "1qaz@WSX";
        //cs.start("127.0.0.1", 8990);
        cs.start("121.40.112.2", 8086);
//        ThreadHelper.sleep(1000);
//        String sRecv = HttpSendAndRecv.doGet("http://121.40.112.2:8808/index.html");
//        Log.log(sRecv);
        cs.join();
        cs.stop();
    }

}
