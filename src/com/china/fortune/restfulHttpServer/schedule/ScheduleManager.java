package com.china.fortune.restfulHttpServer.schedule;

import com.china.fortune.common.DateAction;
import com.china.fortune.global.Log;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.BeansFamily;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;
import com.china.fortune.string.StringAction;

public abstract class ScheduleManager {
    private long iMinLoop = 60 * 60 * 1000;
    private ScheduleAction pHead = null;
    abstract public void addMessage(ScheduleAction scheduleAction);

    public long getMiniLoop() {
        return iMinLoop / 2;
    }

    public void addSchedule(MsgInterface mi, long lStart, long lLoop) {
        if (lLoop > 0) {
            Log.logClass(mi.getClass().getSimpleName() + " " + lStart + " " + lLoop);
            ScheduleAction sa = new ScheduleAction(mi);
            sa.startTicket = lStart;
            sa.loopTicket = lLoop;
            addSchedule(sa);
            if (iMinLoop > lLoop) {
                iMinLoop = lLoop;
            }
        }
    }

    private long[] lsMultiCode = {1000, 60 * 1000, 3600 * 1000, 24*3600*1000};
    public void addSchedule(MsgInterface mi, String cron) {
        String[] lsTime = StringAction.split(cron, ' ');
        if (lsTime != null) {
            long startTicket = 0;
            long loopTicket = 0;
            for (int i = 0; i < lsTime.length && i < lsMultiCode.length; i++) {
                String sTime = lsTime[i];
                int iDot = sTime.indexOf('/');
                if (iDot > 0) {
                    startTicket += StringAction.toInteger(sTime.substring(0, iDot)) * lsMultiCode[i];
                    loopTicket += StringAction.toInteger(sTime.substring(iDot)) * lsMultiCode[i];
                } else {
                    startTicket += StringAction.toInteger(sTime) * lsMultiCode[i];
                }
            }
            if (loopTicket > 0) {
                startTicket += DateAction.getTodayStartHour() * 3600 * 1000;
                addSchedule(mi, startTicket, loopTicket);
            } else {
                Log.logError("cron is error" + mi.getClass().getName());
            }
        } else {
            Log.logClassError("cron is Error " + cron);
        }
    }

    private void addSchedule(ScheduleAction saObj) {
        if (saObj.loopTicket > 0) {
            if (pHead != null) {
                ScheduleAction saCur = pHead;
                ScheduleAction saPrev = null;
                while (saCur != null) {
                    if (saObj.startTicket <= saCur.startTicket) {
                        saObj.pNext = saCur;
                        if (saPrev != null) {
                            saPrev.pNext = saObj;
                        } else {
                            pHead = saObj;
                        }
                        break;
                    }
                    saPrev = saCur;
                    saCur = saCur.pNext;
                }
                if (saCur == null) {
                    if (saPrev != null) {
                        saPrev.pNext = saObj;
                        saObj.pNext = null;
                    }
                }
            } else {
                pHead = saObj;
                saObj.pNext = null;
            }
        }
    }

    public boolean doAction() {
        int iLoop = 0;
        ScheduleAction saObj = pHead;
        while (saObj != null) {
            long lNow = System.currentTimeMillis();
            if (saObj.isIntimeAndReset(lNow)) {
                pHead = pHead.pNext;
                addMessage(saObj);
                addSchedule(saObj);
                iLoop++;
            } else {
                break;
            }
        }
        return iLoop > 0;
    }

    public void injectBeans(BeansFamily bf) {
        ScheduleAction saObj = pHead;
        while (saObj != null) {
            bf.injectFieldAutowired(saObj);
            saObj = saObj.pNext;
        }
    }
}
