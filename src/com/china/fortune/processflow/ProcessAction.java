package com.china.fortune.processflow;

import java.util.ArrayList;
import java.util.Stack;

import com.china.fortune.thread.ThreadUtils;

public abstract class ProcessAction {
	static final public int iStateMiss = -1;
	static final public int iStateWaitAndRunning = 0;
	static final public int iStateFinish = 1;
	static final public int iStateError = 2;
	static final public int iStateCancel = 3;

	private int iPowerPoint = 0;
	private int iRunStatus = iStateWaitAndRunning;
	private boolean bIsEndProcess = false;
	private Thread mSelThread = null;
	private ArrayList<ProcessAction> lsInProcess = new ArrayList<ProcessAction>(4);
	private ArrayList<ProcessAction> lsOutProcess = new ArrayList<ProcessAction>(4);
	
	abstract protected boolean onAction();
	
	public void deinit() {
		mSelThread = null;
		lsInProcess.clear();
		lsOutProcess.clear();
	}
	
	public int getStatus() {
		return iRunStatus;
	}
	
	public void setStatus(int s) {
		iRunStatus = s;
	}
	
	public void setEndProcess() {
		bIsEndProcess = true;
	}
	
	public boolean isFirstInProcess(ProcessAction pA) {
		if (lsInProcess.indexOf(pA) == 0) {
			return true;
		}
		return false;
	}
	
	public void setInProcess(ProcessAction pA) {
		lsInProcess.add(pA);
	}
	
	public void setOutProcess(ProcessAction pA) {
		lsOutProcess.add(pA);
		if (pA.isFirstInProcess(this)) {
			iPowerPoint++;
		}
	}
	
	public boolean isFinish() {
		if (iRunStatus != iStateWaitAndRunning) {
			return true;
		}
		return false;
	}
	
	public boolean isError() {
		if (iRunStatus == iStateError) {
			return true;
		}
		return false;	
	}
	
	private void waitInProcess() {
		for (ProcessAction pA : lsInProcess) {
			while (!pA.isFinish()) {
				ThreadUtils.sleep(50);
			}
		}
	}
	
	public void setOutProcessRunStatus(int iStatus) {
		for (ProcessAction pA : lsOutProcess) {
			if (!pA.isFinish()) {
				pA.setStatus(iStatus);
				pA.setOutProcessRunStatus(iStatus);
			}
		}
	}
	
	private void createOutProcess() {
		int iCreateThread = iPowerPoint;
		for (ProcessAction pA : lsOutProcess) {
			if (pA.isFirstInProcess(this)) {
				pA.start(iCreateThread > 1);
				iCreateThread--;
			}
		}
	}
	
	private void doAction() {
		waitInProcess();
		if (iRunStatus == iStateWaitAndRunning) {
			boolean rs = onAction();
			if (rs) {
				iRunStatus = iStateFinish;
			}
			else {
				setOutProcessRunStatus(iStateCancel);
				iRunStatus = iStateError;
			}
		}
		if (!bIsEndProcess) {
			createOutProcess();
		}	
	}
	
	public boolean start(boolean bCreateThread) {
		boolean hz = false;
		if (iRunStatus == iStateWaitAndRunning) {
			hz = true;
			if (bCreateThread) {
				mSelThread = new Thread()  {
		            public void run()  {
		            	doAction();
		            }
		        };
		        mSelThread.start();
			} else {
				doAction();
			}
		}
		return hz;
	};
	
	public void stop() {
		if (iRunStatus == iStateWaitAndRunning) {
			iRunStatus = iStateCancel;
		}
		if (mSelThread != null) {
			try {
				mSelThread.join();
			} catch (Exception e) {
			}
			mSelThread = null;
		}
	}
	
	public boolean isHead() {
		return (lsInProcess.size() == 0);
	}
	
	public boolean isTail() {
		return (lsOutProcess.size() == 0);
	}
	
	private boolean bStartPoint = false;
	public void setStartPoint() {
		if (lsInProcess.size() == 0) {
			bStartPoint = true;
		} else {
			for (ProcessAction pA : lsInProcess) {
				pA.setStartPoint();
			}
		}
	}
	
	public boolean isReached() {
		boolean rs = bStartPoint;
		if (lsInProcess.size() > 0) {
			rs = true;
			for (ProcessAction pA : lsInProcess) {
				if (!pA.isReached()) {
					rs = false;
					break;
				}
			}
		}
		return rs;
	}
	
	public boolean isRing() {
		Stack<ProcessAction> sPath = new Stack<ProcessAction>();
		return isRing(sPath);
	}
	
	private boolean isRing(Stack<ProcessAction> sPath) {
		boolean bRing = false;
		if (sPath.size() > 0) {
			int iCount = sPath.size() / 2;
			if (sPath.get(iCount).equals(this)) {
				bRing = true;
			}
		}
		if (!bRing) {
			for (ProcessAction pA : lsOutProcess) {
				sPath.push(this);
				if (pA.isRing(sPath)) {
					bRing = true;
					break;
				}
				sPath.pop();
			}
		}
		return bRing;
	}
	
	public boolean reachTo(ProcessAction pAHead) {
		boolean bReached = true;
		if (pAHead != this) {
			bReached = false;
			for (ProcessAction pA : lsOutProcess) {
				if (pA.reachTo(pAHead)) {
					bReached = true;
					break;
				}
			}
		}
		return bReached;
	}
}
