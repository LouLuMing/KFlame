package com.china.fortune.restfulHttpServer.maintain;

import com.china.fortune.global.Log;
import com.china.fortune.restfulHttpServer.action.AddAllowIPAction;

public class AddAllowIP {
	public static void main(String[] args) {
		if (args.length > 0) {
            AddAllowIPAction.addAllowIP("127.0.0.1", 0, args[0]);
		} else {
            AddAllowIPAction.addAllowIP("127.0.0.1", 8700, String.valueOf(123123));
			Log.log("java -cp myAnt.jar " + AddAllowIPAction.class.getName() + " 127.0.0.1");
		}
	}

}
