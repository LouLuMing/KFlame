package com.china.fortune.thirdTools.WePay;

import java.util.concurrent.ThreadLocalRandom;

public class WePayTransfers {
	public String mch_appid;
	public String mchid;
	public String nonce_str;
	public String sign;
	public String partner_trade_no;
	public String openid;
	public String check_name;
	public int amount;
	public String desc;
	public String spbill_create_ip;
	
	public WePayTransfers() {
		check_name = "NO_CHECK";
		nonce_str = String.valueOf(1000000 + ThreadLocalRandom.current().nextInt(9000000));
		spbill_create_ip = "8.8.8.8";
	}

}
