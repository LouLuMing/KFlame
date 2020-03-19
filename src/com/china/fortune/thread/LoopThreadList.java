package com.china.fortune.thread;

import java.util.ArrayList;

public class LoopThreadList {
	private ArrayList<LoopThread> lsThreads = new ArrayList<LoopThread>();
	
	public void add(LoopThread t) {
		lsThreads.add(t);
	}
	
	public void start() {
		for (Thread t : lsThreads) {
			t.start();
		}
	}
	
	public void waitToStop() {
		for (LoopThread t : lsThreads) {
			t.waitToStop();
		}
		lsThreads.clear();
	}

	public void join() {
		for (LoopThread t : lsThreads) {
			try {
				t.join();
			} catch (Exception e) {
			}
		}
		lsThreads.clear();
	}

	public boolean isAllStop() {
		boolean bStop = true;
		for (LoopThread t : lsThreads) {
			if (t.isRunning()) {
				bStop = false;
				break;
			}
		}
		return bStop;
	}
}
