package com.china.fortune.target.autoIncreaseId;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.database.mySql.MySqlManager;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ChainServlet;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.restfulHttpServer.base.IPAllowAction;
import com.china.fortune.target.autoIncreaseId.action.AddAutoIncreaseIdAction;
import com.china.fortune.target.autoIncreaseId.action.GetAutoIncreaseIdAction;
import com.china.fortune.target.autoIncreaseId.action.GetIsNewDayAction;
import com.china.fortune.target.autoIncreaseId.action.GetTimeoutAction;
import com.china.fortune.target.autoIncreaseId.action.GetUnicodeAction;
import com.china.fortune.target.autoIncreaseId.action.GetWXGZHAccessTokenAction;

public class AutoIncreaseIdServer extends WebServer {
	protected MySqlManager mySqlManager = new MySqlManager();

	@Override
	protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody(ResultJson.sJsonNotFoundResource, "application/json");
	}

	@Override
	protected void onException(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody(ResultJson.sJsonException, "application/json");
	}

	@Override
	protected Object createObjectInThread() {
		return mySqlManager.get();
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
		mySqlManager.free((MySqlDbAction) objForThread);
	}

	private IPAllowAction ipAllow = new IPAllowAction();
	private GetAutoIncreaseIdAction idAction = new GetAutoIncreaseIdAction();
	private GetUnicodeAction uniAction = new GetUnicodeAction();
	private GetIsNewDayAction gindAction = new GetIsNewDayAction();
	private GetTimeoutAction gtaAction = new GetTimeoutAction();
	
	public void initMySql(String sSqlUrl, String sSqlUser, String sSqlPasswd, String sDBName) {
		Log.logClass(sSqlUrl + ":" + sSqlUser + ":" + sSqlPasswd + ":" + sDBName);
		setMaxHttpHeadLength(512);
		setMaxHttpBodyLength(0);
		
		mySqlManager.init(sSqlUrl, sDBName, sSqlUser, sSqlPasswd);
		MySqlDbAction dbObj = mySqlManager.get();

		ChainServlet cs = null;
		
		cs = addChainServlet(idAction);
		cs.addChild(ipAllow);
		
		cs = addChainServlet(uniAction);
		cs.addChild(ipAllow);
		
		cs = addChainServlet(gindAction);
		cs.addChild(ipAllow);
		
		cs = addChainServlet(gtaAction);
		cs.addChild(ipAllow);
		
		cs = addChainServlet(new AddAutoIncreaseIdAction(this));
		cs.addChild(ipAllow);

		cs = addChainServlet(new GetWXGZHAccessTokenAction());
		cs.addChild(ipAllow);

		mySqlManager.free(dbObj);
	}
	
	public void addAllowServlet(String sIp) {
		Log.logClass(sIp);
		ipAllow.addAllowIP(sIp);
	}
	
	public void addUnicodeTag(String tag) {
		uniAction.addTag(tag);
		Log.logClass(tag);
	}
	
	public void addNewDayTag(String tag, int off) {
		gindAction.addTag(tag, off);
		Log.logClass(tag + ":" + off);
	}
	
	public void addTimeoutTag(String tag, long off) {
		gtaAction.addTag(tag, off);
		Log.logClass(tag + ":" + off);
	}
	
	public void addTableAndId(String table, String id) {
		MySqlDbAction dbObj = mySqlManager.get();
		idAction.addTableAndId(dbObj, table, id);
		Log.logClass(table + " " + id);
		mySqlManager.free(dbObj);
	}
}
