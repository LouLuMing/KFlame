package com.china.fortune.statemachine;

import java.util.ArrayList;

public abstract class StateAction {
	protected class PathAction {
		public PathInterface pI = null;
		public StateAction sNext = null;
	}

	protected ArrayList<PathAction> lsPathAction = new ArrayList<PathAction>(4);

	abstract protected boolean onAction(Object owner);
	
	protected boolean bEndState = false;
	
	public void setEndState(boolean b) {
		bEndState = b;
	}
	
	public boolean isEndState() {
		return bEndState;
	}
	
	public StateAction doAction(Object owner) {
		StateAction sNext = null;
		if (onAction(owner)) {
			for (PathAction pA : lsPathAction) {
				if (pA.pI.onCondition(owner)) {
					if (pA.pI.onAction(owner)) {
						sNext = pA.sNext;
					}
					break;
				}
			}
		}
		return sNext;
	}

	public boolean addPath(PathInterface pathObj, StateAction sNext) {
		boolean rs = false;
		if (pathObj != null && sNext != null) {
			PathAction pA = new PathAction();
			pA.pI = pathObj;
			pA.sNext = sNext;
			lsPathAction.add(pA);
			rs = true;
		}
		return rs;
	}

	public void clear() {
		lsPathAction.clear();
	}
}
