package com.china.fortune.thirdTools.WePay;

import java.util.concurrent.ThreadLocalRandom;

//java -cp myAnt.jar com.china.fortune.target.mpWeixin.entity.RedPack
public class WePayRedPack {
	public String mch_id;		// 商户号
	public String wxappid;		// 公众账号
	public String send_name;	// 商户名称
	public String re_openid;	// 用户

	public String mch_billno;	// 商户订单号
	public int total_amount;	// 付款金额 单位：分
	public int total_num;		// 红包发放总人数
	public String wishing;		// 红包祝福语
	public String act_name;		// 活动名称
	public String remark;		// 备注

	public String client_ip;	// Ip地址
	public String nonce_str;	// 随机字符串
	public String sign;			// 签名

	public WePayRedPack() {
		nonce_str = String.valueOf(1000000 + ThreadLocalRandom.current().nextInt(9000000));
		client_ip = "8.8.8.8";
	}
}
