package com.china.fortune.socket.pointToPoint;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.socket.SocketChannelHelper;

import java.nio.channels.SocketChannel;

public abstract class P2PConnect extends P2PSendRecvOld {
    protected Thread tThread = null;
    static private int iMaxSleepCount = 60 * 5;
    static private int iMaxErrorCount = iMaxSleepCount * 2;
    public boolean connectAndStart(String sServer, int iPort) {
        tThread = new Thread() {
            @Override
            public void run() {
                bRunning = true;
                int iError = 0;
                while (bRunning) {
                    try {
                        SocketChannel sc = SocketChannelHelper.connect(sServer, iPort);
                        if (sc != null) {
                            sc.socket().setSoTimeout(ConstData.iRecvTimeout);
                            iError = 0;
                            Log.logClass("Connect OK " + sServer + ":" + iPort);
                            startAndBlock(sc);
                            SocketChannelHelper.close(sc);
                        } else {
                            iError++;
                            for (int i = 0; i < iError && i < iMaxSleepCount; i++) {
                                ThreadUtils.sleep(1000);
                            }
                            if (iError > iMaxErrorCount) {
                                iError = iMaxSleepCount;
                            }
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
