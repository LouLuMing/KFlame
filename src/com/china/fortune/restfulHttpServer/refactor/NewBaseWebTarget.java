package com.china.fortune.restfulHttpServer.refactor;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlManager;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.refactor.WebService;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassRraverse;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.restfulHttpServer.action.ShowHttpAction;
import com.china.fortune.restfulHttpServer.action.VerifyCodeAction;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.restfulHttpServer.base.IPFrequentAction;
import com.china.fortune.restfulHttpServer.base.RSACheckAction;
import com.china.fortune.restfulHttpServer.config.WebConfig;
import com.china.fortune.xml.XmlNode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public abstract class NewBaseWebTarget extends WebService implements TargetInterface {
	protected MySqlManager mySqlManager = new MySqlManager();

	abstract public void onAddServlet(MySqlDbAction dbObj);

	@Override
	protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody(ResultJson.sJsonNotFoundResource, "application/json");
	}

	@Override
	protected Object createObjectInThread() {
		return mySqlManager.get();
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
		mySqlManager.free((MySqlDbAction) objForThread);
	}


	@Override
	public void stop() {
		super.stop();
		mySqlManager.clear();
	}

	private HashMap<Class<?>, Object> scanBean(String packagePath) {
        HashMap<Class<?>, Object> mapObj = new HashMap<Class<?>, Object>();
        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            try {
                Class<?> cls = Class.forName(clsName);
                if (cls != null) {
                    Object obj = cls.newInstance();
                    mapObj.put(cls, obj);
                }
            } catch (Error e) {
                Log.logException(e);
            } catch (Exception e) {
                Log.logException(e);
            }
        }
        return mapObj;
    }

	protected void scanServlet(String beanPath, String packagePath) {
        HashMap<Class<?>, Object> mapObject = scanBean(beanPath);

        List<String> lsData = ClassRraverse.getClassName(packagePath);
        for (String clsName : lsData) {
            try {
                Class<?> cls = Class.forName(clsName);
                if (cls != null) {
                    Object obj = cls.newInstance();
                    if (obj instanceof ServletInterface) {
                        ServletInterface si = (ServletInterface)obj;
                        Field[] lsFields = cls.getFields();

                        for (Field f : lsFields) {
                            f.setAccessible(true);
                            Object bean = f.get(obj);
                            if (bean == null) {
                                Class<?> clsType = f.getType();
                                bean = mapObject.get(clsType);
                                if (bean != null) {
                                    f.set(obj, bean);
                                }
                            }
                        }

                        addServlet(si);
                    }
                }
            } catch (Error e) {
                Log.logException(e);
            } catch (Exception e) {
                Log.logException(e);
            }
        }
    }

    protected void addServlet(ServletInterface servlet) {
        if (servlet != null) {
//			lsTag.add(servlet.getResource());
            lsTag.add(ActionToUrl.toUrl(servlet.getClass()));
            lsServlet.add(servlet);
        }
    }

	protected boolean startServlet(int iPort) {
		addServlet(new IPAllowAction());
        addServlet(new IPFrequentAction());
        addServlet(new RSACheckAction());

        addServlet(new ShowHttpAction());
        addServlet(new VerifyCodeAction());

		MySqlDbAction dbObj = mySqlManager.get();

		onAddServlet(dbObj);

		initHitCache();

		mySqlManager.free(dbObj);

		return startAndBlock(iPort);
	}

	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		WebConfig wc = new WebConfig();
		ClassXml.toObject(cfg, wc);
		if (ClassUtils.checkNoNull(wc)) {
			if (mySqlManager.init(wc.MySqlIP, wc.MySqlDBName, wc.MySqlUser, wc.MySqlPassword)) {
				return startServlet(wc.WebPort);
			}
		} else if (wc.WebPort > 0) {
            return startServlet(wc.WebPort);
        }
		return false;
	}
}
