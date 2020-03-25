package com.china.fortune.nginx;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.nginx.proxy.Proxy;
import com.china.fortune.nginx.proxy.ProxyList;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NginxServer extends HttpServerNioAttach implements TargetInterface {
	private ProxyManager proxyManager = new ProxyManager();
	private class HostSocket {
		SocketChannel from;
		SocketChannel to;
	}
	private ConcurrentLinkedQueue<HostSocket> qAddRead = new ConcurrentLinkedQueue<>();

	@Override
	protected void selectAction() {
		super.selectAction();
		while (!qAddRead.isEmpty()) {
			HostSocket ps = qAddRead.poll();
			if (ps != null) {
				SelectionKey skTo = addRead(ps.to);
				skTo.attach(ps.from);
			}
		}
	}

	public void addPairSocketToRead(SocketChannel from, SocketChannel to) {
		HostSocket hs = new HostSocket();
		hs.from = from;
		hs.to = to;
		qAddRead.add(hs);
	}

	protected void addResource(String resource, String path) {
		proxyManager.add(resource, path);
	}

	public NioSocketActionType doProxy(SelectionKey key, SocketChannel to, HttpServerRequest hRequest) {
		SocketChannel from = (SocketChannel) key.channel();
		hRequest.bbData.flip();
		if (SocketChannelHelper.blockWrite(to, hRequest.bbData, 3) > 0) {
			key.attach(to);
			addPairSocketToRead(from, to);
			qObjsForClient.add(hRequest);
			return NioSocketActionType.OP_NULL;
		}
		return NioSocketActionType.OP_CLOSE;
	}

	public NioSocketActionType doWrite(SelectionKey key, HttpServerRequest hRequest) {
		SocketChannel sc = (SocketChannel) key.channel();
		if (SocketChannelHelper.write(sc, hRequest.bbData) >= 0) {
			if (hRequest.bbData.remaining() == 0) {
				hRequest.clear();
				return NioSocketActionType.OP_READ;
			} else {
				return NioSocketActionType.OP_WRITE;
			}
		} else {
			return NioSocketActionType.OP_CLOSE;
		}
	}

	public NioSocketActionType doAction(SelectionKey key, HttpServerRequest hReq) {
		HttpResponse hRes = new HttpResponse();
		String sResource = hReq.getResource();
		NioSocketActionType hat = NioSocketActionType.OP_CLOSE;
		ProxyList pl = proxyManager.get(sResource);
		if (pl != null) {
			Proxy proxy = pl.get();
			while (proxy != null) {
				if (proxy.iPort > 0) {
					SocketChannel to = SocketChannelHelper.connect(proxy.sServer, proxy.iPort);
					if (to != null) {
						proxy.setResult(true);
						Log.log(sResource + " " + proxy.sPath);
						return doProxy(key, to, hReq);
					} else {
						Log.logError(sResource + " " + proxy.sPath);
						proxy.setResult(false);
					}
				} else {
					if (hReq.getHeaderValue("If-Modified-Since") != null
							|| hReq.getHeaderValue("If-Modified-Since") != null
							|| hReq.getHeaderValue("If-None-Match") != null) {
						hRes.setResponse(304);
						hReq.toByteBuffer(hRes);
						return doWrite(key, hReq);
					} else {
						String sFile = proxy.getLocation(sResource);
						if (hRes.putFile(sFile)) {

							Log.log(sResource + " " + sFile);
							hReq.toByteBuffer(hRes);
							return doWrite(key, hReq);
						} else {
							Log.logError(sResource + " " + sFile);
							break;
						}
					}
				}
				proxy = pl.get();
			}
		}
		proxyManager.doCommand(sResource, hReq, hRes);

		hReq.toByteBuffer(hRes);
		return doWrite(key, hReq);
	}

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		Object objForClient = key.attachment();
		if (objForClient != null) {
			SocketChannel sc = (SocketChannel) key.channel();
			if (objForClient instanceof HttpServerRequest) {
				HttpServerRequest hRequest = (HttpServerRequest) objForClient;
				NioSocketActionType op = hRequest.readHttpHead(sc, iMaxHttpHeadLength, iMaxHttpBodyLength);
				if (op == NioSocketActionType.OP_READ_COMPLETED) {
					if (hRequest.parseRequest()) {
						return doAction(key, hRequest);
					}
				} else {
					return op;
				}
			} else if (objForClient instanceof SocketChannel) {
				SocketChannel to = (SocketChannel)objForClient;
				ByteBuffer bb = (ByteBuffer) objForThread;
				try {
					bb.clear();
					if (sc.read(bb) > 0) {
						bb.flip();
						do {
							to.write(bb);
						} while (bb.remaining() > 0);
						return NioSocketActionType.OP_READ;
					}
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
			}
		}
		return NioSocketActionType.OP_CLOSE;
	}

	@Override
	protected boolean isInvalidSocket(long lLimit, Object objForClient) {
		if (objForClient != null) {
			if (objForClient instanceof HttpServerRequest) {
				HttpServerRequest hhb = (HttpServerRequest) objForClient;
				return hhb.lActiveTicket < lLimit;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onClose(SocketChannel sc, Object objForClient) {
		if (objForClient != null) {
			if (objForClient instanceof HttpServerRequest) {
				qObjsForClient.add((HttpServerRequest) objForClient);
			} else {
				SocketChannel to = (SocketChannel) objForClient;
				if (to != null) {
					freeKeyAndSocket(to);
				}
			}
		}
	}

	@Override
	protected Object createObjectInThread() {
		return ByteBuffer.allocate(32 * 1024);
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		return false;
	}

	protected void initAndStart(int iLocalPort) {
		setMaxHttpHeadLength(8 * 1024);
		setMaxHttpBodyLength(256 * 1024);
		startAndBlock(iLocalPort, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 8 + 1);
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
		Log.logClass("NginxServer start " + iLocalPort);
		refreshResourceMap(cfg);
		initAndStart(iLocalPort);
		Log.logClass("NginxServer stop");
		return true;
	}

	// http://127.0.0.1:30087/account/isregister?phone=18258448718
	static public void main(String[] args) {
		NginxServer obj = new NginxServer();
		obj.addResource("/account", "http://121.40.112.2:8900");
		obj.initAndStart(30087);
	}
}
