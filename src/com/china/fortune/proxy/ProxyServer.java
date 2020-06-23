package com.china.fortune.proxy;

import com.china.fortune.compress.GZipCompressor;
import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.proxy.host.Host;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.socket.bk.NioRWAttach;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

import java.io.File;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.china.fortune.http.httpHead.HttpHeader.*;

public class ProxyServer extends NioRWAttach implements TargetInterface {
    private ConcurrentLinkedQueue<HttpProxyRequest> qObjsForClient = new ConcurrentLinkedQueue<>();
    private int iMaxHttpHeadLength = 2 * 1024;
    private int iMaxHttpBodyLength = 2 * 1024 * 1024;

    @Override
    protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hReq = (HttpProxyRequest) objForClient;
            SocketChannel sc = (SocketChannel) key.channel();
            NioSocketActionType op = hReq.readHttpHead(sc, iMaxHttpHeadLength, iMaxHttpBodyLength);
            if (op == NioSocketActionType.NSA_READ_COMPLETED) {
                if (hReq.skClient == null) {
                    if (hReq.parseRequest()) {
                        hReq.skClient = key;
                        return doAction(key, hReq, objForThread);
                    }
                } else if (hReq.skClient == key) {
                    if (hReq.parseRequest()) {
                        return doAction(key, hReq, objForThread);
                    }
                } else {
                    return hReq.proxyData(hReq.skClient);
                }
            } else {
                return op;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected NioSocketActionType onWrite(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hReq = (HttpProxyRequest) objForClient;
            return hReq.writeAndTransferFile(key);
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected boolean isInvalidSocket(long lLimit, SelectionKey key) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hhb = (HttpProxyRequest) objForClient;
            return hhb.lActiveTicket < lLimit;
        } else {
            return true;
        }
    }

    @Override
    protected SelectionKey onAccept(SocketChannel sc) {
        HttpProxyRequest hhb = qObjsForClient.poll();
        if (hhb == null) {
            hhb = new HttpProxyRequest();
        } else {
            hhb.clear();
        }
        return registerRead(sc, hhb);
    }

    @Override
    protected void onClose(SelectionKey key) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hReq = (HttpProxyRequest) objForClient;
            if (hReq.skClient == key) {
                if (hReq.skChannel != null) {
                    freeKeyAndSocket(hReq.skChannel);
                }
            } else if (hReq.skChannel == key) {
                if (hReq.skClient != null) {
                    freeKeyAndSocket(hReq.skClient);
                }
            }
            hReq.skClient = null;
            hReq.skChannel = null;
            hReq.hostList = null;
            hReq.host = null;
            hReq.closeFileChannel();
            qObjsForClient.add(hReq);
        }
    }

    @Override
    protected Object createObjectInThread() {
        return null;
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {
    }

    private ProxyManager proxyManager = new ProxyManager();

    protected void addResource(String resource, String path) {
        proxyManager.add(resource, path);
    }

//    private byte[] bHR304 = new HttpResponse(304).toByte();
//    private byte[] bHR404 = new HttpResponse(404).toByte();
    @Override
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hReq = (HttpProxyRequest) objForClient;
            if (finishConnect(key)) {
                hReq.readyToWrite();
                hReq.host.setResult(true);
                Log.log(hReq.getResource() + " " + hReq.host.sPath);
                return hReq.writeOrNo(key);
            } else {
                hReq.setByteBuffer(HttpProp.bHR404);
                hReq.host.setResult(false);
                key.attach(null);
                hReq.hostList = null;

                Log.logError(hReq.getResource() + " " + hReq.host.sPath);
                hReq.writeOrNo(hReq.skClient);
                return NioSocketActionType.NSA_CLOSE;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    public NioSocketActionType doAction(SelectionKey key, HttpProxyRequest hReq, Object objForThread) {
        String sResource = hReq.getResource();
        HostList pl = proxyManager.getMatch(sResource);
        if (pl != null) {
            if (hReq.hostList == pl) {
                return hReq.proxyData(hReq.skChannel);
            } else {
                Host host = pl.get();
                if (host != null) {
                    if (host.isaRemote != null) {
//                        hReq.host = host;
//                        hReq.hostList = pl;
//                        return doConnect(hReq, host.isaRemote);
                        SelectionKey skTo = addConnect(host.isaRemote);
                        if (skTo != null) {
                            if (hReq.skChannel != null) {
                                freeKeyAndSocket(hReq.skChannel);
                            }
                            hReq.host = host;
                            hReq.hostList = pl;
                            hReq.skChannel = skTo;
                            skTo.attach(hReq);
                            return NioSocketActionType.NSA_NULL;
                        } else {
                            return NioSocketActionType.NSA_CLOSE;
                        }
                    } else {
                        return toFileRequest(host, sResource, key, hReq, objForThread);
                    }
                }
            }
        }
        proxyManager.doCommand(sResource, hReq);
        return hReq.writeOrNo(key);
    }

    private boolean readFile(String sFileName, HttpProxyRequest hReq, boolean bGZip) {
        File file = new File(sFileName);
        if (file.exists() && file.isFile()) {
            hReq.setResponse(200);
            hReq.appendHeader(HttpHeader.csEtag, String.valueOf(file.lastModified()));
            hReq.appendFileHeader(sFileName);
            long fileLen = file.length();
            byte[] bData = FileHelper.readSmallFile(file);
            if (bData != null) {
                if (bGZip && fileLen > iMinGZipLength) {
                    bData = GZipCompressor.compress(bData);
                    hReq.appendHeader(csContentEncoding, "gzip");
                }
                if (bData != null) {
                    hReq.appendBody(bData);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean readFileOrChannel(String sFileName, HttpProxyRequest hReq, boolean bGZip) {
        File file = new File(sFileName);
        if (file.exists() && file.isFile()) {
            long fileLen = file.length();
            hReq.setResponse(200);
            hReq.appendHeader(HttpHeader.csEtag, String.valueOf(file.lastModified()));
            hReq.appendFileHeader(sFileName);
            if (bGZip) {
                byte[] bData = FileHelper.readSmallFile(file);
                if (bData != null) {
                    if (fileLen > iMinGZipLength) {
                        bData = GZipCompressor.compress(bData);
                        hReq.appendHeader(csContentEncoding, "gzip");
                    }
                    if (bData != null) {
                        hReq.appendBody(bData);
                        return true;
                    }
                }
            } else if (fileLen < iMinGZipLength) {
                byte[] bData = FileHelper.readSmallFile(file);
                if (bData != null) {
                    hReq.appendBody(bData);
                    return true;
                }
            } else {
                hReq.appendHeader(csContentLength, String.valueOf(fileLen));
                hReq.appendString(csEnter);
                hReq.readyToWrite();
                return hReq.openFileChannel(file);
            }
        }
        return false;
    }

    private ConcurrentHashMap<String, byte[]> cacheFiles = new ConcurrentHashMap<>();
    private NioSocketActionType toFileRequest(Host proxy, String sResource, SelectionKey key, HttpProxyRequest hReq, Object objForThread) {
        if (hReq.findIfNoneMatch()) {
            Log.log(sResource + " " + 304);
            hReq.setByteBuffer(HttpProp.bHR304);
        } else {
            String sFileName = proxy.getLocation(sResource);
            boolean bGZip = proxy.isGZip;
            if (proxy.isCache) {
                byte[] bCache = cacheFiles.get(sFileName);
                if (bCache == null) {
                    if (readFile(sFileName, hReq, bGZip)) {
                        bCache = hReq.toByte();
                        Log.log(sResource + " " + sFileName);
                        cacheFiles.put(sFileName, bCache);
                    } else {
                        Log.logError(sResource + " " + sFileName);
                        hReq.setByteBuffer(HttpProp.bHR404);
                    }
                } else {
                    Log.log(sResource + " " + sFileName);
                    hReq.setByteBuffer(bCache);
                    return hReq.writeOrNo(key);
                }
            } else {
                if (readFileOrChannel(sFileName, hReq, bGZip)) {
                    Log.log(sResource + " " + sFileName);
                    return hReq.writeAndTransferFile(key);
                } else {
                    Log.logError(sResource + " " + sFileName);
                    hReq.setByteBuffer(HttpProp.bHR404);
                }
            }
        }

        return hReq.writeOrNo(key);
    }

    protected void initAndStart(int iLocalPort) {
        startAndBlock(iLocalPort);
    }

    public void refreshResourceMap(XmlNode cfg) {
        XmlNode resources = cfg.getChildNode("resources");
        if (resources != null) {
            ProxyManager lsMap = new ProxyManager();
            for (int i = 0; i < resources.getChildCount(); i++) {
                XmlNode resource = resources.getChildNode(i);
                if (resource != null && "resource".equals(resource.getTag())) {
                    String sPath = resource.getText();
                    String sUrl = resource.getAttrValue("url");
                    if (sUrl != null && sPath != null) {
                        String[] lsPath = StringAction.split(sPath, ';');
                        for (String path : lsPath) {
                            lsMap.add(sUrl, path);
                        }
                    }
                    Log.log(sUrl + ":" + sPath);
                }
            }
            proxyManager = lsMap;
        }
    }

    @Override
    public boolean doAction(XmlNode cfg, ProcessAction self) {
        int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
        Log.logClass("ProxyServer start " + iLocalPort);
        refreshResourceMap(cfg);
        proxyManager.loadData();
        initAndStart(iLocalPort);
        Log.logClass("ProxyServer stop");
        return true;
    }

    // http://127.0.0.1:30087/account/isregister?phone=18258448718
    static public void main(String[] args) {
        ProxyServer obj = new ProxyServer();
//        obj.addResource("/a.html", "z:\\");
        obj.addResource("/", "http://20.21.1.170:8989");
        obj.initAndStart(8989);
    }
}
