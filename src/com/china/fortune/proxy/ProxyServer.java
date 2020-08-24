package com.china.fortune.proxy;

import com.china.fortune.global.CommonResource;
import com.china.fortune.global.Log;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.proxy.host.Host;
import com.china.fortune.proxy.host.HostList;
import com.china.fortune.socket.selectorManager.NioRWSerial;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringUtils;
import com.china.fortune.xml.XmlNode;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyServer extends NioRWSerial implements TargetInterface {
    private int iMaxHttpHeadLength = 2 * 1024;
    private int iMaxHttpBodyLength = 16 * 1024 * 1024;

    private ConcurrentLinkedQueue<HttpProxyRequest> qObjsForClient = new ConcurrentLinkedQueue<>();
    private ProxyManager proxyManager = new ProxyManager();

    public ProxyServer() {
        CommonResource.put(this);
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
                        return doAction(key, hReq);
                    }
                } else if (hReq.skClient == key) {
                    if (hReq.parseRequest()) {
                        return doAction(key, hReq);
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
    protected SelectionKey onAccept(SocketChannel sc) {
        HttpProxyRequest hhb = qObjsForClient.poll();
        if (hhb == null) {
            hhb = new HttpProxyRequest();
        }
        return registerRead(sc, hhb);
    }

    @Override
    protected NioSocketActionType onConnect(SelectionKey key, Object objForThread) {
        Object objForClient = key.attachment();
        if (objForClient != null) {
            HttpProxyRequest hReq = (HttpProxyRequest) objForClient;
            if (finishConnect(key)) {
                hReq.readyToWrite();
//                hReq.host.setResult(true);
                Log.log(hReq.getResource() + " " + hReq.host.sPath);
                return hReq.write(key);
            } else {
                hReq.setByteBuffer(HttpProp.bHR404);
//                hReq.host.setResult(false);
//                key.attach(null);
//                hReq.hostList = null;

                Log.logError(hReq.getResource() + " " + hReq.host.sPath);
                hReq.write(hReq.skClient);
                return NioSocketActionType.NSA_CLOSE;
            }
        }
        return NioSocketActionType.NSA_CLOSE;
    }

    @Override
    protected void onClose(SelectionKey key) {
        HttpProxyRequest hReq = (HttpProxyRequest) key.attachment();
        if (hReq != null) {
            if (hReq.skClient == key) {
                if (hReq.skChannel != null) {
                    freeKeyAndSocket(hReq.skChannel);
                }
            } else if (hReq.skChannel == key) {
                if (hReq.skClient != null) {
                    freeKeyAndSocket(hReq.skClient);
                }
            }
            hReq.clear();
            qObjsForClient.add(hReq);
        }
    }

    @Override
    protected Object createObjectInThread() {
        return null;
    }

    @Override
    protected void destroyObjectInThread(Object objForThread) {}

    protected void addResource(String resource, String path) {
        addResource(resource, path, false, false);
    }

    protected void addResource(String resource, String path, boolean cache, boolean gzip) {
        proxyManager.add(resource, path, cache, gzip);
    }

    public NioSocketActionType doAction(SelectionKey key, HttpProxyRequest hReq) {
        String sResource = hReq.getResource();
        HostList pl = proxyManager.getMatch(sResource);
        if (pl != null) {
            if (hReq.hostList == pl) {
                return hReq.proxyData(hReq.skChannel);
            } else {
                Host host = pl.get();
                if (host != null) {
                    if (host.isaRemote != null) {
                        SelectionKey skTo = addConnect(host.isaRemote, hReq);
                        if (skTo != null) {
                            if (hReq.skChannel != null) {
                                freeKeyAndSocket(hReq.skChannel);
                            }
                            hReq.host = host;
                            hReq.hostList = pl;
                            hReq.skChannel = skTo;
                            return NioSocketActionType.NSA_NULL;
                        } else {
                            return NioSocketActionType.NSA_CLOSE;
                        }
                    } else {
                        return toFileRequest(host, sResource, key, hReq);
                    }
                } else {
                    Log.log(sResource + " no path");
                    hReq.setByteBuffer(HttpProp.bHR404);
                    return hReq.write(key);
                }
            }
        } else {
            proxyManager.doCommand(sResource, hReq);
            return hReq.write(key);
        }
    }

    public void refreshCache(String sFileName) {
        cacheFiles.remove(sFileName);
//        byte[] bCache = cacheFiles.get(sFileName);
//        if (bCache != null) {
//            HttpResponse hRes = new HttpResponse();
//            hRes.parseHttpHeader(bCache);
//            boolean bGZip = "gzip".equals(hRes.getHeaderValue(csContentEncoding));
//            HttpProxyRequest hReq = new HttpProxyRequest();
//            if (hReq.readFile(sFileName, bGZip)) {
//                cacheFiles.put(sFileName, hReq.toByte());
//            }
//        }
    }

    private ConcurrentHashMap<String, byte[]> cacheFiles = new ConcurrentHashMap<>();
    private NioSocketActionType toFileRequest(Host proxy, String sResource, SelectionKey key, HttpProxyRequest hReq) {
        if (hReq.findIfNoneMatch()) {
            Log.log(sResource + " " + 304);
            hReq.setByteBuffer(HttpProp.bHR304);
        } else {
            String sFileName = proxy.getLocation(sResource);
            boolean bGZip = proxy.isGZip;
            if (proxy.isCache) {
                byte[] bCache = cacheFiles.get(sFileName);
                if (bCache == null) {
                    if (hReq.readFile(sFileName, bGZip)) {
                        cacheFiles.put(sFileName, hReq.toByte());
                        Log.log(sResource + " " + sFileName);
                    } else {
                        hReq.setByteBuffer(HttpProp.bHR404);
                        Log.logError(sResource + " " + sFileName);
                    }
                } else {
                    hReq.setByteBuffer(bCache);
                    Log.log(sResource + " " + sFileName);
                    return hReq.write(key);
                }
            } else {
                if (hReq.readFileOrChannel(sFileName, bGZip)) {
                    Log.log(sResource + " " + sFileName);
                    return hReq.writeAndTransferFile(key);
                } else {
                    Log.logError(sResource + " " + sFileName);
                    hReq.setByteBuffer(HttpProp.bHR404);
                }
            }
        }
        return hReq.write(key);
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
                        String cache = resource.getAttrValue("cache");
                        String gzip = resource.getAttrValue("gzip");
                        String[] lsPath = StringUtils.split(sPath, ';');
                        for (String path : lsPath) {
                            lsMap.add(sUrl, path, "1".equals(cache), "1".equals(gzip));
                        }
                    }

                }
            }
            proxyManager = lsMap;
        }
    }

    @Override
    public boolean doAction(ProcessAction self, XmlNode cfg) {
        int iLocalPort = StringUtils.toInteger(cfg.getChildNodeText("localport"));
        Log.logClass("ProxyServer start " + iLocalPort);
        refreshResourceMap(cfg);
        proxyManager.loadData();
        startAndBlock(iLocalPort);
        Log.logClass("ProxyServer stop");
        return true;
    }

    // http://127.0.0.1:30087/account/isregister?phone=18258448718
    static public void main(String[] args) {
        ProxyServer obj = new ProxyServer();
//        obj.addResource("/a.html", "z:\\");
//        obj.addResource("/slideValidate", "http://20.21.1.133:30082");
        obj.addResource("/", "http://20.21.1.133:8085");
        obj.startAndBlock(8989);
    }
}
