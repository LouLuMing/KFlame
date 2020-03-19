package com.china.fortune.http.webservice.servlet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlBuilder;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.string.StringAction;

public abstract class RestfulStringServlet extends RestfulBaseServlet<String[]> {
	protected CheckKeys ksKey = new CheckKeys();
	protected CheckKeys ksUnKey = new CheckKeys();

	abstract protected void onParamMiss(JSONObject json, String sMissKey);

	@Override
	public RunStatus unPackAndWork(HttpServerRequest hReq, JSONObject json, Object objForThread) {
		RunStatus bOK = RunStatus.isError;
		String[] lsValues = unPack(hReq);
		int iNullKey = ksKey.checkNull(lsValues);
		if (iNullKey < 0) {
			try {
				bOK = doWork(hReq, json, objForThread, lsValues);
			} catch (Exception e) {
				Log.logException(e);
			}
		} else {
			onParamMiss(json, ksKey.getKey(iNullKey));
		}
		return bOK;
	}

	@Override
	protected void onFoundParam(String[] lsValues, String sKey, String sValue) {
		int i = ksKey.find(sKey);
		if (i >= 0) {
			lsValues[i] = urlDecodeValue(sValue);
		} else {
			i = ksUnKey.find(sKey);
			if (i >= 0) {
				lsValues[ksKey.size() + i] = urlDecodeValue(sValue);
			}
		}
	}

	@Override
	protected String[] newObject() {
		int iTotalKey = ksKey.size() + ksUnKey.size();
		if (iTotalKey > 0) {
			return new String[iTotalKey];
		} else {
			return null;
		}
	}

	public ServletInterface getHost() {
		return this;
	}

	protected void clone(RestfulStringServlet rs) {
		bGZip = rs.bGZip;
		bUrlDecode = rs.bUrlDecode;
		ksKey = rs.ksKey.clone();
		ksUnKey = rs.ksUnKey.clone();
	}

	protected int getInt(String[] lsValues, String sKey) {
		return StringAction.toInteger(getString(lsValues, sKey));
	}
	
	protected long getLong(String[] lsValues, String sKey) {
		return StringAction.toLong(getString(lsValues, sKey));
	}
	
	protected String getString(String[] lsValues, String sKey) {
		int i = ksKey.find(sKey);
		if (i >= 0) {
			return lsValues[i];
		} else {
			i = ksUnKey.find(sKey);
			if (i >= 0) {
				return lsValues[ksKey.size() + i];
			}
		}
		return null;
	}

	protected void setString(String[] lsValues, String sKey, String sValue) {
		int i = ksKey.find(sKey);
		if (i >= 0) {
			lsValues[i] = sValue;
		} else {
			i = ksUnKey.find(sKey);
			if (i >= 0) {
				lsValues[ksKey.size() + i] = sValue;
			}
		}
	}

	protected int countNotNull(String[] lsValues) {
		int iNotNull = 0;
		for (String s : lsValues) {
			if (s != null) {
				iNotNull++;
			}
		}
		return iNotNull;
	}

	protected void valuesToObject(String[] lsValues, Object o) {
		Class<?> cls = o.getClass();
		try {
			Field[] lsFields = cls.getFields();
			for (Field f : lsFields) {
				String sValue = getString(lsValues, f.getName());
				if (sValue != null) {
					Class<?> cType = f.getType();
					if ((f.getModifiers() & Modifier.STATIC) == 0) {
						if (cType == String.class) {
							f.set(o, sValue);
						} else if (cType == Integer.class || cType == int.class) {
							f.set(o, StringAction.toInteger(sValue));
						} else if (cType == Long.class || cType == long.class) {
							f.set(o, StringAction.toLong(sValue));
						}
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(cls.getSimpleName() + ":" + e.getMessage());
		}
	}
	
	@Override
	public String showUrlParam(String sUrl, String[] lsValues) {
		UrlBuilder pb = new UrlBuilder(sUrl);
		int iMaxValues = 0;
		if (lsValues != null) {
			iMaxValues = lsValues.length;
		}
		int iValues = 0;
		for (int i = 0; i < ksKey.size(); i++) {
			String sKey = ksKey.get(i);
			if (iValues < iMaxValues) {
				pb.add(sKey, lsValues[iValues++]);
			} else {
				pb.add(sKey, null);
			}
		}
		for (int i = 0; i < ksUnKey.size(); i++) {
			String sKey = ksUnKey.get(i);
			if (iValues < iMaxValues) {
				pb.add(sKey, lsValues[iValues++]);
			} else {
				pb.add(sKey, null);
			}
		}
		if (pb.size() > 0) {
			return pb.toString();
		} else {
			return sUrl;
		}
	}

	@Override
	public String showUrlParam(String sUrl) {
		return showUrlParam(sUrl, null);
	}

	public void valuesToJson(JSONObject json, String[] lsValues) {
		if (ksKey.lsKey != null) {
			for (String field : ksKey.lsKey) {
				json.put(field, getString(lsValues, field));
			}
		}
		if (ksUnKey.lsKey != null) {
			for (String field : ksUnKey.lsKey) {
				json.put(field, getString(lsValues, field));
			}
		}
	}

	public String classToUrl(Class<?> cls, JSONObject json) {
		UrlBuilder ub = new UrlBuilder(ActionToUrl.toUrl(cls));
		if (ksKey.lsKey != null) {
			for (String field : ksKey.lsKey) {
				ub.add(field, json.optString(field));
			}
		}
		if (ksUnKey.lsKey != null) {
			for (String field : ksUnKey.lsKey) {
				ub.add(field, json.optString(field));
			}
		}
		return ub.toString();
	}
}