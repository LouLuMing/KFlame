package com.china.fortune.http.webservice.servlet;

import java.lang.reflect.*;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.string.StringUtils;

public abstract class RestfulClassServlet<E> extends RestfulBaseServlet<E> {
	protected CheckKeys ksKey = new CheckKeys();
	abstract protected void onParamError(JSONObject json, String sMissKey);

	private Field[] lsFinalFields = null;

	private Field[] getFields(E obj) {
		if (lsFinalFields == null) {
			if (obj != null) {
				Class<?> cls = obj.getClass();
				try {
					lsFinalFields = cls.getFields();
				} catch (Exception e) {
					Log.logException(e);
				}
			}
		}
		return lsFinalFields;
	}

	@Override
	public RunStatus unPackAndWork(HttpServerRequest hReq, JSONObject json, Object objForThread) {
		boolean bParamValid = true;
		String sMissKey = null;

		String sResource = hReq.getResource();
		E obj = newObject();
		if (obj != null) {
			Field[] lsFields = getFields(obj);
			for (int i = 0; i < lsFields.length; i++) {
				Field f = lsFields[i];
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					String sKey = f.getName();
//					if (ksKey.find(sKey) >= 0) {
//						String sValue = UrlParam.findValue(sResource, sKey);
//						if (StringAction.length(sValue) > 0) {
//							setValue(obj, f, sValue);
//						} else {
//							bParamValid = false;
//							sMissKey = sKey;
//							break;
//						}
//					} else if (ksUnKey.find(sKey) >= 0) {
//						String sValue = UrlParam.findValue(sResource, sKey);
//						if (StringAction.length(sValue) > 0) {
//							setValue(obj, f, sValue);
//						}
//					}

					String sValue = UrlParam.findValue(sResource, sKey);
					if (StringUtils.length(sValue) > 0) {
						setValue(obj, f, sValue);
					} else if (ksKey.find(sKey) >= 0) {
						bParamValid = false;
						sMissKey = sKey;
						break;
					}
				}
			}
		}

		RunStatus bOK = RunStatus.isError;
		if (bParamValid) {
			try {
				bOK = doWork(hReq, json, objForThread, obj);
			} catch (Exception e) {
				Log.logException(e);
			}
		} else {
			onParamError(json, sMissKey);
		}
		return bOK;
	}
}
