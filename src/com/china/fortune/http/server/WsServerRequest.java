package com.china.fortune.http.server;

import com.china.fortune.socket.SocketChannelHelper;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WsServerRequest extends HttpServerRequest {
    // 表示此帧是否是消息的最后帧，第一帧也可能是最后帧
    public boolean isEof = false;
    // x0 表示一个后续帧；
    // x1 表示一个文本帧；
    // x2 表示一个二进制帧；
    // x3-7 为以后的非控制帧保留；
    // x8 表示一个连接关闭；
    // x9 表示一个ping；
    // xA 表示一个pong；
    // xB-F 为以后的控制帧保留
    public int iMsgType = 0;
    public boolean hasMask = true;

    public SocketChannel scChannel;

    public void remaskData() {
        if (hasMask) {
            byte[] bData = bbData.array();
            int iMask = iHeadLength - 4;
            for (int i = 0; i < iDataLength; i++) {
                bData[iHeadLength + i] = (byte) (bData[iHeadLength + i] ^ bData[iMask + (i & 0x03)]);
            }
        }
    }

    public boolean readCompleted() {
        return bbData.position() >= iHeadLength + iDataLength;
    }

    public void clearRecvBuffer() {
        bbData.clear();
        iDataLength = 0;
    }

    public int removeUsedData() {
        bbData.compact();
        int iLeft = bbData.position() - (iHeadLength + iDataLength);
        if (iLeft > 0) {
            bbData.position(0);
            bbData.put(bbData.array(), iHeadLength + iDataLength, iLeft);
            iDataLength = 0;
        } else {
            bbData.clear();
            iDataLength = 0;
        }
        return iLeft;
    }

    public boolean parseWsHead() {
        if (bbData.position() > 5) {
            byte[] bData = bbData.array();
            isEof = ((bData[0] >> 7) & 0x01) > 0;
            iMsgType = bData[0] & 0xF;
            hasMask = ((bData[1] >> 7) & 0x01) > 0;
            iDataLength = (bData[1] & 0x7F);
            iHeadLength = 2;
            if (hasMask) {
                iHeadLength += 4;
            }
            if (iDataLength == 126) {
                iDataLength = (bData[2] & 0x0FF);
                iDataLength <<= 8;
                iDataLength += (bData[3] & 0x0FF);
                iHeadLength += 2;
            } else if (iDataLength == 127) {
                iDataLength = (bData[2] & 0x0FF);
                for (int i = 3; i < 3 + 7; i++) {
                    iDataLength <<= 8;
                    iDataLength += (bData[i] & 0x0FF);
                }
                iHeadLength += 8;
            }
            return true;
        }
        return false;
    }

    public boolean sendData(String sData) {
        return sendData(scChannel, sData);
    }

    public boolean sendData(SocketChannel sc, String sData) {
        byte[] bData = sData.getBytes();
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
        return SocketChannelHelper.write(sc, bbSend) > 0;
    }
}
