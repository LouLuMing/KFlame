package com.china.fortune.socket.pointToPoint;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.socket.SocketChannelHelper;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class P2PAccept extends P2PSendRecvOld {
    protected Thread tThread = null;

    protected abstract boolean onChannelAccept(SocketChannel sc);

    public void listenAndStartAndBlock(int iListenPort) {
        listenAndStart(iListenPort);
        ThreadUtils.join(tThread);
    }

    public boolean listenAndStart(int iListenPort) {
        tThread = new Thread() {
            @Override
            public void run() {
                ServerSocketChannel ssc = null;
                bRunning = true;
                while (bRunning) {
                    if (ssc == null) {
                        ssc = SocketChannelHelper.createServerSocket(iListenPort);
                        Log.logClass("Listen " + iListenPort);
                    }
                    if (ssc != null) {
                        SocketChannel sc = null;
                        try {
                            sc = ssc.accept();
                        } catch (Exception e) {
                            Log.logClass(e.getMessage());
                            SocketChannelHelper.closeServer(ssc);
                            ssc = null;
                            sc = null;
                        }

                        if (sc != null) {
                            Log.logClass("0. Connection");
                            if (onChannelAccept(sc)) {
                                SocketChannelHelper.closeServer(ssc);
                                startAndBlock(sc);
                            }
                            SocketChannelHelper.close(sc);
                        }
                    }
                }
                SocketChannelHelper.closeServer(ssc);
                Log.logClass("Close Listen " + iListenPort);
            }
        };
        tThread.start();
        return true;
    }

    public void stop() {
        bRunning = false;
        ThreadUtils.join(tThread);
    }
}
