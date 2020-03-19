package com.china.fortune.thirdTools.WePay;

import com.china.fortune.global.Log;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.http.httpHead.HttpPost;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.os.xml.XmlParser;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.reflex.ClassHashMap;
import com.china.fortune.reflex.ClassXml;
import com.china.fortune.secure.Digest;
import com.china.fortune.string.StringAction;
import com.china.fortune.string.StringsSort;
import com.china.fortune.xml.XmlNode;

import javax.net.ssl.KeyManagerFactory;
import java.util.HashMap;

public class WePay {
	static public String sign(Object o, String apiKey) {
		HashMap<String, String> map = ClassHashMap.toMap(o);
		map.remove("sign");
		String sToSign = StringsSort.toASC(map) + "&key=" + apiKey;
		return Digest.getMD5(sToSign).toUpperCase();
	}

	static public String sign(HashMap<String, String> map, String apiKey) {
		String sToSign = StringsSort.toASC(map) + "&key=" + apiKey;
		Log.log(sToSign);
		return Digest.getMD5(sToSign).toUpperCase();
	}

	static public void showLog(String sUrl, HttpPost hr, HttpResponse hs) {
		StringBuilder sb = new StringBuilder();
		sb.append("WePay ");
		sb.append(sUrl);
		sb.append(':');
		sb.append(hr.getBody());
		sb.append(':');
		sb.append(hs.getBody());
		Log.log(sb.toString());
	}

	static public XmlNode toPost(String sUrl, XmlNode in, KeyManagerFactory kmf) {
		XmlNode xml = null;
		HttpPost hr = new HttpPost(sUrl);
		hr.setBody(in.createXMLNoHead(), "text/xml");
		HttpClient hc = new HttpClient();
		HttpResponse hs = hc.execute(kmf, null, hr);
		showLog(sUrl, hr, hs);
		if (hs.getStatusCode() == 200) {
			xml = XmlParser.parse(hs.getBody(), "utf-8");
		}
		return xml;
	}

	static public XmlNode signAndPay(Object o, String apiKey, KeyManagerFactory kmf) {
		String sPayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		return signAndPost(sPayUrl, o, apiKey, kmf);
	}

	static public XmlNode signAndPost(String sUrl, Object o, String apiKey, KeyManagerFactory kmf) {
		String sign = sign(o, apiKey);
		ClassUtils.setValue(o, "sign", sign);
		XmlNode xmlObj = new XmlNode("xml");
		ClassXml.toXml(xmlObj, o);
		return toPost(sUrl, xmlObj, kmf);
	}

	static public boolean isSuccess(XmlNode xml) {
		String error_code = xml.getChildNodeText("return_code");
		if ("SUCCESS".compareToIgnoreCase(error_code) == 0) {
			error_code = xml.getChildNodeText("result_code");
			return "SUCCESS".compareToIgnoreCase(error_code) == 0;
		}
		return false;
	}

	static public String getErrorMessage(XmlNode xml) {
		String error_message = xml.getChildNodeText("err_code_des");
		if (StringAction.length(error_message) == 0) {
			error_message = xml.getChildNodeText("return_msg");
		}
		return error_message;
	}
}
