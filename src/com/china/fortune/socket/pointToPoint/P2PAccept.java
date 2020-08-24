package com.china.fortune.socket.pointToPoint;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.socket.SocketChannelUtils;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class P2PAccept extends P2PChannel {
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
                        ssc = SocketChannelUtils.createServerSocket(iListenPort);
                        Log.logClass("Listen " + iListenPort);
                    }
                    if (ssc != null) {
                        SocketChannel sc = null;
                        try {
                            sc = ssc.accept();
                        } catch (Exception e) {
                            Log.logClass(e.getMessage());
                            SocketChannelUtils.closeServer(ssc);
                            ssc = null;
                            sc = null;
                        }

                        if (sc != null) {
                            Log.logClass("0. Connection");
                            if (onChannelAccept(sc)) {
                                SocketChannelUtils.closeServer(ssc);
                                startAndBlock(sc);
                            } else {
                                Log.logClass("1. Close Socket");
                            }
                            SocketChannelUtils.close(sc);
                        }
                    }
                }
                SocketChannelUtils.closeServer(ssc);
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
