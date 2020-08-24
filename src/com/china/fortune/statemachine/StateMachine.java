package com.china.fortune.statemachine;

import com.china.fortune.global.Log;

import java.util.HashMap;

// StateAction can't be used twice, must new instance for every use
public class StateMachine extends StateAction {
	protected HashMap<String, StateAction> lsState = new HashMap<String, StateAction>();
	protected String sStart = null;

	protected PathInterface pif = new PathInterface() {
		@Override
		public boolean onAction(Object owner) {
			return true;
		}
		@Override
		public boolean onCondition(Object owner)  {
			return true;
		}
	};
	
	public void addState(String name, StateAction sa) {
		lsState.put(name, sa);
	}

	public void addState(String name, StateMachine sm) {
		lsState.put(name, sm);
	}
	
	public boolean addPath(String from, String to, PathInterface pathImp) {
		boolean rs = false;
		StateAction sf = lsState.get(from);
		if (sf != null) {
			StateAction st = lsState.get(to);
			if (st != null) {
				sf.addPath(pathImp, st);
				rs = true;
			} else {
				Log.logClassError("miss:" + to);
			}
		} else {
			Log.logClassError("miss:" + from);
		}
		return rs;
	}
	
	public boolean addPath(String from, String to) {
		boolean rs = false;
		StateAction sf = lsState.get(from);
		if (sf != null) {
			StateAction st = lsState.get(to);
			if (st != null) {
				sf.addPath(pif, st);
				rs = true;
			}
		}
		return rs;
	}
	
	public void setStart(String s) {
		sStart = s;
	}
	
	public void setStop(String s) {
		StateAction sState = lsState.get(s);
		if (sState != null) {
			sState.setEndState(true);
		}
	}

	@Override
	protected boolean onAction(Object owner) {
		return doAction(owner) != null;
	}

	@Override
	public StateAction doAction(Object owner) {
		StateAction sState = lsState.get(sStart);
		while (sState != null) {
			StateAction sNextState = sState.doAction(owner);
			if (sState.isEndState()) {
				break;
			}
			sState = sNextState;
		}
		return sState;
	}

	public void clear() {
		for (StateAction sa : lsState.values()) {
			sa.clear();
		}
		lsState.clear();
	}
}
