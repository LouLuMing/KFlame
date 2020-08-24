package com.china.fortune.socket;

import com.china.fortune.global.Log;
import com.china.fortune.statistics.Counter;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

import java.util.ArrayList;

public class PortScan {
    static final int icMaxPort = 65535;
    static final int icMaxThread = 50;
    static public int icRecvTimeout = 500;

    static public void setRecvTimeout(int l) {
        icRecvTimeout = l;
    }
    public ArrayList<Integer> doAction(String sServer) {
        return doAction(sServer, icMaxPort, icMaxThread);
    }

    public ArrayList<Integer> doAction(String sServer, int iThread) {
        return doAction(sServer, icMaxPort, iThread);
    }

    public void doActionAndLog(String sServer) {
        TimeoutAction ta = new TimeoutAction();
        ArrayList<Integer> lsOpen = doAction(sServer, 100);
        for (Integer port : lsOpen) {
            Log.log(sServer + ":" + port);
        }
        Log.logNoDate(sServer + " Port:" + lsOpen.size() + " Cost:" + ta.getMilliseconds());
    }

    public ArrayList<Integer> doAction(String sServer, int iMaxPort, int iThread) {
        Log.hideClasss(SocketAction.class);
        ArrayList<Integer> lsOpen = new ArrayList<>();
        Counter ctObj = new Counter(1, iMaxPort);
        for (int i = 0; i < iThread; i++) {
            Thread t = new Thread(() -> {
                while (true) {
                    int iPort = ctObj.get();
                    if (iPort <= iMaxPort) {
                        SocketAction sa = new SocketAction();
                        sa.setTimeOut(icRecvTimeout, icRecvTimeout);
                        if (sa.connect(sServer, iPort)) {
                            lsOpen.add(iPort);
                            sa.close();
                        }
                        ctObj.commit();
                    } else {
                        break;
                    }
                }
            });
            t.start();
        }
        while (!ctObj.isAllCommit()) {
            ThreadUtils.sleep(250);
        }
        return lsOpen;
    }
}
