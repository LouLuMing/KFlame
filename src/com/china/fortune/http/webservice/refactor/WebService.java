package com.china.fortune.http.webservice.refactor;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.ServletUtils;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.socket.IPUtils;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;
import com.china.fortune.struct.HitCacheManager;
import com.china.fortune.timecontrol.TimeoutSetAction;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class WebService extends HttpServerNioAttach {

	abstract protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);

	private TimeoutSetAction<Integer> setBlockIP = new TimeoutSetAction<Integer>(2, 18);

	public void blockIP(int IP) {
		setBlockIP.add(IP);
	}

	protected FastList<String> lsTag = new FastList<String>();
	protected FastList<ServletInterface> lsServlet = new FastList<ServletInterface>();
	protected HitCacheManager hcm = new HitCacheManager();

	public int initHitCache() {
		return hcm.init(lsTag);
	}

	@Override
	protected boolean allowAccept(SocketChannel sc) {
		if (setBlockIP.size() > 0) {
			try {
				byte[] bAddr = ((InetSocketAddress) sc.getRemoteAddress()).getAddress().getAddress();
				return !setBlockIP.contains(IPUtils.bytes2Int(bAddr));
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

//	protected void addServlet(ServletInterface servlet) {
//		if (servlet != null) {
//			lsTag.add(servlet.getResource());
//			lsServlet.add(servlet);
//		}
//	}

	protected void addServlet(String sTag, ServletInterface servlet) {
		if (servlet != null) {
			lsTag.add(sTag);
			lsServlet.add(servlet);
		}
	}

	public ServletInterface getServlet(String sTag) {
		if (sTag != null) {
			int index = hcm.find(sTag);
			if (index >= 0) {
				return lsServlet.get(index);
			}
		}
		return null;
	}

	public ServletInterface getServlet(Class<?> cls) {
		for (int i = 0; i < lsServlet.size(); i++) {
			ServletInterface si = lsServlet.get(i);
			ServletInterface self = ServletUtils.getFinalHost(si);
			if (self.getClass() == cls) {
				return self;
			}
		}
		return null;
	}

//	public ServletInterface getServlet(Class<?> cls) {
//		for (int i = 0; i < lsServlet.size(); i++) {
//			ServletInterface si = lsServlet.get(i);
//			if (si.getClass() == cls) {
//				return si;
//			}
//		}
//		return null;
//	}

	protected void redirectServlet(String sSrc, String sDes) {
		for (int i = 0; i < lsTag.size(); i++) {
			if (StringUtils.compareTo(lsTag.get(i), sSrc) == 0) {
				ServletInterface si = lsServlet.get(i);
				addServlet(sDes, si);
				break;
			}
		}
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		boolean rs = true;
		String sTag = UrlParam.getResource(hReq.getResource());
		Log.logClass(sTag);
		ServletInterface si = getServlet(sTag);
		if (si != null) {
			rs = (si.doAction(hReq, hRes, objForThread) != ServletInterface.RunStatus.isClose);
		} else {
			onMissResource(hReq, hRes, objForThread);
		}
		return rs;
	}

	public ArrayList<String> sortTag() {
		ArrayList<String> lsNodes = new ArrayList<String>();
		for (int i = 0; i < lsTag.size(); i++) {
			lsNodes.add(lsTag.get(i));
		}
		if (lsNodes.size() > 0) {
			Collections.sort(lsNodes, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return StringUtils.compareTo(o1, o2);
				}
			});
		}
		return lsNodes;
	}
}
