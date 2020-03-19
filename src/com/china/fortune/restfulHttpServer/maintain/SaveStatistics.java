package com.china.fortune.restfulHttpServer.maintain;

import com.china.fortune.restfulHttpServer.ActionToUrl;
import com.china.fortune.restfulHttpServer.ServerAccess;
import com.china.fortune.restfulHttpServer.action.SaveToFileAction;

public class SaveStatistics {

	public static void main(String[] args) {
		ServerAccess httpConnection = new ServerAccess("127.0.0.1");
		httpConnection.showLog(true);
		httpConnection.getAndShow(ActionToUrl.toUrl(SaveToFileAction.class));
	}

}
