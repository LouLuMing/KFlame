package com.china.fortune.socket.pointToPoint;

import com.china.fortune.global.Log;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.thread.ThreadUtils;

import java.nio.channels.SocketChannel;

public abstract class P2PConnect extends P2PChannel {
    protected abstract void onConnect(SocketChannel sc);
    static final public int iRecvTimeout = 5000;
    protected Thread tThread = null;
    static private int iMaxErrorCount = 60;

    public boolean connectAndStart(String sServer, int iPort) {
        tThread = new Thread() {
            @Override
            public void run() {
                bRunning = true;
                int iError = 0;
                while (bRunning) {
                    try {
                        SocketChannel sc = SocketChannelUtils.connect(sServer, iPort);
                        if (sc != null) {
                            sc.socket().setSoTimeout(iRecvTimeout);
                            onConnect(sc);
                            long lNow = System.currentTimeMillis();
                            Log.logClass("Connect OK " + sServer + ":" + iPort);
                            startAndBlock(sc);
                            SocketChannelUtils.close(sc);
                            if (System.currentTimeMillis() - lNow > iMaxFreeTime / 2) {
                                iError = 0;
                            } else {
                                iError++;
                            }
                        } else {
                            iError++;
                        }
                        for (int i = 0; i < iError; i++) {
                            ThreadUtils.sleep(1000);
                        }
                        if (iError > iMaxErrorCount) {
                            iError = iMaxErrorCount;
                        }
                    } catch (Exception e) {
                        Log.logClassError(e.getMessage());
                    }
                }

            }
        };
        tThread.start();
        return true;
    }

    public void stop() {
        bRunning = false;
        if (tThread != null) {
            ThreadUtils.join(tThread);
        }
    }
}
