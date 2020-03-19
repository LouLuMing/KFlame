package com.china.fortune.thirdTools.WePay;

import java.util.concurrent.ThreadLocalRandom;

public class WePayH5Order {
	public String appid;
	public String body;
	public String mch_id;
	public String out_trade_no;
	public String time_expire;
	public String spbill_create_ip;
	public String total_fee;
	public String trade_type;
	public String nonce_str;
	public String notify_url;
	public String openid;
	public String sign_type;
	public String sign;

	public WePayH5Order() {
		sign_type = "MD5";
		nonce_str = String.valueOf(1000000 + ThreadLocalRandom.current().nextInt(9000000));
		spbill_create_ip = "127.0.0.1";
	}
}
