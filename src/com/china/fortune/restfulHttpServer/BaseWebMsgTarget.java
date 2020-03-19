package com.china.fortune.restfulHttpServer;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.global.Log;
import com.china.fortune.restfulHttpServer.msgSystem.MsgActionInterface;
import com.china.fortune.restfulHttpServer.msgSystem.MsgInterface;
import com.china.fortune.thread.AutoIncreaseThreadPool;
import com.china.fortune.timecontrol.timeout.TimeoutActionThreadSafe;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseWebMsgTarget extends BaseWebTarget {
    abstract protected void onTimer(MySqlDbAction dbObj);

    protected TimeoutActionThreadSafe taObj = new TimeoutActionThreadSafe();
    protected ConcurrentLinkedQueue<Object> lsMessage = new ConcurrentLinkedQueue<>();
    protected HashMap<Class<?>, MsgActionInterface> lsMsgAction = new HashMap<Class<?>, MsgActionInterface>();

    public void addMsgSerlvet(Class<?> cls, MsgActionInterface mai) {
        lsMsgAction.put(cls, mai);
    }

    private MsgInterface ota = ((Object dbObj) -> onTimer((MySqlDbAction)dbObj));

    protected void setLoopSecond(int iSecond) {
        taObj.setWaitTime(iSecond * 1000);
    }

    protected AutoIncreaseThreadPool msgServer = new AutoIncreaseThreadPool() {
        @Override
        protected Object onCreate() {
            return mySqlManager.get();
        }

        @Override
        protected void doAction(Object obj) {
            Object o = lsMessage.poll();
            if (o != null) {
                if (o instanceof MsgInterface) {
                    MsgInterface mi = (MsgInterface)o;
                    mi.doAction(obj);
                } else {
                    MsgActionInterface mai = lsMsgAction.get(o.getClass());
                    if (mai != null) {
                        mai.doAction(o, obj);
                    } else {
                        Log.logClassError("MsgActionInterface miss " + o.getClass().getSimpleName());
                    }
                }
            }
        }

        @Override
        protected boolean haveThingsToDo(Object obj) {
            if (taObj.isTimeoutAndReset()) {
                return lsMessage.add(ota);
            } else {
                return !lsMessage.isEmpty();
            }
        }

        @Override
        protected void onDestroy(Object obj) {
            mySqlManager.free((MySqlDbAction) obj);
        }
    };

    public void addMessage(MsgInterface o) {
        lsMessage.add(o);
    }

    @Override
    protected boolean startWeb(int iPort) {
        setLoopSecond(60 * 5);
        msgServer.start(1);
        return super.startWeb(iPort);
    }

}
