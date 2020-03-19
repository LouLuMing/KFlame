package com.china.fortune.target.httpFileServer;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.WebServer;
import com.china.fortune.http.webservice.servlet.ServletInterface;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.restfulHttpServer.action.ResourceMapPathAction;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class FileServer extends WebServer implements TargetInterface  {
	private ResourceMapPathAction getSmallFileAction = new ResourceMapPathAction();

	@Override
	protected Object createObjectInThread() {
		return null;
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}

	protected void addResource(String sResource, String sPath) {
		getSmallFileAction.addResource(sResource, sPath);
	}

	protected void initAndStart(int iLocalPort) {
		setMaxHttpHeadLength(16 * 1024);
		setMaxHttpBodyLength(0);
		startAndBlock(iLocalPort);
	}

	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		Log.logClass("FileServer start " + iLocalPort);
		XmlNode resources = cfg.getChildNode("resources");
		if (resources != null) {
			for (int i = 0; i < resources.getChildCount(); i++) {
				XmlNode resource = resources.getChildNode(i);
				if (resource != null && "resource".equals(resource.getTag())) {
					String sResource = resource.getText();
					String sPath = resource.getAttrValue("folder");
					if (sResource != null && sPath != null) {
						addResource(sResource, sPath);
					}
					Log.log(sResource + ":" + sPath);
				}
			}
		}

		initAndStart(iLocalPort);

		Log.logClass("FileServer waitToStop");

		return true;
	}

	@Override
	protected boolean service(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		boolean rs = true;
		try {
			rs = (getSmallFileAction.doAction(hReq, hRes, objForThread) != ServletInterface.RunStatus.isClose);
		} catch (Exception e) {
			onException(hReq, hRes, objForThread);
			Log.logException(e);
		} catch (Error e) {
			onException(hReq, hRes, objForThread);
			Log.logException(e);
		}
		return rs;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}

	@Override
	protected void onException(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
		hRes.setBody(ResultJson.sJsonException, "application/json");
	}

	@Override
	protected void onMissResource(HttpServerRequest hReq, HttpResponse hRes, Object objForThread) {
	}

	//java -cp myAnt.jar com.china.fortune.target.httpFileServer.FileServer
	// http://115.159.71.91:30087/index.html
	// http://127.0.0.1:30087/proguard.xml
	static public void main(String[] args) {
		FileServer obj = new FileServer();
		obj.addResource("/visitor-wechat", "/home/yiqi/zjrc/visitor-api");
		obj.addResource("/", "/home/yiqi/zjrc/visitor-wechat/dist");
		obj.initAndStart(30087);
	}

}
