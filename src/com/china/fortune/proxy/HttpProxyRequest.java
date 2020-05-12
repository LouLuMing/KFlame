package com.china.fortune.proxy;

import com.china.fortune.global.Log;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;

import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpProxyRequest extends HttpServerRequest {
    public SelectionKey skClient;
    public SelectionKey skChannel;
    public HostList pl;

//    public boolean noChannel = true;
//    public void clear() {
//        super.clear();
//        closeFileChannel();
//    }

    private FileChannel fileChannel = null;
    private long iFilePosition;
    private long iFileSize;
    public boolean openFileChannel(String fileName) {
        Path file = Paths.get(fileName);
        if (Files.exists(file)) {
            try {
                fileChannel = (FileChannel) (Files.newByteChannel(file));
                iFilePosition = 0;
                iFileSize = fileChannel.size();
                return true;
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        return false;
    }

    public void closeFileChannel() {
        if (fileChannel != null) {
            try {
                fileChannel.close();
            } catch (Exception e) {}
            fileChannel = null;
        }
    }

    private NioSocketActionType transferFile(SelectionKey key) {
        if (fileChannel != null) {
            long transfer = -1;
            try {
                transfer = fileChannel.transferTo(iFilePosition, iFileSize-iFilePosition, (SocketChannel) key.channel());
            } catch (Exception e) {
            }
            if (transfer >= 0) {
                iFilePosition += transfer;
                if (iFilePosition >= iFileSize) {
                    reset();
                    closeFileChannel();
                    return NioSocketActionType.OP_READ;
                } else {
                    return NioSocketActionType.OP_WRITE;
                }
            } else {
                return NioSocketActionType.OP_CLOSE;
            }
        } else {
            reset();
            return NioSocketActionType.OP_READ;
        }
    }

    @Override
    public NioSocketActionType write(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (bbData.remaining() == 0) {
            return transferFile(key);
        } else {
            if (SocketChannelHelper.write(sc, bbData) >= 0) {
                if (bbData.remaining() == 0) {
                    return transferFile(key);
                } else {
                    return NioSocketActionType.OP_WRITE;
                }
            } else {
                return NioSocketActionType.OP_CLOSE;
            }
        }
    }
}
