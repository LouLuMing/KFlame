package com.china.fortune.http.webservice.refactor.servlet;

import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.json.JSONObject;
import com.china.fortune.string.StringAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class NewRestfulClassServlet<E> extends NewRestfulBaseServlet<E> {
	private int iCheckCount = 0;
	abstract protected void onParamError(JSONObject json, String sErrorMsg);
	
	public void setCheckCount(int i) {
		iCheckCount = i;
	}

	private Field[] lsFinalFields = null;

	private Field[] getField(E obj) {
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
			Field[] lsFields = getField(obj);
			for (int i = 0; i < lsFields.length; i++) {
				Field f = lsFields[i];
				if ((f.getModifiers() & Modifier.STATIC) == 0) {
					f.setAccessible(true);
					String sKey = f.getName();
					String sValue = UrlParam.findValue(sResource, sKey);
					if (StringAction.length(sValue) > 0) {
						setValue(obj, f, sValue);
					} else if (iCheckCount > i) {
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
