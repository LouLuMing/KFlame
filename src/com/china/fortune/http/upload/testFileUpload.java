package com.china.fortune.http.upload;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class testFileUpload {

	public static void main(String[] args) {
		String sURL = "https://www.huigu666.com/share/sharepic";
		String sFile = "F:\\Picture\\05809_943.jpg";
		HttpFileUpload hc = new HttpFileUpload();
		HttpFileRequest hf = new HttpFileRequest(sURL);
		
		hf.addBlock("file", FileHelper.readSmallFile(sFile));
		hf.addBlock("userId", "1");
		hf.addBlock("token", "10086");
		hf.addBlock("remark", "123456");
		hf.addEndLine();
		Log.log(hf.toString());

		TimeoutAction ta = new TimeoutAction();
		ta.start();
		HttpResponse hs = hc.execute(hf);
		if (hs != null) {
			Log.log(hs.toString());
			Log.log(hs.getBody());
			if ("200".equals(hs.getStatusCode())) {
			}
		}
		Log.log(ta.getMilliseconds() + "");
	}

}
