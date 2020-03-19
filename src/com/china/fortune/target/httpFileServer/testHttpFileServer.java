package com.china.fortune.target.httpFileServer;

import com.china.fortune.common.DateAction;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class testHttpFileServer {

	public static void main(String[] args) {
		int iPort = 20000;
		// String sServer = "cs.yiqi1717.com";
		// String sServer = "115.159.2.33";
		String sServer = "127.0.0.1";

		String sPostFile = String.format("http://%s:%d/image?file=hello.jar", sServer, iPort);
		HttpFileServer obj = new HttpFileServer();
		obj.setRootPath("Z:\\");
		obj.addHttpFileAction(new HttpFileAction("pdfjs", "/pdfjs"));
		//
		if (obj.start(iPort)) {
			Log.log("WebServer start");
			TimeoutAction ta = new TimeoutAction();
			ta.start();
//			Log.log("sPostFile:" + sPostFile);
//			String sResource = HttpSendAndRecv.postFile(sPostFile, "Z:\\debit.pdf");
//			Log.log("sResource:" + sResource);
			String sResource = "/image/debit.pdf";
			String sGetUrl = String.format("http://%s:%d%s", sServer, iPort, sResource);
			Log.log("sGetUrl:" + sGetUrl);
			HttpSendAndRecv.getFile(sGetUrl, "Z:\\" + DateAction.getDateTime("yyyyMMddHHmmss"));

			Log.log("Cost:" + ta.getMilliseconds());
			while (true) {
				ThreadUtils.sleep(1000);
			}
		}
	}
}
