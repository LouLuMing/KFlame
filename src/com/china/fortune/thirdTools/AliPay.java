package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.china.fortune.secure.RSAAction;
import com.china.fortune.string.StringAction;

public class AliPay {
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	private String DEFAULT_PARTNER = "2088411986441381";
	private String DEFAULT_SELLER = "pajiakeji@126.com";

	//RSA 私钥
	//private String PRIVATE = "MIICWwIBAAKBgQDDQiZYd6MuUqj7u1SA4tifZmb1g+O1SbAjFTfep7UlksPni6Nr5Z1b7oH90ww7VZ4kimZh9ffDo+cyDZzQtnR1avkmW/0fFpelIl3KUvpH7GbLlH/Fv/q5Fgm7AcH/DPLXBLezGLzVd4EeulHvTCuQtXAyLGcmJ9UV8igLa849JQIDAQABAoGABduAJvs/y+3y38pY2Jz1fqKlyAuy0wdo30SNroI9bukeM13/AweaVt2qzKB+J3GEEviL5bhpPFc4YkoCQEiY6bdaGTzBKzd0XfseCmi7r++SQcHKwLtxQ0Bhb3Vmlk/5opuuK2ffjMqbFO8aggdTUf1HiPcv0xXYdzRg0kC2fIECQQD0eciDiGs2aqfcl42JhbFlvThaIkJ/KtuCLPJMgrsMX9diTfJkHs6A3LoZcNnM2iSU7YRULuRBulJiWppHWLyxAkEAzHZvcYjCYOGJ+Gy2TkLCX9VqEvlwvMapBIm6iTqdhBjIQKbPzgPEiMIkR11ruG+4nVEqfspSWUWZAJBreUMUtQJAHZAgfC9h75mkJNu6xNeC5S+lgdjEu8X991biEfh5D/0C+aM9XXgQNqr6Yhswa0IxzESQjPGCrqQOVFBqeZg0cQJAGuiHWCjIdBman9Am2fvdOuGKWT7swFtgyREbNI47RrRAPon1lUZXqivqvF2mHJrPcBzLe+5Cki8fqLxY+JeEdQJAC1EwkP784zox4aso4Y8OD9NA96a47ILCUal9Xpxg2yairz963GVx4jnr90e38pJbQK/gLVbqaikyHC5KRVc8Ag==";
	//RSA PKCS8格式私钥
	private String PKCS8 = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMNCJlh3oy5SqPu7VIDi2J9mZvWD47VJsCMVN96ntSWSw+eLo2vlnVvugf3TDDtVniSKZmH198Oj5zINnNC2dHVq+SZb/R8Wl6UiXcpS+kfsZsuUf8W/+rkWCbsBwf8M8tcEt7MYvNV3gR66Ue9MK5C1cDIsZyYn1RXyKAtrzj0lAgMBAAECgYAF24Am+z/L7fLfyljYnPV+oqXIC7LTB2jfRI2ugj1u6R4zXf8DB5pW3arMoH4ncYQS+IvluGk8VzhiSgJASJjpt1oZPMErN3Rd+x4KaLuv75JBwcrAu3FDQGFvdWaWT/mim64rZ9+MypsU7xqCB1NR/UeI9y/TFdh3NGDSQLZ8gQJBAPR5yIOIazZqp9yXjYmFsWW9OFoiQn8q24Is8kyCuwxf12JN8mQezoDcuhlw2czaJJTthFQu5EG6UmJamkdYvLECQQDMdm9xiMJg4Yn4bLZOQsJf1WoS+XC8xqkEibqJOp2EGMhAps/OA8SIwiRHXWu4b7idUSp+ylJZRZkAkGt5QxS1AkAdkCB8L2HvmaQk27rE14LlL6WB2MS7xf33VuIR+HkP/QL5oz1deBA2qvpiGzBrQjHMRJCM8YKupA5UUGp5mDRxAkAa6IdYKMh0GZqf0CbZ+9064YpZPuzAW2DJERs0jjtGtEA+ifWVRleqK+q8XaYcms9wHMt77kKSLx+ovFj4l4R1AkALUTCQ/vzjOjHhqyjhjw4P00D3prjsgsJRqX1enGDbJqKvP3rcZXHiOev3R7fykltAr+AtVupqKTIcLkpFVzwC";
	//RSA 公钥
	//private String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	
	private String sUrlNotify = null;
	private String sUrlReturn = null;
	
	public void init(String partner, String sSeller, 
			String sPKCS8, String sNotify, String sReturn) {
		DEFAULT_PARTNER = partner;
		DEFAULT_SELLER = sSeller;
		PKCS8 = sPKCS8;
		sUrlNotify = sNotify;
		sUrlReturn = sReturn;
	}
	
	private String getNewOrderInfo(String sOderNo, String sSubject, String sBody, String sMoney) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(sOderNo);
		sb.append("\"&subject=\"");
		sb.append(sSubject);
		sb.append("\"&body=\"");
		sb.append(sBody);
		sb.append("\"&total_fee=\"");
		sb.append(sMoney);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(StringAction.urlEncode(sUrlNotify));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(StringAction.urlEncode(sUrlReturn));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return sb.toString();
	}
	
	public String getPayUrl(String sOderNo, String sSubject, String sBody, String sMoney) {
		String sPayURL = getNewOrderInfo(sOderNo, sSubject, sBody, sMoney);
		String sign = RSAAction.signature(sPayURL, PKCS8, SIGN_ALGORITHMS);
		sPayURL += "&sign=\"" + StringAction.urlEncode(sign) + "\"&sign_type=\"RSA\"";
		return sPayURL;
	}
	
	public static void main(String[] args) {
		AliPay ap = new AliPay();
		String sOrder = ap.getNewOrderInfo("121", "111", "123", "1");
		Log.log(sOrder);
	}
}
