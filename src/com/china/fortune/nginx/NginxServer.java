package com.china.fortune.nginx;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NginxServer extends HttpServerNioAttach implements TargetInterface {
	private ActionManager actionManager = new ActionManager();

	protected void addResource(String resource, String path) {
		actionManager.add(resource, path);
	}

	@Override
	protected NioSocketActionType onRead(SelectionKey key, Object objForThread) {
		Object objForClient = key.attachment();
		if (objForClient != null) {
			HttpServerRequest hRequest = (HttpServerRequest)objForClient;
			SocketChannel sc = (SocketChannel) key.channel();
			NioSocketActionType op = hRequest.readHttpHead(sc, iMaxHttpHeadLength, iMaxHttpBodyLength);
			if (op == NioSocketActionType.OP_READ_COMPLETED) {
				if (hRequest.parseRequest()) {
					actionManager.doAction(hRequest);
					if (SocketChannelHelper.write(sc, hRequest.bbData) >= 0) {
						if (hRequest.bbData.remaining() == 0) {
							hRequest.clear();
							return NioSocketActionType.OP_READ;
						} else {
							return NioSocketActionType.OP_WRITE;
						}
					}
				}
			} else {
				return op;
			}
		}
		return NioSocketActionType.OP_CLOSE;
	}

	@Override
	protected Object createObjectInThread() {
		return null;
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		return false;
	}

	protected void initAndStart(int iLocalPort) {
		setMaxHttpHeadLength(16 * 1024);
		setMaxHttpBodyLength(16 * 1024);
		startAndBlock(iLocalPort, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 8 + 1);
	}

	public void refreshResourceMap(XmlNode cfg) {
		XmlNode resources = cfg.getChildNode("resources");
		if (resources != null) {
			ActionManager lsMap = new ActionManager();
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
			actionManager = lsMap;
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
