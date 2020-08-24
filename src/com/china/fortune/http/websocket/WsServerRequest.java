package com.china.fortune.http.websocket;

import com.china.fortune.common.ByteAction;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.secure.Digest;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WsServerRequest extends HttpServerRequest {
    // 表示此帧是否是消息的最后帧，第一帧也可能是最后帧
    protected boolean isEof = false;
    // x0 表示一个后续帧；
    // x1 表示一个文本帧；
    // x2 表示一个二进制帧；
    // x3-7 为以后的非控制帧保留；
    // x8 表示一个连接关闭；
    // x9 表示一个ping；
    // xA 表示一个pong；
    // xB-F 为以后的控制帧保留
    protected int iMsgType = 0;
    protected boolean hasMask = true;

    protected ConcurrentLinkedQueue<ByteBuffer> queueWrite = new ConcurrentLinkedQueue();

    protected boolean isTcpStatus = false;
    protected boolean inTcpStatus() {
        return isTcpStatus;
    }
    protected SelectionKey selectionKey = null;
    @Override
    public void clear() {
        selectionKey = null;
        queueWrite.clear();
        isTcpStatus = false;
        super.clear();
    }

    protected boolean isHttpComplete() {
        return (findHttpHeadLength() && parseRequestAndHeader());
    }

    protected boolean isUpgrade() {
        isTcpStatus = true;
        return (checkHeaderValue("Upgrade", "websocket")
                && checkHeaderValue("Connection", "Upgrade"));
    }

//    private ByteBuffer getWrite() {
//        do {
//            ByteBuffer bb = queueWrite.peek();
//            if (bb != null) {
//                if (bb.remaining() == 0) {
//                    queueWrite.remove(bb);
//                } else {
//                    return bb;
//                }
//            } else {
//                return null;
//            }
//        } while (true);
//    }

    public void writeData(String sData) {
        queueWrite.add(toByteBuffer(sData));
    }

    protected boolean needWrite() {
        return !queueWrite.isEmpty();
    }

    public void writeData(ByteBuffer bb) {
        queueWrite.add(bb);
    }

//    protected NioSocketActionType write(SocketChannel sc) {
//        ByteBuffer bb = getWrite();
//        if (bb != null) {
//            int iLen = SocketChannelHelper.write(sc, bb);
//            if (iLen > 0) {
//                if (bb.remaining() == 0) {
//                    return write(sc);
//                } else {
//                    return NioSocketActionType.NSA_READ_WRITE;
//                }
//            } else {
//                return NioSocketActionType.NSA_CLOSE;
//            }
//        } else {
//            return NioSocketActionType.NSA_READ;
//        }
//    }
//
//    @Override
//    public NioSocketActionType write(SelectionKey key) {
//        SocketChannel sc = (SocketChannel) key.channel();
//        return write(sc);
//    }

    protected void respondUpgrade(SelectionKey key) {
        String Sec_WebSocket_Key = getHeaderValue("Sec-WebSocket-Key");
        HttpResponse hRes = new HttpResponse(101, "Switching Protocols");

        hRes.addHeader("Upgrade", "websocket");
        hRes.addHeader("Connection", "Upgrade");
        hRes.addHeader("Sec-WebSocket-Accept", ByteAction.toBase64(Digest.toSHA(Sec_WebSocket_Key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));

        bbData.clear();
        writeData(hRes.toByteBuffer());
    }

    protected void remaskData() {
        if (hasMask) {
            int iMask = iHeadLength - 4;
            for (int i = 0; i < iDataLength; i++) {
                int index = iHeadLength + i;
                byte b = (byte)(bbData.get(index) ^ bbData.get(iMask + (i & 0x03)));
                bbData.put(index, b);
            }
        }
    }

    protected boolean readCompleted() {
        if (iHeadLength > 0 && iDataLength > 0) {
            return bbData.position() >= iHeadLength + iDataLength;
        } else {
            return false;
        }
    }

    protected int removeUsedData() {
        int pos = bbData.position();
        int iLeft = pos - (iHeadLength + iDataLength);
        if (iLeft > 0) {
            bbData.position(iHeadLength + iDataLength);
            bbData.limit(pos);
            bbData.compact();
        } else {
            bbData.clear();
        }
        iDataLength = 0;
        return iLeft;
    }

    protected boolean parseWsHead() {
        if (bbData.position() > 5) {
            isEof = ((bbData.get(0) >> 7) & 0x01) > 0;
            iMsgType = bbData.get(0) & 0xF;
            hasMask = ((bbData.get(1) >> 7) & 0x01) > 0;
            iDataLength = (bbData.get(1) & 0x7F);
            iHeadLength = 2;
            if (hasMask) {
                iHeadLength += 4;
            }
            if (iDataLength == 126) {
                iDataLength = (bbData.get(2) & 0x0FF);
                iDataLength <<= 8;
                iDataLength += (bbData.get(3) & 0x0FF);
                iHeadLength += 2;
            } else if (iDataLength == 127) {
                iDataLength = (bbData.get(2) & 0x0FF);
                for (int i = 3; i < 3 + 7; i++) {
                    iDataLength <<= 8;
                    iDataLength += (bbData.get(i) & 0x0FF);
                }
                iHeadLength += 8;
            }
            return true;
        }
        return false;
    }

    protected ByteBuffer toByteBuffer(byte[] bData) {
        ByteBuffer bbSend = ByteBuffer.allocate(6 + bData.length);

        bbSend.clear();
        bbSend.put((byte) 0x81);
        if (bData.length < 126) {
            bbSend.put((byte) (bData.length));
        } else if (bData.length < 65535) {
            bbSend.put((byte) (126));
            bbSend.put((byte) (bData.length >> 8));
            bbSend.put((byte) bData.length);
        } else {
            bbSend.put((byte) (127));
            bbSend.put((byte) (bData.length >> 24));
            bbSend.put((byte) (bData.length >> 16));
            bbSend.put((byte) (bData.length >> 8));
            bbSend.put((byte) bData.length);
        }
        bbSend.put(bData);
        bbSend.flip();
        return bbSend;
    }

    protected ByteBuffer toByteBuffer(String sData) {
        byte[] bData = sData.getBytes();
        return toByteBuffer(bData);
    }
}
