package com.china.fortune.target.miniWeb.schedule;

import com.china.fortune.global.Log;
import com.china.fortune.restfulHttpServer.annotation.AsSchedule;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;

@AsSchedule(cron = "0/10")
public class ClearLogSchedule implements MsgInterface {
    @Override
    public void doAction(Object dbObj) {
        Log.logClass("Start");
        Log.clearHistory(30);
    }
}
