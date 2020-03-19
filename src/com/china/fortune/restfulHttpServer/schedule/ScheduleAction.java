package com.china.fortune.restfulHttpServer.schedule;

import com.china.fortune.global.Log;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;

public class ScheduleAction implements MsgInterface {
    public long startTicket;
    public long loopTicket;

    private MsgInterface scheduleObj = null;
    public ScheduleAction(MsgInterface mif) {
        scheduleObj = mif;
    }

    public ScheduleAction pNext;

    @Override
    public void doAction(Object dbObj) {
        scheduleObj.doAction(dbObj);
    }

    public boolean isIntimeAndReset(long lNow) {
        if (startTicket < lNow) {
            startTicket = lNow + loopTicket;
            return true;
        } else {
            return false;
        }
    }
}
