package com.china.fortune.target.tcpRouter;

import com.china.fortune.thread.LoopThread;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SendThread extends LoopThread {
    protected ConcurrentLinkedQueue<Integer> lsObjs = new ConcurrentLinkedQueue<Integer>();

    public void addObject(int data) {
        lsObjs.add(data);
    }
}
