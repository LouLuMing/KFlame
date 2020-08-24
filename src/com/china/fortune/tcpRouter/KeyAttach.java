package com.china.fortune.tcpRouter;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KeyAttach {
    public long lTicket;
    public int iPort;
    public ConcurrentLinkedQueue<ByteBuffer> lsSend = new ConcurrentLinkedQueue<>();

    public KeyAttach() {
        lTicket = System.currentTimeMillis();
    }

    public void setActive() {
        lTicket = System.currentTimeMillis();
    }
}
