package com.china.fortune.thread;

public class ThreadUtils {
    static public void sleep(int iMil) {
		try {
			Thread.sleep(iMil);
		} catch (Exception e) {
		}
    }

	static public void join(Thread t) {
    	if (t != null) {
			try {
				t.join();
			} catch (Exception e) {
			}
		}
	}
}
