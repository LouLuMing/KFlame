package com.china.fortune.processflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.china.fortune.global.Log;

public class ProcessManager {
	private HashMap<String, ProcessAction> lsProcess = new HashMap<String, ProcessAction>();
	private ArrayList<String> lsStartProcess = new ArrayList<String>(4);
	private ArrayList<String> lsEndProcess = new ArrayList<String>(4);
	
	public void addProcess(String name, ProcessAction pA) {
		lsProcess.put(name, pA);
	}
	
	public void addPath(String from, String to) {
		ProcessAction pAf = lsProcess.get(from);
		if (pAf != null) {
			ProcessAction pAt = lsProcess.get(to);
			if (pAt != null) {
				if (!pAt.reachTo(pAf)) {
					pAt.setInProcess(pAf);
					pAf.setOutProcess(pAt);
				} else {
					Log.logClass("isRing:" + from + ":" + to);
				}
			}
		}
	}
	
	public void setStopProcess(String end) {
		ProcessAction pA = lsProcess.get(end);
		if (pA != null) {
			if (pA.isReached()) {
				pA.setEndProcess();
				lsEndProcess.add(end);
				Log.logClass(end);
			} else {
				Log.logClass("notReach:" + end);
			}
		}
	}
	
	private void setStopProcessAuto() {
		for (Entry<String, ProcessAction> entry : lsProcess.entrySet()) {
			ProcessAction pA = entry.getValue();
			String end = entry.getKey();
			if (pA != null && end != null) {
				if (pA.isTail()) {
					if (pA.isReached()) {
						pA.setEndProcess();
						lsEndProcess.add(end);
						Log.logClass(end);
					}
				}
			}
		}
	}
	
	public void setStartProcess(String start) {
		ProcessAction pA = lsProcess.get(start);
		if (pA != null) {
			pA.setStartPoint();
			lsStartProcess.add(start);
			Log.logClass(start);
		}
	}
	
	private void setStartProcessAuto() {
		int iStartPoint = 0;
		for (Entry<String, ProcessAction> entry : lsProcess.entrySet()) {
			ProcessAction pA = entry.getValue();
			String start = entry.getKey();
			if (pA != null && start != null) {
				if (pA.isHead()) {
					pA.setStartPoint();
					lsStartProcess.add(start);
					Log.logClass(start);
					iStartPoint++;
				}
			}
		}
		if (iStartPoint == 0) {
			Log.logClass("NoStartProcess");
		}
	}
	
	public boolean start() {
		boolean rs = false;
		if (lsStartProcess.size() == 0) {
			setStartProcessAuto();
		}
		if (lsEndProcess.size() == 0) {
			setStopProcessAuto();
		}
		if (lsStartProcess.size() > 0) {
			if (lsEndProcess.size() > 0) {
				for (String start : lsStartProcess) {
					ProcessAction pA = lsProcess.get(start);
					if (pA != null) {
						pA.start(true);
					}
				}
				rs = true;
			} else {
				Log.logClass("NoEndProcess");
			}
		} else {
			Log.logClass("NoStartProcess");
		}
		return rs;
	}
	
	public void stop() {
		for (ProcessAction pA : lsProcess.values()) {
			pA.stop();
		}
	}
	
	public boolean isFinish() {
		boolean rs = true;
		for (String end : lsEndProcess) {
			ProcessAction pA = lsProcess.get(end);
			if (pA != null && !pA.isFinish()) {
				rs = false;
				break;
			}
		}
		return rs;
	}
	
	public boolean isError() {
		boolean rs = false;
		for (ProcessAction pA : lsProcess.values()) {
			if (pA.isError()) {
				rs = true;
				break;
			}
		}
		return rs;
	}
	
	public int getStatus(String p) {
		int iStatus = ProcessAction.iStateMiss;
		ProcessAction pA = lsProcess.get(p);
		if (pA != null) {
			iStatus = pA.getStatus();
		}
		return iStatus;
	}
	
	public boolean setStatus(String p, int iStatus) {
		boolean rs = false;
		ProcessAction pA = lsProcess.get(p);
		if (pA != null) {
			pA.setStatus(iStatus);
			rs = true;
		}
		return rs;
	}
	
	public void clear() {
		for (ProcessAction pA : lsProcess.values()) {
			pA.deinit();
		}
		lsProcess.clear();
		lsStartProcess.clear();
		lsEndProcess.clear();
	}
	

}
