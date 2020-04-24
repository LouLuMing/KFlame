package com.china.fortune.target.tcpRouter;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioAcceptDelayAttach;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpRouter extends NioAcceptDelayAttach implements TargetInterface {
    private String outServer = "127.0.0.1";
    private int outPort = 9999;

    @Override
    protected void selectAction() {
        super.selectAction();
        while (!qAddRead.isEmpty()) {
            PairSocket ps = qAddRead.poll();
            if (ps != null) {
                SelectionKey sk = registerRead(ps.to);
                sk.attach(ps.from);
            }
        }
    }

    public boolean start(int iPort, String remoteIP, int remotePort) {
        boolean rs = false;
        outServer = remoteIP;
        outPort = remotePort;
        Log.logClass("inPort:" + iPort + " outServer:" + remoteIP + " outPort:" + remotePort);
        if (openAndStart(iPort)) {
            join();
            rs = true;
        } else {
            super.stop();
        }
        return rs;
    }

	@Override
	protected boolean onRead(SocketChannel sc, Object objForClient, Object objForThread) {
		boolean rs = false;
		ByteBuffer bb = (ByteBuffer) objForThread;
		SocketChannel to = (SocketChannel) objForClient;
		try {
			bb.clear();
			if (sc.read(bb) > 0) {
				rs = true;
				bb.flip();
				do {
					to.write(bb);
				} while (bb.remaining() > 0);
			}
		} catch (Exception e) {
			rs = false;
			Log.logClass(e.getMessage());
		}
		return rs;
	}

//    @Override
//    protected boolean onRead(SocketChannel sc, Object objForClient, Object objForThread) {
//        boolean rs = false;
//        ByteBuffer bb = (ByteBuffer) objForThread;
//        SocketChannel to = (SocketChannel) objForClient;
//
//        bb.clear();
//        int len = 0;
//        try {
//            len = sc.read(bb);
//        } catch (Exception e) {
//            rs = false;
//            Log.logClass(e.getMessage() + " read " + sc);
//        }
//        if (len > 0) {
//            rs = true;
//            bb.flip();
//            do {
//                try {
//                    to.write(bb);
//                } catch (Exception e) {
//                    rs = false;
//                    Log.logClass(e.getMessage() + " write " + to);
//                }
//            } while (bb.remaining() > 0);
//        }
//
//        return rs;
//    }

    @Override
    protected void onClose(SelectionKey key, Object objForClient) {
        SocketChannel sc = (SocketChannel)key.channel();
        if (sc != null) {
            SocketChannel to = (SocketChannel) objForClient;
            if (to != null) {
                freeKeyAndSocket(to);
            }
        }
    }

    @Override
    protected Object createObjectInThread() {
        return ByteBuffer.allocate(64 * 1024);
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }

    @Override
    protected Object onAccept(SocketChannel sc, Object objForThread) {
        SocketChannel to = SocketChannelHelper.connectNoBlock(outServer, outPort);
        if (to != null) {
            addPairSocketToRead(sc, to);
            mSelector.wakeup();
        }
        return to;
    }

    private class PairSocket {
        SocketChannel from;
        SocketChannel to;
    }

    private void addPairSocketToRead(SocketChannel s1, SocketChannel s2) {
        PairSocket ps = new PairSocket();
        ps.from = s1;
        ps.to = s2;
        qAddRead.add(ps);
    }

    private ConcurrentLinkedQueue<PairSocket> qAddRead = new ConcurrentLinkedQueue<PairSocket>();

    public class RouterConfig {
        public int inPort;
        public String outServer;
        public int outPort;
    }

    @Override
    public boolean doAction(XmlNode cfg, ProcessAction self) {
        RouterConfig rf = new RouterConfig();
        ClassXml.toObject(cfg, rf);
        if (rf.inPort > 0 && rf.outServer != null && rf.outPort > 0) {
            if (start(rf.inPort, rf.outServer, rf.outPort)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        TcpRouter obj = new TcpRouter();
        if (obj.start(8700, "121.40.112.2", 8700)) {
            Log.log("TcpRouter waitToStop");
        }
    }

}
