package com.china.fortune.tcpRouter;

import com.china.fortune.socket.SocketChannelUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelAndByteBuffer {
    public int index;
    public SocketChannel sc;
    public ByteBuffer bb;

    public SocketChannelAndByteBuffer(SocketChannel s, int len) {
        sc = s;
        bb = ByteBuffer.allocate(len);
        bb.clear();
        bb.limit(0);
    }

    public void appendSendBuffer(ByteBuffer tmp) {
        int pos = bb.position();
        int limit = bb.limit();
        int cap = bb.capacity();
        if (cap - limit + pos >= tmp.limit()) {
            bb.limit(cap);
            if (pos == limit) {
                bb.position(0);
            } else {
                if (pos > 0) {
                    bb.position(0);
                    bb.put(bb.array(), pos, limit - pos);
                } else {
                    bb.position(limit);
                }
            }
             bb.put(tmp);
        } else {
            ByteBuffer large = ByteBuffer.allocate(bb.capacity() * 2);
            large.put(bb.array(), pos, limit - pos);
            large.put(tmp);
            bb = large;
        }
        bb.flip();
    }

    public int sendBuffer(SocketChannel sc, ByteBuffer data) {
        int len = 0;
        if (bb != null) {
            synchronized (this) {
                if (data != null) {
                    if (bb.remaining() > 0) {
                        len = SocketChannelUtils.loopWrite(sc, bb);
                        if (len == 0) {
                            appendSendBuffer(data);
                        } else if (len > 0) {
                            len = SocketChannelUtils.loopWrite(sc, data);
                            if (len == 0) {
                                appendSendBuffer(data);
                            }
                        }
                    } else {
                        len = SocketChannelUtils.loopWrite(sc, data);
                        if (len == 0) {
                            appendSendBuffer(data);
                        }
                    }
                } else {
                    if (bb.remaining() > 0) {
                        len = SocketChannelUtils.loopWrite(sc, bb);
                    } else {
                        len = 1;
                    }
                }
            }
        }
        return len;
    }
}
