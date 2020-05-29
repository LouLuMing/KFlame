package com.china.fortune.socket.pointToPoint;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.thread.AutoThreadPool;
import com.china.fortune.thread.ThreadUtils;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class P2PSendRecv {
    private int iByteBufferLen = 16 * 1024; //1460;
    protected ConcurrentLinkedQueue<ByteBuffer> qFreeQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    protected ConcurrentLinkedQueue<ByteBuffer> qSendQueue = new ConcurrentLinkedQueue<ByteBuffer>();
    protected ConcurrentLinkedQueue<ByteBuffer> qRecvQueue = new ConcurrentLinkedQueue<ByteBuffer>();

    protected ConcurrentLinkedQueue<Integer> qRetry = new ConcurrentLinkedQueue<Integer>();

    protected int iRecvTimeout = 50;
    protected boolean bRunning = false;
    protected SocketChannel scChannel = null;

    protected abstract boolean onRead(int port, ByteBuffer bb);
    protected abstract void onClose(int port);

    public ByteBuffer getBuffer(int port,int cmd) {
        ByteBuffer bb = qFreeQueue.poll();
        if (bb == null) {
            bb = ByteBuffer.allocate(iByteBufferLen);
        }
        bb.clear();
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

    public void sendBuffer(ByteBuffer bb) {
        bb.putInt(4, bb.position());
        bb.flip();
        qSendQueue.add(bb);
    }

    private void putData(ByteBuffer bbRecv, int iLen) {
        ByteBuffer bb = qFreeQueue.poll();
        if (bb == null) {
            bb = ByteBuffer.allocate(iByteBufferLen);
        }
        bb.clear();
        bb.put(bbRecv.array(), 0, iLen);
        bb.flip();
        qRecvQueue.add(bb);
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
                        int len = scChannel.write(bb);
                        if (len > 0) {
                            lLiveTicket = System.currentTimeMillis();
                        }
                    } while (bb.remaining() > 0);
                } catch (Exception e) {
                    bSendAndRecv = false;
                    Log.logException(e);
                }
                qFreeQueue.add(bb);
            } else {
                ThreadUtils.sleep(50);
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
                    bb.position(0);
                    bb.put(bb.array(), 8, bb.limit() - 8);
                    bb.flip();
                    if (!onRead(port, bb)) {
                        qRetry.add(port);
                    }
                } else {
                    int op = opra & 0x01;
                    if (op == 0) {
                        onClose(port);
                    } else {
                        onRead(port, null);
                    }
                    bb.clear();
                }
                qFreeQueue.add(bb);
            } else {
                closeTimeout();
                ThreadUtils.sleep(50);
            }
        }
    }

    private class ReadThreads extends AutoThreadPool {
        @Override
        protected boolean doAction(Object objForThread) {
            Integer port = qRetry.poll();
            if (port != null) {
                if (!onRead(port, null)) {
                    qRetry.add(port);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onDestroy(Object objForThread) {
        }

        @Override
        protected Object onCreate() {
            return null;
        }
    }

    private long lLiveTicket = 0;

    public void closeTimeout() {
        if (System.currentTimeMillis() - lLiveTicket > 60 * 1000) {
            SocketChannelHelper.close(scChannel);
        }
    }

    protected void startAndBlock(SocketChannel sc) {
        Log.logClass("Start");
        lLiveTicket = System.currentTimeMillis();
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

        ReadThreads readThreaPool = new ReadThreads();
        readThreaPool.start();

        ByteBuffer bbRecv = ByteBuffer.allocate(iByteBufferLen * 2);
        while (bRunning && bSendAndRecv) {
            try {
                int iRecv = scChannel.read(bbRecv);
                if (iRecv > 0) {
                    lLiveTicket = System.currentTimeMillis();
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

        readThreaPool.waitToStop();
        ThreadUtils.join(tReadThread);
        ThreadUtils.join(tSendThread);
        Log.logClass("End");
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
