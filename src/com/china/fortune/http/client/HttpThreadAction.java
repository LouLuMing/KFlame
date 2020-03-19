package com.china.fortune.http.client;

import java.util.concurrent.atomic.AtomicInteger;

import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.LoopThreadList;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public abstract class HttpThreadAction {
	abstract public boolean doHttpRequest(LoopThread lt);
	private LoopThreadList ltl = new LoopThreadList();
	private AtomicInteger iRecv = new AtomicInteger(0);
	private AtomicInteger iLost = new AtomicInteger(0);
	private AtomicInteger iCost = new AtomicInteger(0);
	
	private TimeoutAction taTotal = new TimeoutAction();
	
	public int getRecvCount() {
		return iRecv.get();
	}
	
	public int getLostCount() {
		return iLost.get();
	}
	
	public int getCostCount() {
		return iCost.get();
	}
	
	public void start(int iThread) {
		for (int i = 0; i < iThread; i++) {
			LoopThread t = new LoopThread() {
				private TimeoutAction ta = new TimeoutAction();
				@Override
				public boolean doAction() {
					ta.start();
					if (doHttpRequest(this)) {
						iRecv.incrementAndGet();
						iCost.addAndGet(ta.getMilliseconds());
					} else {
						iLost.incrementAndGet();
					}
					return false;
				}
			};
			t.setSleep(0);
			ltl.add(t);
		}
		ltl.start();
		taTotal.start();
		ltl.isAllStop();
	}
	
	public String showStatus() {
		int iCount = iRecv.get();
		int iPerCost = 0;
		int iSecond = taTotal.getMilliseconds() / 1000;
		if (iCount > 0) {
			iPerCost = iCost.get() / iCount;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Second:");
		sb.append(iSecond);
		sb.append(" Count:");
		sb.append(iCount);
		sb.append(" PerCost:");
		sb.append(iPerCost);
		sb.append(" Lost:");
		sb.append(iLost.get());
		return sb.toString();
	}
	
	public void waitToStop() {
		ltl.waitToStop();
	}

	public void join() {
		ltl.join();
	}
}
