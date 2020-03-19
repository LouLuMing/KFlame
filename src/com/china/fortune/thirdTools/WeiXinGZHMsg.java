package com.china.fortune.thirdTools;

import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import com.china.fortune.xml.XmlNode;

public class WeiXinGZHMsg {
	static public XmlNode createBase(String ToUserName, String FromUserName, String MsgType) {
		XmlNode xmlObj = new XmlNode("xml");
		xmlObj.addChildNode("ToUserName", ToUserName);
		xmlObj.addChildNode("FromUserName", FromUserName);
		xmlObj.addChildNode("CreateTime", String.valueOf(System.currentTimeMillis() / (60 * 1000)));
		xmlObj.addChildNode("MsgType", MsgType);
		return xmlObj;
	}

	static public XmlNode sendText(String ToUserName, String FromUserName, String Content) {
		XmlNode xmlObj = createBase(ToUserName, FromUserName, "text");
		xmlObj.addChildNode("Content", Content);
		return xmlObj;
	}

	static public XmlNode createBase(XmlNode recvObj, String MsgType) {
		return createBase(recvObj.getChildNodeText("FromUserName"), recvObj.getChildNodeText("ToUserName"), MsgType);
	}

	static public XmlNode sendText(XmlNode recvObj, String Content) {
		XmlNode xmlObj = createBase(recvObj, "text");
		xmlObj.addChildNode("Content", Content);
		return xmlObj;
	}

	static public XmlNode sendImage(XmlNode recvObj, String MediaId) {
		XmlNode xmlObj = createBase(recvObj, "image");
		XmlNode Image = xmlObj.addChildNode("Image");
		Image.addChildNode("MediaId", MediaId);
		return xmlObj;
	}

	static public XmlNode transferCustomerService(XmlNode recvObj) {
		XmlNode xmlObj = createBase(recvObj, "transfer_customer_service");
		return xmlObj;
	}

	static public XmlNode sendPicture(XmlNode recvObj, String Title, String Description, String PicUrl, String Url) {
		XmlNode xmlObj = createBase(recvObj, "news");
		XmlNode Articles = xmlObj.addChildNode("Articles");
		addPicture(Articles, Title, Description, PicUrl, Url);
		xmlObj.addChildNode("ArticleCount", String.valueOf(Articles.getChildCount()));
		return xmlObj;
	}
	
	static public void addPicture(XmlNode Articles, String Title, String Description, String PicUrl, String Url) {
		XmlNode item = Articles.addChildNode("item");
		item.addChildNode("Title", XmlNode.toCDATA(Title));
		item.addChildNode("Description", XmlNode.toCDATA(Description));
		item.addChildNode("PicUrl", XmlNode.toCDATA(PicUrl));
		item.addChildNode("Url", XmlNode.toCDATA(Url));
	}

	static public XmlNode sendByResource(XmlNode recvObj, JSONArray news_item) {
		XmlNode xmlObj = createBase(recvObj, "news");
		
		XmlNode Articles = xmlObj.addChildNode("Articles");
		for (int i = 0; i < news_item.length(); i++) {
			JSONObject new_item = news_item.optJSONObject(i);
			
			addPicture(Articles, new_item.optString("title"), new_item.optString("digest"), new_item.optString("thumb_url"), new_item.optString("url"));
//			XmlNode item = Articles.addChildNode("item");
//			item.addChildNode("Title", XmlNode.toCDATA(new_item.optString("title")));
//			item.addChildNode("Description", XmlNode.toCDATA(new_item.optString("digest")));
//			item.addChildNode("PicUrl", XmlNode.toCDATA(new_item.optString("thumb_url")));
//			item.addChildNode("Url", XmlNode.toCDATA(new_item.optString("url")));
		}
		xmlObj.addChildNode("ArticleCount", String.valueOf(Articles.getChildCount()));
		return xmlObj;
	}
}
