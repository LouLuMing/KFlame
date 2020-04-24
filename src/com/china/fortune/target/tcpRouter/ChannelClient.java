package com.china.fortune.target.tcpRouter;

import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.os.log.LogAction;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.pointToPoint.P2PConnect;
import com.china.fortune.socket.selectorManager.NioRWAttach;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.struct.ReuseArrayList;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChannelClient extends NioRWAttach implements TargetInterface {

    private ReuseArrayList lsSC = new ReuseArrayList();
    private String outServer = "20.21.1.133";
    private int outPort = 8085;

    private ConcurrentLinkedQueue<SocketChannelAndByteBuffer> qAddRead = new ConcurrentLinkedQueue<SocketChannelAndByteBuffer>();

    protected Object createObjectInThread() {
        return null;
    };

    protected void destroyObjectInThread(Object objForThread) {};

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        return NioSocketActionType.OP_READ;
    }

    @Override
    protected void selectAction() {
        super.selectAction();
        while (!qAddRead.isEmpty()) {
            SocketChannelAndByteBuffer scp = qAddRead.poll();
            if (scp != null) {
                SelectionKey sk = registerRead(scp.sc);
                if (sk != null) {
                    sk.attach(scp.index);
                }
            }
        }
    }

    private SocketChannelAndByteBuffer connectServer(int port) {
        SocketChannel sc = SocketChannelHelper.connectNoBlock(outServer, outPort);
        Log.logClass("2. Connect Port " + port);
        if (sc != null) {
            SocketChannelAndByteBuffer sb = new SocketChannelAndByteBuffer(sc, 1024 * 1024);
            sb.index = port;
            lsSC.set(port, sb);
            qAddRead.add(sb);
            mSelector.wakeup();
            return sb;
        }
        return null;
    }

    private P2PConnect channelObj = new P2PConnect() {
        protected boolean onRead(int port, ByteBuffer bb) {
            int len = -1;
            SocketChannelAndByteBuffer sb = (SocketChannelAndByteBuffer)lsSC.get(port);
            if (sb == null) {
                sb = connectServer(port);
            }
            if (sb != null && bb != null) {
                len = sb.sendBuffer(sb.sc, bb);
                Log.logClass("3. Send Port " + port + " Len " + len);
            }
            return len != 0;
        }

        protected void onClose(int port) {
            SocketChannelAndByteBuffer sb = (SocketChannelAndByteBuffer)lsSC.free(port);
            if (sb != null) {
                freeKeyAndSocket(sb.sc);
            }
        }
    };

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        int len = 0;
        SocketChannel sc = (SocketChannel) key.channel();
        int port = (Integer) key.attachment();
        ByteBuffer bb = channelObj.getBuffer(port);
        try {
            len = sc.read(bb);
            Log.logClass("4. Recv Port " + port + " Len " + len);
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
    protected void onClose(SelectionKey key, Object objForClient) {
        int port = (Integer) objForClient;
        channelObj.sendCloseEvent(port);
        lsSC.free(port);
        Log.logClass("6. Close Port " + port);
    }

    public boolean start(String sServer, int iPort) {
        if (this.openAndStart(-1)) {
            if (channelObj.connectAndStart(sServer, iPort)) {
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
    }

    @Override
    public boolean doAction(XmlNode cfg, ProcessAction self) {
        Log.init(LogAction.iNull);
        ChannelClientConfig rf = new ChannelClientConfig();
        ClassXml.toObject(cfg, rf);
        if (rf.channelServer != null && rf.channelPort > 0 && rf.outServer != null && rf.outPort > 0) {
            outServer = rf.outServer;
            outPort = rf.outPort;
            if (start(rf.channelServer, rf.channelPort)) {
                join();
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
        cs.outServer = "20.21.1.140";
        cs.outPort = 3306;
        cs.start("127.0.0.1", 8901);
//        ThreadHelper.sleep(1000);
//        String sRecv = HttpSendAndRecv.doGet("http://121.40.112.2:8808/index.html");
//        Log.log(sRecv);
        cs.join();
    }

}
