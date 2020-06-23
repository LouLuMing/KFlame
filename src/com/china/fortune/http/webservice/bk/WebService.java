package com.china.fortune.http.webservice.bk;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ChainServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.http.webservice.servlet.ServletInterface.RunStatus;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.struct.FastList;
import com.china.fortune.timecontrol.TimeoutSetAction;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class WebService extends HttpServerNioAttach {
	public int calServletHash() {
		return 0;
	};

	abstract protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);

	protected HashMap<String, ServletInterface> mapServlet = new HashMap<String, ServletInterface>();
	protected FastList<String> lsTag = new FastList<String>();
	protected FastList<ServletInterface> lsServlet = new FastList<ServletInterface>();

	private TimeoutSetAction<Integer> setBlockIP = new TimeoutSetAction<Integer>(2, 18);

	public void blockIP(int IP) {
		setBlockIP.add(IP);
	}

	@Override
	protected boolean allowAccept(SocketChannel sc) {
		if (setBlockIP.size() > 0) {
			try {
				byte[] bAddr = ((InetSocketAddress) sc.getRemoteAddress()).getAddress().getAddress();
				return !setBlockIP.contains(IPHelper.bytes2Int(bAddr));
			} catch (Exception e) {
				Log.logException(e);
			}
		}
		return true;
	}

//	protected ChainServlet addChainServlet(ServletInterface ba) {
//		ChainServlet cs = new ChainServlet(ba);
//		addServlet(cs);
//		return cs;
//	}
//
//	public void addServlet(ServletInterface servlet) {
//		if (servlet != null) {
//			mapServlet.put(servlet.getResource(), servlet);
//			lsTag.add(servlet.getResource());
//			lsServlet.add(servlet);
//		}
//	}

	public void addServlet(String sTag, ServletInterface servlet) {
		if (sTag != null) {
			mapServlet.put(sTag, servlet);
			lsTag.add(sTag);
			lsServlet.add(servlet);
		}
	}

	public ServletInterface getServlet(String sTag) {
		return mapServlet.get(sTag);
	}

	public ServletInterface getServlet(Class<?> cls) {
		for (ServletInterface si : mapServlet.values()) {
			ServletInterface self = si.getHost();
			if (self.getClass() == cls) {
				return self;
			}
		}
		return null;
	}

	protected void redirectServlet(Class<?> cls, String sDes) {
		for (Entry<String, ServletInterface> en : mapServlet.entrySet()) {
			Object o = en.getValue();
			if (((ServletInterface) o).getHost().getClass() ==  cls) {
				mapServlet.put(sDes, en.getValue());
				break;
			}
		}

	}

	protected void redirectServlet(String sSrc, String sDes) {
		HashMap<String, ServletInterface> mapRedirect = new HashMap<String, ServletInterface>();
		for (Entry<String, ServletInterface> en : mapServlet.entrySet()) {
			String sUrl = en.getKey();
			if (sUrl.startsWith(sSrc)) {
				if (sUrl.length() > sSrc.length()) {
					String sTail = sUrl.substring(sSrc.length());
					mapRedirect.put(sDes + sTail, en.getValue());
				} else {
					mapRedirect.put(sDes, en.getValue());
				}
			}
		}
		mapServlet.putAll(mapRedirect);
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		boolean rs = true;
		String sResource = UrlParam.getResource(hReq.getResource());
		ServletInterface si = mapServlet.get(sResource);
		if (si != null) {
			rs = (si.doAction(hReq, hRes, objForThread) != RunStatus.isClose);
		} else {
			onMissResource(hReq, hRes, objForThread);
		}
		return rs;
	}

	public ArrayList<String> sortTag() {
		ArrayList<String> lsNodes = new ArrayList<String>(mapServlet.keySet());
		if (lsNodes.size() > 0) {
			Collections.sort(lsNodes, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return StringAction.compareTo(o1, o2);
				}
			});
		}
		return lsNodes;
	}

}
