package com.china.fortune.proxy;

import com.china.fortune.global.Log;
import com.china.fortune.socket.selectorManager.NioRWAttach;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.struct.FastList;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RouterNio extends NioRWAttach {
    @Override
    protected void selectAction(FastList<SelectionKey> qSelectedKey) {
        super.selectAction(qSelectedKey);
        while (!qAddRead.isEmpty()) {
            PairSocket ps = qAddRead.poll();
            if (ps != null) {
                SelectionKey skTo = addRead(ps.to);
                skTo.attach(ps.from);
                SelectionKey skFrom = addRead(ps.from);
                skFrom.attach(ps.to);
            }
        }
    }

    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        ByteBuffer bb = (ByteBuffer) objForThread;
        SocketChannel sc = (SocketChannel)key.channel();
        SocketChannel to = (SocketChannel) key.attachment();
        boolean rs = false;
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
        if (rs) {
            return NioSocketActionType.OP_READ;
        } else {
            return NioSocketActionType.OP_CLOSE;
        }
    }

    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        return NioSocketActionType.OP_READ;
    }

    @Override
    protected void onClose(SelectionKey key) {
        SocketChannel sc = (SocketChannel)key.channel();
        if (sc != null) {
            SocketChannel to = (SocketChannel) key.attachment();
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

    private class PairSocket {
        SocketChannel from;
        SocketChannel to;
    }

    public void addPairSocketToRead(SocketChannel s1, SocketChannel s2) {
        PairSocket ps = new PairSocket();
        ps.from = s1;
        ps.to = s2;
        qAddRead.add(ps);
    }

    private ConcurrentLinkedQueue<PairSocket> qAddRead = new ConcurrentLinkedQueue<PairSocket>();
}
