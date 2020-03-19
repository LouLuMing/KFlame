package com.china.fortune.restfulHttpServer;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlManager;
import com.china.fortune.database.objectList.ObjectListFromDB;
import com.china.fortune.database.objectList.ObjectListManager;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ChainServlet;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassRraverse;
import com.china.fortune.restfulHttpServer.action.*;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.restfulHttpServer.base.IPFrequentAction;

import java.util.List;

public abstract class BaseServer extends WebServer implements DataSaveInterface {
	protected MySqlManager mySqlManager = new MySqlManager();
	protected ObjectListManager objectListManager = new ObjectListManager();
	protected IPAllowAction ipAllow = new IPAllowAction();
	protected IPFrequentAction ipFrequent = new IPFrequentAction();

	abstract public void onInitTableFirst(MySqlDbAction dbObj);
	abstract public void onInitObjectListSecond();
	abstract public void onAddServletThird();

	protected boolean isIpAllowAccess(JSONObject json) {
		int ip = json.optInt("ip");
		return ipAllow.isAllowAccess(ip);
	}
	
	public void onParamError(JSONObject json, String sErrorMsg) {
		ResultJson.fillError(json, sErrorMsg);
	}

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

	public ObjectListManager getObjectListManager() {
		return objectListManager;
	}

	public ObjectListFromDB getObjectList(Class<?> c) {
		return objectListManager.get(c);
	}

	protected void scanServlet(String packagePath, String excludePath) {
		List<String> lsData = ClassRraverse.getClassName(packagePath);
		for (String clsName : lsData) {
			if (!clsName.startsWith(excludePath)) {
				addServlet(clsName);
			}
		}
	}

	protected void scanServlet(String packagePath) {
		List<String> lsData = ClassRraverse.getClassName(packagePath);
		for (String clsName : lsData) {
			addServlet(clsName);
		}
	}

	public void init(String sUrl, String sSqlUser, String sSqlPasswd, String sDBName) {
		Log.logClass(sUrl + ":" + sSqlUser + ":" + sSqlPasswd + ":" + sDBName);
		if (mySqlManager.init(sUrl, sDBName, sSqlUser, sSqlPasswd)) {
			MySqlDbAction dbObj = mySqlManager.get();
			
			onInitTableFirst(dbObj);
			onInitObjectListSecond();
			objectListManager.update(dbObj);

			addIPAllowServelt(new ShowStatisticsAction(this));
			addIPAllowServelt(new ResetStatisticsAction(this));
			addIPAllowServelt(new SaveToFileAction(this));
			addIPAllowServelt(new UpdateObjectListAction(this));
			addIPFrequentServelt(new DocAction(this));
			addServlet(new ShowHttpAction());

			InterfaceAction si = new InterfaceAction(this);
			addIPFrequentServelt(si);
			addIPFrequentServelt(new GetObjectListAction(this));
			addIPFrequentServelt(new VerifyCodeAction());

			addServlet(new AddAllowIPAction());

			onAddServletThird();

			initHitCache();
			mySqlManager.free(dbObj);
		} else {
			Log.logClass("init database is error");
		}
	}

	protected void addIPAllowServelt(ServletInterface ba) {
		ChainServlet cs = addChainServlet(ba);
		cs.addChild(ipAllow);
	}

	protected void addIPFrequentServelt(ServletInterface ba) {
		ChainServlet cs = addChainServlet(ba);
		cs.addChild(ipFrequent);
	}

}
