package com.china.fortune.socket.pointToPoint;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.thread.ThreadUtils;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class P2PChannel {
    protected int iMaxFreeTime = 60 * 1000;
    protected int iSleepTime = 50;
    protected int iMaxFreeCount = iMaxFreeTime / iSleepTime;
    protected int iByteBufferLen = 16 * 1024; //1460;
    protected ConcurrentLinkedQueue<ByteBuffer> qFreeQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    protected ConcurrentLinkedQueue<ByteBuffer> qSendQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    protected ConcurrentLinkedQueue<ByteBuffer> qRecvQueue = new ConcurrentLinkedQueue<ByteBuffer>();

    protected int iRecvTimeout = 50;
    protected boolean bRunning = false;
    protected SocketChannel scChannel = null;

    protected abstract boolean onRead(int port, ByteBuffer bb);
    protected abstract void onClose(int port);
    protected abstract void onOpen(int port);

    protected ByteBuffer getBuffer(int port, int cmd) {
        ByteBuffer bb = qFreeQueue.poll();
        if (bb == null) {
            bb = ByteBuffer.allocate(iByteBufferLen);
        } else {
            bb.clear();
        }
        bb.putInt((port<<1)+cmd);
        bb.putInt(0);
        return bb;
    }

    public ByteBuffer getBuffer(int port) {
        return getBuffer(port, 0);
    }

    public void showBuffer(String sTag, ByteBuffer bb) {
        Log.log(sTag + " " + byteBufferToString(bb));
    }

    public String byteBufferToString(ByteBuffer bb) {
        return ByteAction.toHexString(bb.array(), 0, bb.limit());
    }

    private void putData(ByteBuffer bbRecv, int iLen) {
        ByteBuffer bb = qFreeQueue.poll();
        if (bb == null) {
            bb = ByteBuffer.allocate(iByteBufferLen);
        } else {
            bb.clear();
        }
        bb.put(bbRecv.array(), 0, iLen);
        bb.flip();
        qRecvQueue.add(bb);
    }

    public void freeBuffer(ConcurrentLinkedQueue<ByteBuffer> queueWrite) {
        do {
            ByteBuffer bb = queueWrite.poll();
            if (bb != null) {
                qFreeQueue.add(bb);
            } else {
                break;
            }
        } while (true);
    }

    public void freeBuffer(ByteBuffer bb) {
        qFreeQueue.add(bb);
    }

    private boolean bSendAndRecv = true;

    private void sendThread() {
        while (bRunning && bSendAndRecv) {
            ByteBuffer bb = qSendQueue.poll();
            if (bb != null) {
                try {
                    do {
                        scChannel.write(bb);
                        iFreeDays = 0;
                    } while (bb.remaining() > 0);
                } catch (Exception e) {
                    bSendAndRecv = false;
                    Log.logException(e);
                }
                qFreeQueue.add(bb);
            } else {
                ThreadUtils.sleep(iSleepTime);
            }
        }
    }

    private void readThread() {
        while (bRunning && bSendAndRecv) {
            ByteBuffer bb = qRecvQueue.poll();
            if (bb != null) {
                int opra = bb.getInt(0);
                int len = bb.getInt(4);
                int port = opra >> 1;
                if (len > 8) {
                    bb.position(8);
                    onRead(port, bb);
                } else {
                    int op = opra & 0x01;
                    if (op == 0) {
                        onClose(port);
                    } else if (op == 1) {
                        onOpen(port);
                    }
                    qFreeQueue.add(bb);
                }
            } else {
                iFreeDays++;
                ThreadUtils.sleep(iSleepTime);
                closeTimeout();
            }
        }
    }

    private long iFreeDays = 0;

    protected void closeTimeout() {
        if (iFreeDays > iMaxFreeCount) {
            SocketChannelUtils.close(scChannel);
            Log.logClass("iFreeDays " + iFreeDays);
        }
    }

    protected void startAndBlock(SocketChannel sc) {
        Log.logClass("Start");
        iFreeDays = 0;
        this.scChannel = sc;
        try {
            scChannel.socket().setSoLinger(true, 0);
            scChannel.socket().setSoTimeout(iRecvTimeout);
        } catch (Exception e) {
            Log.logException(e);
        }
        bSendAndRecv = true;

        Thread tSendThread = new Thread(()->sendThread());
        tSendThread.start();

        Thread tReadThread = new Thread(()->readThread());
        tReadThread.start();

        ByteBuffer bbRecv = ByteBuffer.allocate(iByteBufferLen * 2);
        while (bRunning && bSendAndRecv) {
            try {
                int iRecv = scChannel.read(bbRecv);
                if (iRecv > 0) {
                    iFreeDays = 0;
                    while (bbRecv.position() >= 8) {
                        int pos = bbRecv.position();
                        int packetLen = bbRecv.getInt(4);
                        if (pos >= packetLen) {
                            putData(bbRecv, packetLen);
                            if (pos > packetLen) {
                                bbRecv.position(0);
                                bbRecv.put(bbRecv.array(), packetLen, pos - packetLen);
                            } else {
                                bbRecv.clear();
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    bSendAndRecv = false;
                }
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                bSendAndRecv = false;
                Log.logClassError(e.getMessage());
            }
        }

        ThreadUtils.join(tReadThread);
        ThreadUtils.join(tSendThread);
        Log.logClass("End");
    }

    public void sendBuffer(ByteBuffer bb) {
        bb.putInt(4, bb.position());
        bb.flip();
        qSendQueue.add(bb);
    }

    public void sendCloseEvent(int port) {
        ByteBuffer bb = getBuffer(port);
        sendBuffer(bb);
    }

    public void sendOpenEvent(int port) {
        ByteBuffer bb = getBuffer(port, 1);
        sendBuffer(bb);
    }
}
