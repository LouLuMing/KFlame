package com.china.fortune.restfulHttpServer;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.string.StringAction;

public class ServerAccess {
	private String sBaseIP = "127.0.0.1";
	private int iServerPort = 9600;
	private String sServerUrl = null;
	private String sBaseURL = null;
	private String sFileURL = null;
	private int iTcpPort = iServerPort + 1;
	private boolean bShowLog = false;

	public void showLog(boolean bShow) {
		bShowLog = bShow;
	}

	public void resetServer(String sBase, int iPort) {
		sBaseIP = sBase;
		iServerPort = iPort;
		resetServer();
	}

	private void resetServer() {
		sServerUrl = "http://" + sBaseIP;
		iTcpPort = iServerPort + 1;
		sBaseURL = sServerUrl + ":" + iServerPort;
		sFileURL = sServerUrl + ":" + (iServerPort + 2);
	}

	public String getBaseIP() {
		return sBaseIP;
	}

	public int getHttpPort() {
		return iTcpPort;
	}

	public int getTcpPort() {
		return iTcpPort;
	}

	public String getFileUrl() {
		return sFileURL;
	}
	
	public ServerAccess() {
		resetServer();
	}

	public ServerAccess(String sBase) {
		sBaseIP = sBase;
		parsePort();
		resetServer();
	}

	public ServerAccess(String sBase, int iPort) {
		sBaseIP = sBase;
		if (iPort > 0) {
            iServerPort = iPort;
        } else {
            parsePort();
        }
		resetServer();
	}

	public void parsePort() {
		if (!parsePort("localport")) {
			parsePort("WebPort");
		}
	}
	
	public boolean parsePort(String sKey) {
		String sFile = PathUtils.getCurrentDataPath(true) + "myAnt.xml";
		String sXml = FileHelper.readSmallFile(sFile, "utf-8");
		if (sXml != null) {
			String sPort = StringAction.findBetween(sXml, sKey, sKey);
			if (sPort != null) {
				iServerPort = StringAction.toInteger(sPort);
				if (bShowLog) {
					Log.logClass("iServerPort:" + iServerPort);
				}
				return true;
			}
		}
		return false;
	}

	public String postAndShow(String sPost, String sBody) {
		String sUrl = sBaseURL + sPost;
		String sRecv = HttpSendAndRecv.doPost(sUrl, sBody);
		if (bShowLog) {
			Log.logClass(sUrl + ":" + sBody + ":" + sRecv);
		}
		return sRecv;
	}
	
	public HttpResponse get(String sGet) {
		String sUrl = sBaseURL + sGet;
		return HttpSendAndRecv.doGetInner(sUrl);
	}

    public HttpResponse post(String sGet, String sBody, String sType) {
        String sUrl = sBaseURL + sGet;
        return HttpSendAndRecv.doPostInner(sUrl, sBody, sType);
    }

	public String getAndShow(Class<?> servlet, Object param) {
		UrlBuilder pb = new UrlBuilder(ActionToUrl.toUrl(servlet));
		pb.add(param);
		return getAndShow(pb.toString());
	}

	public String getAndShow(Class<?> servlet, Object param1, Object param2) {
		UrlBuilder pb = new UrlBuilder(ActionToUrl.toUrl(servlet));
		pb.add(param1);
		pb.add(param2);
		return getAndShow(pb.toString());
	}

	public String getAndShow(RestfulStringServlet la, String lsValues[]) {
		return getAndShow(la.showUrlParam(ActionToUrl.toUrl(la.getClass()), lsValues));
	}

	public String getAndShow(String sGet) {
		String sUrl = sBaseURL + sGet;
		String sRecv = HttpSendAndRecv.doGet(sUrl);
		if (bShowLog) {
			Log.logClass(sUrl + ":" + sRecv);
		}
		return sRecv;
	}

	public boolean isOK(String sRecv) {
		boolean bOK = false;
		if (sRecv != null) {
			try {
				JSONObject json = new JSONObject(sRecv);
				if (json.optInt("retcode") == 0) {
					bOK = true;
				} else {
					Log.logClass(json.optString("message"));
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return bOK;
	}

	public int sendAndCheckOK(String sGet) {
		int retcode = -1;
		if (sGet != null) {
			String sRecv = getAndShow(sGet);
			if (sRecv != null) {
				try {
					JSONObject json = new JSONObject(sRecv);
					retcode = json.optInt("retcode");
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
			}
		}
		return retcode;
	}

	public JSONObject parseData(String sRecv) {
		JSONObject data = null;
		if (sRecv != null) {
			try {
				JSONObject json = new JSONObject(sRecv);
				if (json.optInt("retcode") == 0) {
					data = json.optJSONObject("data");
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return data;
	}

	public JSONObject httpGetAndParseData(String sGet) {
		String sRecv = getAndShow(sGet);
		JSONObject data = null;
		if (sRecv != null) {
			data = parseData(sRecv);
		}
		return data;
	}

    public int httpGetAndParseDataOptInt(String sGet, String sKey) {
        JSONObject data = httpGetAndParseData(sGet);
        if (data != null) {
            return data.optInt(sKey);
        } else {
            return -1;
        }
    }
}
