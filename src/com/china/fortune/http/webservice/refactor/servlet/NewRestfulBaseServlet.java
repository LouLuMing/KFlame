package com.china.fortune.http.webservice.refactor.servlet;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.PairBuilder;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.refactor.WebService;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassUrlParam;
import com.china.fortune.string.StringUtils;
import com.china.fortune.struct.FastList;

import java.lang.reflect.Field;
// doCrawler
// 	unPack
// 	check
// 	doWork
// 	setBody

public abstract class NewRestfulBaseServlet<E> implements ServletInterface {
	static final protected int iMinGZipLength = 1024 * 4;
	static final protected String sCharset = "utf-8";

	public abstract RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, E lsValues);

	abstract protected E newObject();

	protected boolean bUrlDecode = false;
	protected boolean bGZip = false;

	protected void setUrlDecode(boolean b) {
		bUrlDecode = b;
	}

	protected void setGZip(boolean b) {
		bGZip = b;
	}

	protected String urlDecodeValue(String sValue) {
		if (bUrlDecode) {
			return StringUtils.urlDecode(sValue, sCharset);
		} else {
			return sValue;
		}
	}

	protected void setValue(E obj, Field f, String sValue) {
		try {
			f.setAccessible(true);
			Class<?> cType = f.getType();
			if (cType == String.class) {
				f.set(obj, urlDecodeValue(sValue));
			} else if (cType == Integer.class || cType == int.class) {
				f.setInt(obj, StringUtils.toInteger(sValue));
			} else if (cType == Long.class || cType == long.class) {
				f.setLong(obj, StringUtils.toLong(sValue));
			}
		} catch (Exception e) {
		}
	}

	protected void onFoundParam(E obj, String sKey, String sValue) {
		Class<?> cls = obj.getClass();
		try {
			Field f = cls.getField(sKey);
			setValue(obj, f, sValue);
		} catch (Exception e) {
		}

	}

	protected E unPack(HttpServerRequest hReq) {
		E obj = newObject();
		if (obj != null) {
			String sResource = hReq.getResource();
			if (sResource != null) {
				int iStart = sResource.indexOf('?');
				while (iStart > 0) {
					iStart++;
					int iEnd = sResource.indexOf('=', iStart);
					if (iEnd > 0) {
						String sKey = sResource.substring(iStart, iEnd);
						String sValue = null;
						iEnd++;
						int iAnd = sResource.indexOf('&', iEnd);
						if (iAnd > 0) {
							if (iEnd < iAnd) {
								sValue = sResource.substring(iEnd, iAnd);
								onFoundParam(obj, sKey, sValue);
							}
							iStart = iAnd;
						} else {
							if (iEnd < sResource.length()) {
								sValue = sResource.substring(iEnd);
								onFoundParam(obj, sKey, sValue);
							}
							break;
						}
					} else {
						break;
					}
				}
			}
		}

		return obj;
	}

	protected RunStatus unPackAndWork(HttpServerRequest hReq, JSONObject json, Object objForThread) {
		RunStatus bOK = RunStatus.isError;
		E obj = unPack(hReq);
		try {
			bOK = doWork(hReq, json, objForThread, obj);
		} catch (Exception e) {
			Log.logException(e);
		}
		return bOK;
	}

	protected void setHttpBody(HttpServerRequest hReq, HttpResponse hRes, String sBody, String sType) {
		if (Log.isShow()) {
			StringBuilder sb = new StringBuilder();
			sb.append(hReq.getResource());
			sb.append(':');
			int iLen = hReq.getContentLength();
			if (iLen < 1024) {
				String sReqBody = hReq.getBody();
				if (sReqBody != null) {
					sb.append(sReqBody);
				}
			}
			sb.append(':');
			sb.append(sBody);
			Log.logClass(sb.toString());
		}

		if (bGZip && sBody.length() > iMinGZipLength) {
			byte[] bData = null;
			try {
				bData = sBody.getBytes(sCharset);
			} catch (Exception e) {
				Log.logException(e);
			}
			if (bData != null) {
				hRes.setContentType(sType, sCharset);
				hRes.setBodyGZip(bData);
			}
		} else {
			hRes.setBody(sBody, sType, sCharset);
		}
	}

	public void setJsonToBody(HttpServerRequest hReq, HttpResponse hRes, JSONObject json) {
		String sBody = json.toString();
		setHttpBody(hReq, hRes, sBody, "application/json");
	}

	private FastList<NewRestfulBaseServlet> lsChild = new FastList<NewRestfulBaseServlet>();

	public void addChildServlet(NewRestfulBaseServlet servlet) {
	    if (servlet != null) {
            lsChild.add(servlet);
        }
    }

    public void addChildServlet(WebService ws, Class<?> cls) {
        ServletInterface servlet = ws.getServlet(cls);
        if (servlet instanceof ServletInterface) {
            addChildServlet((NewRestfulBaseServlet)servlet);
        }
    }

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		JSONObject json = new JSONObject();
        RunStatus bOK;
        if (lsChild.size() > 0) {
		    for (int i = 0; i < lsChild.size(); i++) {
                NewRestfulBaseServlet servlet = lsChild.get(i);
                bOK = servlet.unPackAndWork(hReq, json, objForThread);
                if (bOK != RunStatus.isOK) {
                    servlet.setJsonToBody(hReq, hRes, json);
                    return bOK;
                }
            }
        }
        bOK = unPackAndWork(hReq, json, objForThread);
		setJsonToBody(hReq, hRes, json);
		return bOK;
	}

    public String showUrlParam(String sUrl, E obj) {
        return UrlParam.together(sUrl, ClassUrlParam.toUrl(obj));
    }

    protected void addUrlParam(PairBuilder pb) {
        E obj = newObject();
        if (obj != null) {
            pb.add(obj);
        }
    }

    public String showUrlParam(String sUrl) {
        PairBuilder pb = new PairBuilder();
        if (lsChild.size() > 0) {
            for (int i = 0; i < lsChild.size(); i++) {
                NewRestfulBaseServlet servlet = lsChild.get(i);
                servlet.addUrlParam(pb);
            }
        }
        addUrlParam(pb);
        if (pb.size() > 0) {
            return UrlParam.together(sUrl, pb.toString());
        } else {
            return sUrl;
        }
    }

}