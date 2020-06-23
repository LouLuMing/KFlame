package com.china.fortune.http.webservice;

import com.china.fortune.data.CacheClass;
import com.china.fortune.file.ReadFileAction;
import com.china.fortune.file.WriteFileAction;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerNioAttach;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.ChainServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.DataSaveInterface;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.struct.FastList;
import com.china.fortune.struct.HitCacheManager;
import com.china.fortune.timecontrol.TimeoutSetAction;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class WebServer extends HttpServerNioAttach {

	abstract protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);
	abstract protected void onException(HttpServerRequest hReq, HttpResponse hRes, Object objForThread);

	private TimeoutSetAction<Integer> setBlockIP = new TimeoutSetAction<Integer>(2, 18);

	public void blockIP(int IP) {
		setBlockIP.add(IP);
	}

	protected FastList<String> lsTag = new FastList<>();
	protected FastList<ServletInterface> lsServlet = new FastList<>();
	protected HitCacheManager hcm = new HitCacheManager();

	protected String actionToUrl(ServletInterface servlet) {
		return ActionToUrl.toUrl(servlet.getHost().getClass());
	}

	public int initHitCache() {
		int i = hcm.init(lsTag);
		hcm.showUsage();
		hcm.showDetail(lsTag);
		return i;
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

	protected ChainServlet addChainServlet(ServletInterface ba) {
		ChainServlet cs = new ChainServlet(ba);
		addServlet(cs);
		return cs;
	}

	protected void addServlet(String clsName) {
		try {
			Class<?> cls = Class.forName(clsName);
			if (cls != null) {
				Object obj = cls.newInstance();
				if (obj instanceof ServletInterface) {
					addServlet((ServletInterface)obj);
				}
			}
		} catch (Error e) {
			Log.logException(e);
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	protected void addServlet(ServletInterface servlet) {
		if (servlet != null) {
			lsTag.add(actionToUrl(servlet));
			lsServlet.add(servlet);
		}
	}

	protected void addServlet(String sTag, ServletInterface servlet) {
		if (servlet != null) {
			lsTag.add(sTag);
			lsServlet.add(servlet);
		}
	}

	public ServletInterface getServlet(int index) {
		if (index >= 0 && index < lsServlet.size()) {
			return lsServlet.get(index);
		}
		return null;
	}

	public ServletInterface getServlet(String sTag) {
		if (sTag != null) {
			int index = lsTag.indexOf(sTag);
			if (index >= 0) {
				return lsServlet.get(index);
			}
		}
		return null;
	}

	public int getServletSize() {
		return lsServlet.size();
	}

	protected ServletInterface getServletQuick(String sTag) {
		if (sTag != null) {
			int index = hcm.find(sTag);
			if (index >= 0) {
				return lsServlet.get(index);
			}
		}
		return null;
	}

	public void addFilter(ServletInterface siHost, ServletInterface siFilter) {
		if (siFilter != null) {
			for (int i = lsServlet.size() - 1; i >= 0; i--) {
				ServletInterface siShell = lsServlet.get(i);
				if (siShell != null) {
					if (siShell.getHost() == siHost) {
						if (siShell instanceof ChainServlet) {
							((ChainServlet) siShell).addChild(siFilter);
						} else {
							ChainServlet cs = new ChainServlet(siHost);
							lsServlet.set(i, cs);
							cs.addChild(siFilter);
						}
						break;
					}
				}
			}
		}
	}

	public void addFilter(ServletInterface siHost, Class<?> filter) {
		addFilter(siHost, getServlet(filter));
	}

	public ServletInterface getServlet(Class<?> cls) {
		for (int i = lsServlet.size() - 1; i >= 0; i--) {
			ServletInterface self = lsServlet.get(i).getHost();
			if (self.getClass() == cls) {
				return self;
			}
		}
		return null;
	}

	public String getResource(Class<?> cls) {
		for (int i = lsServlet.size() - 1; i >= 0; i--) {
			ServletInterface self = lsServlet.get(i).getHost();
			if (self.getClass() == cls) {
				return lsTag.get(i);
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
		ServletInterface si = getServlet(sSrc);
		if (si != null) {
			addServlet(sDes, si);
		}
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		boolean rs = true;
		String sTag = hReq.getResourceWithoutParam();
		ServletInterface si = getServletQuick(sTag);
		if (si != null) {
			try {
				rs = (si.doAction(hReq, hRes, objForThread) != ServletInterface.RunStatus.isClose);
			} catch (Exception e) {
				onException(hReq, hRes, objForThread);
				Log.logException(e);
			} catch (Error e) {
				onException(hReq, hRes, objForThread);
				Log.logException(e);
			}
		} else {
			onMissResource(hReq, hRes, objForThread);
		}
		return rs;
	}

	public ArrayList<String> sortTag() {
		ArrayList<String> lsNodes = new ArrayList<String>();
		for (int i = lsTag.size() - 1; i >= 0; i--) {
			lsNodes.add(lsTag.get(i));
		}
		if (lsNodes.size() > 0) {
			Collections.sort(lsNodes, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return StringAction.compareTo(o1, o2);
				}
			});
		}
		return lsNodes;
	}

	protected void addStatisticsServlet() {
		for (int i = 0; i < lsServlet.size(); i++) {
			ServletInterface si = lsServlet.get(i);
			if (si instanceof StatisticsServlet) {
			} else {
				StatisticsServlet ss = new StatisticsServlet(si);
				lsServlet.set(i, ss);
			}
		}
		csd.loadData(this);
	}

	public boolean saveToFile() {
		csd.saveData(this);
		return true;
	}

	private CacheClass csd = new CacheClass() {
		@Override
		protected void onSave(WriteFileAction wfa) {
			wfa.writeInt(lsServlet.size());
			for (int i = 0; i < lsServlet.size(); i++) {
				ServletInterface si = lsServlet.get(i);
				if (si != null && si instanceof StatisticsServlet) {
					wfa.writeString(lsTag.get(i));

					StatisticsServlet ss = (StatisticsServlet) si;
					wfa.writeLong(ss.getAccessCount());
					wfa.writeLong(ss.getAccessCost());
				}
			}
		}

		@Override
		protected void onLoad(ReadFileAction rfa) {
			int iSize = rfa.readInt();
			for (int i = 0; i < iSize; i++) {
				String sTag = rfa.readString();
				long lCount = rfa.readLong();
				long lCost = rfa.readLong();
				ServletInterface si = getServlet(sTag);
				if (si != null && si instanceof StatisticsServlet) {
					StatisticsServlet ss = (StatisticsServlet) si;
					ss.resetStatistics(lCount, lCost);
				}
			}
		}
	};
}
