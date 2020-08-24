package com.china.fortune.socket;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SocketChannelUtils {
    static final private int iConnectTimeout = 2500;
    static final private int iSoTimeout = 5000;

    static public void setNoBlocking(SocketChannel sc) {
        try {
            sc.socket().setSoLinger(true, 0);
            sc.configureBlocking(false);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
    }
    static public SocketChannel connect(InetSocketAddress isa) {
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            if (sc != null && sc.socket() != null) {
                sc.socket().connect(isa, iConnectTimeout);
            }
        } catch (Exception e) {
            Log.logClass(e.getMessage());
            sc = null;
        }
        return sc;
    }

    static public SocketChannel connect(String ip, int port) {
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            if (sc != null && sc.socket() != null) {
                InetSocketAddress isa = new InetSocketAddress(ip, port);
                sc.socket().connect(isa, iConnectTimeout);
            }
        } catch (Exception e) {
            Log.logClass(e.getMessage());
            sc = null;
        }
        return sc;
    }

    static public ServerSocketChannel createServerSocket(int iListenPort) {
        ServerSocketChannel ssc = null;
        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(iListenPort));
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return ssc;
    }

    static public void closeServer(ServerSocketChannel ssc) {
        if (ssc != null) {
            try {
                ssc.close();
            } catch (Exception e) {
                Log.logClass(e.getMessage());
            }
        }
    }

    static public void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e) {
            Log.logClass(e.getMessage());
        }
    }

    static public int read(SocketChannel sc, ByteBuffer bb) {
        int iRecv;
        try {
            iRecv = sc.read(bb);
        } catch (Exception e) {
            iRecv = -1;
            Log.logClass(e.getMessage());
        }
        return iRecv;
    }

    static public int write(SocketChannel sc, ByteBuffer bb) {
        int iSend;
        try {
            iSend = sc.write(bb);
        } catch (Exception e) {
            iSend = -1;
            Log.logClass(e.getMessage());
        }
        return iSend;
    }

    static public int blockWrite(SocketChannel sc, ByteBuffer bb, int iRetry) {
        int total = 0;
        try {
            do {
                int len = sc.write(bb);
                if (len > 0) {
                    total += len;
                    if (bb.remaining() == 0) {
                        break;
                    }
                } else if (len == 0) {
                    iRetry--;
                    if (iRetry > 0) {
                        ThreadUtils.sleep(50);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } while (true);
        } catch (Exception e) {
            Log.logException(e);
        }
        return total;
    }

    static public int loopWrite(SocketChannel sc, ByteBuffer bb) {
        int len = 0;
        do {
            len += SocketChannelUtils.write(sc, bb);
        } while (len > 0 && bb.remaining() > 0);
        return len;
    }

}
