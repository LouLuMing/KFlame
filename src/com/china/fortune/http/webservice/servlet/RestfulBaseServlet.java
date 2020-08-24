package com.china.fortune.http.webservice.servlet;

import com.china.fortune.global.Log;
import com.china.fortune.http.PairBuilder;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassUrlParam;
import com.china.fortune.string.StringUtils;

import java.lang.reflect.*;
// doCrawler
// unPack
// check
// doWork
// setBody

public abstract class RestfulBaseServlet<E> implements ServletInterface {
	static final protected String sCharset = "utf-8";

	public abstract RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, E lsValues);

	protected E newObject() {
		E o = null;
		try {
			Class<?> clazz = getTemplateClass();
			if (clazz != null) {
				o = (E) clazz.newInstance();
			}
		} catch (Exception e) {
			Log.logException(e);
		}
		return o;
	}

	private Class<?> clsTemplate = null;
	protected Class<?> getTemplateClass() {
		if (clsTemplate == null) {
			try {
				Type superClass = getClass().getGenericSuperclass();
				Type[] lsTypes = ((ParameterizedType) superClass).getActualTypeArguments();
				if (lsTypes != null && lsTypes.length > 0) {
					Type type = lsTypes[0];
					clsTemplate = getRawType(type);
				}
			} catch (Exception e) {
				Log.logException(e);
			}
		}
		return clsTemplate;
	}

	protected Class<?> getRawType(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			return (Class) rawType;
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		} else if (type instanceof TypeVariable) {
			return Object.class;
		} else if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);
		} else {
			Log.logClassError(type.getClass().getName());
			return null;
		}
	}

	protected boolean bUrlDecode = false;
	protected boolean bGZip = false;

	protected void setUrlDecode(boolean b) {
		bUrlDecode = b;
	}

	protected void setGZip(boolean b) {
		bGZip = b;
	}

	public ServletInterface getHost() {
		return this;
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

	private boolean bBodyLog = true;
	protected void setBodyLog(boolean b) {
		bBodyLog = b;
	}
	protected void setHttpBody(HttpServerRequest hReq, HttpResponse hRes, String sBody, String sType) {
		if (Log.isShow()) {
			StringBuilder sb = new StringBuilder();
			sb.append(hReq.getResource());
			sb.append(':');

			if (bBodyLog) {
				String sRequestBody = hReq.getBody(1024);
				if (sRequestBody != null) {
					sb.append(sRequestBody);
				}
			}

			sb.append(':');
			sb.append(sBody);
			Log.logClass(sb.toString());
		}

		if (bGZip) {
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

//	private FastList<RestfulBaseServlet> lsRestServlet = new FastList<RestfulBaseServlet>(0);
//	private FastList<ServletInterface> lsServlet = new FastList<ServletInterface>(0);

//	public void addChildServlet(ServletInterface servlet) {
//	    if (servlet != null) {
//	    	if (servlet instanceof RestfulBaseServlet) {
//				lsRestServlet.add((RestfulBaseServlet)servlet);
//			} else {
//				lsServlet.add(servlet);
//			}
//        }
//    }

//    public void addChildServlet(WebService ws, Class<?> cls) {
//        ServletInterface servlet = ws.getServlet(cls);
//		addChildServlet(servlet);
//    }

//    public ServletInterface getChildServlet(Class<?> cls) {
//		for (int i = 0; i < lsServlet.size(); i++) {
//			ServletInterface servlet = lsServlet.get(i);
//			if (servlet.getClass() == cls) {
//				return servlet;
//			}
//		}
//		return null;
//	}

	@Override
	public RunStatus doAction(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		JSONObject json = new JSONObject();
        RunStatus bOK;
//		if (lsServlet.size() > 0) {
//			for (int i = 0; i < lsServlet.size(); i++) {
//				ServletInterface servlet = lsServlet.get(i);
//				bOK = servlet.doAction(hReq, hRes, objForThread);
//				if (bOK != RunStatus.isOK) {
//					return bOK;
//				}
//			}
//		}
//        if (lsRestServlet.size() > 0) {
//		    for (int i = 0; i < lsRestServlet.size(); i++) {
//                RestfulBaseServlet servlet = lsRestServlet.get(i);
//                bOK = servlet.unPackAndWork(hReq, json, objForThread);
//                if (bOK != RunStatus.isOK) {
//                    servlet.setJsonToBody(hReq, hRes, json);
//                    return bOK;
//                }
//            }
//        }
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
        addUrlParam(pb);
        if (pb.size() > 0) {
			return UrlParam.together(sUrl, pb.toString());
		} else {
			return sUrl;
		}
	}

}