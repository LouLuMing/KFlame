package com.china.fortune.proxy;

import com.china.fortune.global.Log;
import com.china.fortune.socket.selectorManager.NioRWSerial;
import com.china.fortune.socket.selectorManager.NioSocketActionType;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class RouterNio extends NioRWSerial {
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
            return NioSocketActionType.NSA_READ;
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
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
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        return NioSocketActionType.NSA_READ;
    }

    @Override
    protected Object createObjectInThread() {
        return ByteBuffer.allocate(64 * 1024);
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }
}
