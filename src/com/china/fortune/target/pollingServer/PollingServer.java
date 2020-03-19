package com.china.fortune.target.pollingServer;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.server.WsServerRequest;
import com.china.fortune.secure.Digest;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public abstract class PollingServer extends HttpServerNioAttach {
    HashMap<String, Object> mapClinet = new HashMap<String, Object>();
    static final private int ciMaxDataLength = 1024 * 1024;

    abstract protected boolean onDataRecv(SocketChannel sc, WsServerRequest hReq, Object objForThread);

    @Override
    protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
        String sResource = hReq.getResource();
        Log.logClass(hReq.getMethod() + " " + sResource + ":" + hReq.getBody());

        HashMap<String, String> mapParam = UrlParam.findValuesLowerCase(sResource);
        String transport = mapParam.get("transport");
        if ("polling".compareToIgnoreCase(transport) == 0) {
            String sid = mapParam.get("sid");
            if (StringAction.length(sid) == 0) {
                StringBuilder sb = new StringBuilder();
                sid = Digest.getMD5(String.valueOf(System.currentTimeMillis()));
                mapClinet.remove(sid);

                sb.append("0{\"sid\":\"");
                sb.append(sid);
                sb.append("\",\"upgrades\":[\"websocket\"],\"pingInterval\":25000,\"pingTimeout\":5000}");

                hRes.addHeader("Set-Cookie", "io=" + sid + "; Path=/; HttpOnly");
                hRes.addHeader("Connection", "keep-alive");
                hRes.setBody(sb.length() + ":" + sb.toString() + "2:40", "text/plain");
            } else {
                if ("post".compareToIgnoreCase(hReq.getMethod()) == 0) {
                    String sBody = hReq.getBody();
                    hRes.addHeader("Set-Cookie", "io=" + sid + "; Path=/; HttpOnly");
                    hRes.addHeader("Connection", "keep-alive");
                    Log.logClass(sBody);

                    hRes.setBody("ok", "text/html");
                } else {
                    Object o = mapClinet.get(sid);
                    if (o == null) {
                        hRes.addHeader("Set-Cookie", "io=" + sid + "; Path=/; HttpOnly");
                        hRes.addHeader("Connection", "keep-alive");

                        StringBuilder sb = new StringBuilder();
                        sb.append("42");
                        sb.append("[\"open\",");
                        sb.append(System.currentTimeMillis());
                        sb.append(']');
                        hRes.setBody(sb.length() + ":" + sb.toString(), "text/plain");
                        Log.logClass(hRes.getBody());
                        mapClinet.put(sid, new Object());
                    } else {
//						return false;

                        hRes.addHeader("Set-Cookie", "io=" + sid + "; Path=/; HttpOnly");
                        hRes.addHeader("Connection", "keep-alive");

                        StringBuilder sb = new StringBuilder();
                        //sb.append("6");
                        sb.append("42");
                        sb.append("[\"ack\",");
                        sb.append(System.currentTimeMillis());
                        sb.append(']');
                        hRes.setBody(sb.length() + ":" + sb.toString(), "text/plain");
                        Log.logClass(hRes.getBody());
                    }
                }
            }
        } else if ("websocket".compareToIgnoreCase(transport) == 0) {
            String sid = mapParam.get("sid");
            hRes.setResponse(101, "Switching Protocols");
            hRes.addHeader("Upgrade", "websocket");
            hRes.addHeader("Connection", "Upgrade");
            hRes.addHeader("Sec-WebSocket-Accept", ByteAction.toBase64(Digest.toSHA(sid + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));
            hRes.setBody(null);
        }

        return true;
    }

    public boolean sendData(SocketChannel sc, String sData) {
        byte[] bData = sData.getBytes();
        ByteBuffer bbData = ByteBuffer.allocate(6 + bData.length);
        bbData.put((byte) 0x81);
        if (bData.length < 126) {
            bbData.put((byte) (bData.length));
        } else if (bData.length < 65535) {
            bbData.put((byte) (126));
            bbData.put((byte) (bData.length >> 8));
            bbData.put((byte) bData.length);
        } else {
            bbData.put((byte) (127));
            bbData.put((byte) (bData.length >> 24));
            bbData.put((byte) (bData.length >> 16));
            bbData.put((byte) (bData.length >> 8));
            bbData.put((byte) bData.length);
        }
        bbData.put(bData);
        bbData.flip();
        return SocketChannelHelper.write(sc, bbData) > 0;
    }

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            PollingRequest hRequest = (PollingRequest) objForClient;
            if (hRequest.isWebSocket) {
                boolean rs = true;
                SocketChannel sc = (SocketChannel) key.channel();
                WsServerRequest hReq = (WsServerRequest) objForClient;
                if (SocketChannelHelper.read(sc, hReq.bbData) > 0) {
                    if (hReq.iDataLength == 0) {
                        hReq.parseWsHead();
                        if (hReq.iDataLength > ciMaxDataLength) {
                            rs = false;
                        }
                    }
                    if (hReq.readCompleted()) {
                        hReq.remaskData();
                        String sBody = hReq.getBody();
                        if (sBody.compareToIgnoreCase("2probe") == 0) {
                            sendData(sc, "3probe");
                        } else {
                            onDataRecv(sc, hReq, objForThread);
                        }
                        hReq.removeUsedData();
                    }
                } else {
                    rs = false;
                }
                if (rs) {
                    return NioSocketActionType.OP_READ;
                }
            } else {
                return super.onRead(key, objForThread);
            }
        }
        return NioSocketActionType.OP_CLOSE;
    }

    @Override
    protected Object onAccept(SocketChannel sc) {
        PollingRequest hhb = (PollingRequest) qObjsForClient.poll();
        if (hhb == null) {
            hhb = new PollingRequest();
        } else {
            hhb.clear();
        }

        hhb.isWebSocket = false;
        InetSocketAddress isa = (InetSocketAddress) sc.socket().getRemoteSocketAddress();
        if (isa != null) {
            hhb.bRemoteAddr = isa.getAddress().getAddress();
        }
        return hhb;
    }

    public static void main(String[] args) {
        PollingServer ws = new PollingServer() {
            @Override
            protected Object createObjectInThread() {
                return null;
            }

            @Override
            protected void destroyObjectInThread(Object objForThread) {
            }

            @Override
            protected boolean onDataRecv(SocketChannel sc, WsServerRequest hReq, Object objForThread) {
                String sBody = hReq.getBody();
                Log.log(sBody);
                sendData(sc, sBody);
                return true;
            }
        };
        ws.startAndBlock(3399);
    }

}
