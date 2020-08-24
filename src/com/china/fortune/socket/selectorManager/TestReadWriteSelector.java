package com.china.fortune.socket.selectorManager;

import com.china.fortune.global.Log;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class TestReadWriteSelector {
    static private SelectionKey addAccept(Selector selector, int iPort) {
        SelectionKey selectionKey = null;
        if (iPort > 0) {
            try {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ServerSocket ss = ssc.socket();
                ss.bind(new InetSocketAddress(iPort));
                ssc.configureBlocking(false);

                selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT, null);
            } catch (Exception e) {
                selectionKey = null;
                Log.logClass(e.getMessage());
            }
            Log.logClass("Listen " + iPort);
        }
        return selectionKey;
    }

    static protected SelectionKey registerWrite(Selector selector, SocketChannel sc) {
        SelectionKey selectionKey = null;
        try {
            selectionKey = sc.register(selector, SelectionKey.OP_WRITE, null);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return selectionKey;
    }

    static protected SelectionKey registerRead(Selector selector, SocketChannel sc) {
        SelectionKey selectionKey = null;
        try {
            selectionKey = sc.register(selector, SelectionKey.OP_READ, null);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return selectionKey;
    }

    static protected SelectionKey addWrite(Selector selector, SocketChannel sc) {
        SelectionKey selectionKey = null;
        try {
            sc.socket().setSoLinger(true, 0);
            sc.configureBlocking(false);
            selectionKey = sc.register(selector, SelectionKey.OP_WRITE, null);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return selectionKey;
    }

    static protected SelectionKey addRead(Selector selector, SocketChannel sc) {
        SelectionKey selectionKey = null;
        try {
            sc.socket().setSoLinger(true, 0);
            sc.configureBlocking(false);
            selectionKey = sc.register(selector, SelectionKey.OP_READ, null);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return selectionKey;
    }

    static protected SocketChannel accept(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = null;
        try {
            sc = ssc.accept();
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
        return sc;
    }

    static protected void freeKeyAndSocket(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            if (sc != null) {
                sc.close();
            }
            key.cancel();
            key.attach(null);
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Selector mReadSelector = Selector.open();
        Selector mWriteSelector = Selector.open();
        addAccept(mReadSelector, 8989);
        while (true) {
            int iSel = mReadSelector.selectNow();
            if (iSel > 0) {
                Set<SelectionKey> selectedKeys = mReadSelector.selectedKeys();
                if (selectedKeys != null) {
                    for (SelectionKey key : selectedKeys) {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                SocketChannel sc = accept(key);
                                if (sc != null) {
                                    addRead(mReadSelector, sc);
                                    addWrite(mWriteSelector, sc);
                                }
                            } else {

                            }
                        } else {
                            freeKeyAndSocket(key);
                        }
                    }
                    selectedKeys.clear();
                }
            }
        }
    }
}
