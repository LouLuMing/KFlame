package com.china.fortune.tcpRouter;

import com.china.fortune.socket.pointToPoint.P2PAccept;
import com.china.fortune.socket.pointToPoint.P2PConnect;
import com.china.fortune.thread.ThreadUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ShowP2PSendRecv {
    private P2PAccept server = new P2PAccept() {
        @Override
        protected boolean onChannelAccept(SocketChannel sc) {
            return true;
        }

        @Override
        protected void onClose(int port) {
        }

        @Override
        protected void onOpen(int port) {

        }

        @Override
        protected boolean onRead(int port, ByteBuffer bb) {
            this.showBuffer("server", bb);
            ByteBuffer out = this.getBuffer(port);
            out.put(bb);
            this.sendBuffer(out);
            return true;
        }
    };

    private P2PConnect client = new P2PConnect() {
        @Override
        protected void onConnect(SocketChannel sc) {
        }

        @Override
        protected boolean onRead(int port, ByteBuffer bb) {
            this.showBuffer("client", bb);
            return true;
        }

        @Override
        protected void onClose(int port) {
        }

        @Override
        protected void onOpen(int port) {

        }
    };

    public void runServer() {
        server.listenAndStartAndBlock(9000);
    }

    public void runClient() {
        client.connectAndStart("127.0.0.1", 9000);
        while (true) {
            ByteBuffer out = client.getBuffer(1000);
            out.put("1234567890".getBytes());
            client.sendBuffer(out);
            ThreadUtils.sleep(1000);
        }
    }

    public void doAction() {
        server.listenAndStart(9000);
        client.connectAndStart("127.0.0.1", 9000);
        while (true) {
            ByteBuffer out = client.getBuffer(1000);
            out.put("0123456789".getBytes());
            client.sendBuffer(out);

//            out = server.getBuffer(1000);
//            out.put("1234567890".getBytes());
//            server.sendBuffer(out);

            ThreadUtils.sleep(1000);
        }
    }

    public static void main(String[] args) {
        ShowP2PSendRecv ts = new ShowP2PSendRecv();
        ts.doAction();
    }
}
