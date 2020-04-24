package com.china.fortune.nginx;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HostSocket {
    public SelectionKey from;
    public SocketChannel to;
}
