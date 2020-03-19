package com.china.fortune.http;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.http.httpHead.HttpGet;
import com.china.fortune.http.httpHead.HttpPost;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.string.URLEncodeNotJava;

public class HttpSendAndRecv {

	public static HttpResponse doPostInner(String sUrl, String sBody, String sType) {
		HttpPost hr = new HttpPost(sUrl);
		if (sBody != null) {
			hr.setBody(sBody, sType);
		}
		HttpClient hc = new HttpClient();
		return hc.executeLoopMove(hr);
	}

	public static HttpResponse doGetInner(String sUrl) {
		HttpGet hr = new HttpGet(sUrl);
		HttpClient hc = new HttpClient();
		return hc.executeLoopMove(hr);
	}

	static public byte[] doGetBody(String sUrl) {
		byte[] bBody = null;
		HttpResponse hs = doGetInner(sUrl);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				bBody = hs.getByteBody();
			} else {
				Log.logClass(hs.toString());
			}
		}
		return bBody;
	}

	static public String doGet(String sUrl, String cCharset) {
		String sRecv = null;
		HttpResponse hs = doGetInner(sUrl);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				sRecv = hs.getBody(cCharset);
			} else {
				Log.logClass(hs.toString() + hs.getBody());
			}
		}
		return sRecv;
	}
	
	static public String doGet(String sUrl) {
		String sRecv = null;
		HttpResponse hs = doGetInner(sUrl);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				sRecv = hs.getBody();
			} else {
//				Log.logClass(hs.toString() + hs.getBody());
			}
		}
		return sRecv;
	}

	static public String doPost(String sUrl, String sBody) {
		return doPost(sUrl, sBody, "application/octet-stream");
	}

	static public String doPost(String sUrl, String sBody, String sType) {
		String sRecv = null;
		HttpResponse hs = doPostInner(sUrl, sBody, sType);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				sRecv = hs.getBody();
			} else {
				Log.logClass(hs.toString());
			}
		}
		return sRecv;
	}

	static public byte[] doPostBytes(String sUrl, String sBody, String sType) {
		byte[] sRecv = null;
		HttpResponse hs = doPostInner(sUrl, sBody, sType);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				sRecv = hs.getByteBody();
			} else {
				Log.logClass(hs.toString());
			}
		}
		return sRecv;
	}

	static public String postFile(String sUrl, String sFile) {
		String sRecv = null;
		HttpClient hc = new HttpClient();
		HttpPost hr = new HttpPost(sUrl);

		byte[] bData = FileHelper.readSmallFile(sFile);
		hr.setBody(bData);
		HttpResponse hs = hc.execute(hr);
		if (hs != null) {
			if (hs.getStatusCode() == 200) {
				sRecv = hs.getBody();
			} else {
				Log.logClass(hs.toString());
			}
		}
		return sRecv;
	}

	static public boolean getFile(String sUrl, String sFile) {
		byte[] bData = doGetBody(sUrl);
		if (bData != null) {
			return FileHelper.writeSmallFile(sFile, bData);
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String sHead = "https://dblz.zjrd.gov.cn/tempload/wjyl/";
		String sUrl = "75487_15常办函9号（省十二届人大三次会议代表审议发言汇编(省两院部分 2015).pdf";

		sUrl = URLEncodeNotJava.encode(sUrl, "utf-8");

		sUrl = "https://eviweb.tsign.cn/evi-web/static/certificate-info.html?id=C1154702973713018887&projectId=1111565128&timestamp=1597376961272&reverse=true&type=ID_CARD&number=371326199004072411&signature=b556305310159311047c18a43bed0958dcc925e6b7837e95df3c7965cc4b7201";

		HttpResponse hs = doGetInner(sUrl);
		if (hs != null) {
				Log.logClass(hs.toString() + hs.getBody());
		}

//		Log.log(sHead + sUrl);
//		getFile(sHead + sUrl, "z:/hello.pdf");
			
	}
}
