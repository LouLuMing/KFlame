package com.china.fortune.proxy;

import com.china.fortune.compress.GZipCompressor;
import com.china.fortune.file.FileUtils;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.proxy.host.Host;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.socket.selectorManager.NioSocketActionType;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HttpProxyRequest extends HttpServerRequest {
    public SelectionKey skClient;
    public SelectionKey skChannel;
    public HostList hostList;
    public Host host;
//    public boolean noChannel = true;
    public void clear() {
        super.clear();
        skClient = null;
        skChannel = null;
        hostList = null;
        host = null;
        closeFileChannel();
    }

    private FileChannel fileChannel = null;
    private FileInputStream fileStream = null;
    private long iFilePosition;
    private long iFileSize;
    public boolean openFileChannel(File file) {
        try {
            fileStream = new FileInputStream(file);
            if (fileStream != null) {
                fileChannel = fileStream.getChannel();
                iFilePosition = 0;
                iFileSize = fileChannel.size();
                return true;
            }
        } catch (Exception e) {
            Log.logException(e);
            closeFileChannel();
        }
        return false;
    }

    public void closeFileChannel() {
        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (Exception e) {}
            fileStream = null;
        }
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
                    return NioSocketActionType.NSA_READ;
                } else {
                    return NioSocketActionType.NSA_WRITE;
                }
            } else {
                return NioSocketActionType.NSA_CLOSE;
            }
        } else {
            reset();
            return NioSocketActionType.NSA_READ;
        }
    }

    public NioSocketActionType writeAndTransferFile(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (bbData.remaining() == 0) {
            return transferFile(key);
        } else {
            if (SocketChannelUtils.write(sc, bbData) >= 0) {
                if (bbData.remaining() == 0) {
                    return transferFile(key);
                } else {
                    return NioSocketActionType.NSA_WRITE;
                }
            } else {
                return NioSocketActionType.NSA_CLOSE;
            }
        }
    }

    public NioSocketActionType channelData(SocketChannel sc) {
        bbData.clear();
        if (SocketChannelUtils.read(sc, bbData) > 0) {
            bbData.flip();
            SocketChannel scTo = (SocketChannel) skClient.channel();
            if (SocketChannelUtils.write(scTo, bbData) >= 0) {
                if (bbData.remaining() == 0) {
                    reset();
                    skClient.interestOps(SelectionKey.OP_READ);
                    skChannel.interestOps(SelectionKey.OP_READ);
                } else {
                    skClient.interestOps(SelectionKey.OP_WRITE);
                }
                return NioSocketActionType.NSA_NULL;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    public boolean readFileOrChannel(String sFileName, boolean bGZip) {
        File file = new File(sFileName);
        if (file.exists() && file.isFile()) {
            long fileLen = file.length();
            setResponse(200);
            appendHeader(HttpHeader.csEtag, String.valueOf(file.lastModified()));
            appendFileHeader(sFileName);
            if (bGZip) {
                byte[] bData = FileUtils.readSmallFile(file);
                if (bData != null) {
                    if (fileLen > iMinGZipLength) {
                        bData = GZipCompressor.compress(bData);
                        appendHeader(csContentEncoding, "gzip");
                    }
                    if (bData != null) {
                        appendBody(bData);
                        return true;
                    }
                }
            } else if (fileLen < iMinGZipLength) {
                byte[] bData = FileUtils.readSmallFile(file);
                if (bData != null) {
                    appendBody(bData);
                    return true;
                }
            } else {
                appendHeader(csContentLength, String.valueOf(fileLen));
                appendString(csEnter);
                readyToWrite();
                return openFileChannel(file);
            }
        }
        return false;
    }

    public boolean readFile(String sFileName, boolean bGZip) {
        File file = new File(sFileName);
        if (file.exists() && file.isFile()) {
            setResponse(200);
            appendHeader(HttpHeader.csEtag, String.valueOf(file.lastModified()));
            appendFileHeader(sFileName);
            long fileLen = file.length();
            byte[] bData = FileUtils.readSmallFile(file);
            if (bData != null) {
                if (bGZip && fileLen > iMinGZipLength) {
                    bData = GZipCompressor.compress(bData);
                    appendHeader(csContentEncoding, "gzip");
                }
                if (bData != null) {
                    appendBody(bData);
                    return true;
                }
            }
        }
        return false;
    }
}
