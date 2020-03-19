package com.china.fortune.statemachine;

import com.china.fortune.global.Log;

public class LogStateAction extends StateAction {
	public String sName;
	private StateAction innerSA = null;
	public LogStateAction(StateAction sa) {
		innerSA = sa;
		bEndState = sa.bEndState;
	}
	
	public void setName(String s) {
		sName = s;
	}
	
	@Override
	public StateAction doAction(Object owner) {
		StateAction sNext = null;
		boolean hz = onAction(owner);
		if (hz) {
			for (PathAction pA : lsPathAction) {
				hz = pA.pI.onCondition(owner);
				if (hz) {
					hz = pA.pI.onAction(owner);
					if (hz) {
						Log.log(sName + " to " + ((LogStateAction)pA.sNext).sName);
						sNext = pA.sNext;
					} else {
						Log.log(sName + " to " + ((LogStateAction)pA.sNext).sName + " path onAction error");
					}
					break;
				}
			}
			if (hz && sNext == null) {
				Log.log(sName + ":no path");
			}
		} else {
			Log.log(sName + ":onAction error");
		}
		return sNext;
	}

	@Override
	protected boolean onAction(Object owner) {
		return innerSA.onAction(owner);
	}
}
