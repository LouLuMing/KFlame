package com.china.fortune.restfulHttpServer;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlManager;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ChainServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassRraverse;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.restfulHttpServer.action.*;
import com.china.fortune.restfulHttpServer.annotation.AsServlet;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.restfulHttpServer.base.IPFrequentAction;
import com.china.fortune.restfulHttpServer.config.WebConfig;
import com.china.fortune.xml.XmlNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

public abstract class BaseWebTarget extends WebServer implements TargetInterface,DataSaveInterface {
	protected MySqlManager mySqlManager = new MySqlManager();
	protected IPAllowAction ipAllow = new IPAllowAction();
	protected IPFrequentAction ipFrequent = new IPFrequentAction();

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

	protected void addIPAllowServelt(ServletInterface ba) {
		ChainServlet cs = addChainServlet(ba);
		cs.addChild(ipAllow);
	}

	protected void addIPFrequentServelt(ServletInterface ba) {
		ChainServlet cs = addChainServlet(ba);
		cs.addChild(ipFrequent);
	}

	protected void scanServlet(String packagePath, String excludePath) {
		List<String> lsData = ClassRraverse.getClassName(packagePath);
		for (String clsName : lsData) {
			if (excludePath == null || !clsName.startsWith(excludePath)) {
				addServlet(clsName);
			}
		}
	}

	@Override
	protected void addServlet(String clsName) {
		try {
			Class<?> cls = Class.forName(clsName);
			if (cls != null) {
				Object obj = cls.newInstance();
				if (obj instanceof ServletInterface) {
					if (cls.isAnnotationPresent(AsServlet.class)) {
						AsServlet kf = cls.getAnnotation(AsServlet.class);
						if (kf.ipFrequent() || kf.ipAllow()) {
							ChainServlet cs = addChainServlet((ServletInterface)obj);
							if (kf.ipFrequent()) {
								cs.addChild(ipFrequent);
							}
							if (kf.ipAllow()) {
								cs.addChild(ipAllow);
							}
						} else {
							addServlet((ServletInterface) obj);
						}
					} else {
						addServlet((ServletInterface) obj);
					}
				}
			}
		} catch (Error e) {
			Log.logException(e);
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	protected void scanServlet(String packagePath) {
		List<String> lsData = ClassRraverse.getClassName(packagePath);
		for (String clsName : lsData) {
			addServlet(clsName);
		}
	}

	protected HashMap<Class<?>, Object> scanBean(String packagePath) {
		HashMap<Class<?>, Object> mapObj = new HashMap<Class<?>, Object>();
		List<String> lsData = ClassRraverse.getClassName(packagePath);
		for (String clsName : lsData) {
			try {
				Class<?> cls = Class.forName(clsName);
				if (cls != null && !Modifier.isAbstract(cls.getModifiers())) {
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

	protected void injectBean(BeansFamily bf) {
		for (int i = lsServlet.size() - 1; i >= 0; i--) {
			ServletInterface self = lsServlet.get(i).getHost();
			bf.injectField(self);
		}
	}

	protected void scanServletAndInjectBean(String packagePath, HashMap<Class<?>, Object> mapObject) {
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
								} else {
									Log.logError(clsName + ":" + clsType.getName() + " is miss");
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

	@Override
	public void stop() {
		super.stop();
		mySqlManager.clear();
	}

	protected boolean startWeb(int iPort) {
		addServlet(new AddAllowIPAction());

		addIPAllowServelt(new ShowStatisticsAction(this));
		addIPAllowServelt(new ResetStatisticsAction(this));
		addIPAllowServelt(new SaveToFileAction(this));

		addIPFrequentServelt(new ShowHttpAction());
		addIPFrequentServelt(new InterfaceAction(this));
		addIPFrequentServelt(new DocAction(this));
		addIPFrequentServelt(new VerifyCodeAction());

		MySqlDbAction dbObj = mySqlManager.get();
		onAddServlet(dbObj);

		initHitCache();
//		loadStatisticsData(ClassSaveData.getSavePath(this));
		mySqlManager.free(dbObj);

		return startAndBlock(iPort);
	}

	protected boolean initDatabase(String sServer, String sUser, String sPasswd, String sDBName) {
		return mySqlManager.init(sServer, sDBName, sUser, sPasswd);
	}

	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		WebConfig wc = new WebConfig();
		ClassXml.toObject(cfg, wc);
		if (ClassUtils.checkNoNull(wc)) {
			if (initDatabase(wc.MySqlIP, wc.MySqlUser, wc.MySqlPassword, wc.MySqlDBName)) {
				return startWeb(wc.WebPort);
			}
		} else if (wc.WebPort > 0) {
            return startWeb(wc.WebPort);
        }
		return false;
	}
}
