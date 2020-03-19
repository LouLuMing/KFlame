package com.china.fortune.restfulHttpServer.msgSystem;

import com.china.fortune.database.sql.InsertSql;
import com.china.fortune.global.Log;
import com.china.fortune.struct.EnConcurrentLinkedQueue;
import com.china.fortune.thread.AutoIncreaseThreadPool;
import com.china.fortune.timecontrol.timeout.TimeoutActionThreadSafe;

import java.util.HashMap;

public abstract class MsgSystem extends AutoIncreaseThreadPool {
	protected EnConcurrentLinkedQueue<Object> lsObjs = new EnConcurrentLinkedQueue<Object>(18);
	protected HashMap<Class<?>, MsgActionInterface> lsMsgAction = new HashMap<Class<?>, MsgActionInterface>();

	abstract protected void onTimer(Object dbObj);

	private TimeoutActionThreadSafe taObj = new TimeoutActionThreadSafe();

	private class OnTimerAction implements MsgInterface {
		@Override
		public void doAction(Object dbObj) {
			onTimer(dbObj);
		}
	}

	public void setLoop(long iMilSecond) {
		Log.logClass("Millisecond " + iMilSecond);
		taObj.setWaitTime(iMilSecond);
	}

	public void setLoopSecond(int iSecond) {
		Log.logClass("Second " + iSecond);
		taObj.setWaitTime(iSecond * 1000);
	}

	public void addSqlInsert(Object o) {
		InsertSql isa = new InsertSql();
		isa.addObject(o);
		addSql(isa.toSql());
	}

	public void addSql(String sSql) {
		SqlAction se = new SqlAction();
		se.sSql = sSql;
		lsObjs.add(se);
	}

	public void addObject(Object o) {
		lsObjs.add(o);
	}

	public void addMsgSerlvet(MsgActionInterface mai, Class<?> cls) {
		lsMsgAction.put(cls, mai);
	}
	@Override
	protected void doAction(Object objForThread) {
		Object o = lsObjs.poll();
		if (o != null) {
			if (o instanceof MsgInterface) {
				MsgInterface mi = (MsgInterface)o;
				mi.doAction(objForThread);
			} else {
				MsgActionInterface mai = lsMsgAction.get(o.getClass());
				if (mai != null) {
					mai.doAction(o, objForThread);
				} else {
					Log.logClassError("MsgActionInterface miss " + o.getClass().getSimpleName());
				}
			}
		}
	}

	private OnTimerAction ota = new OnTimerAction();
	@Override
	protected boolean haveThingsToDo(Object obj) {
		if (taObj.isTimeoutAndReset()) {
			lsObjs.add(ota);
		}
		return !lsObjs.isEmpty();
	}

}
