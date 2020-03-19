package com.china.fortune.thirdTools.WePay;

import java.util.concurrent.ThreadLocalRandom;

public class WePayRefund {
    public String appid;
    public String mch_id;
    public String nonce_str;
    public String sign;
    public String out_refund_no;
    public String out_trade_no;
    public int total_fee;
    public int refund_fee;
    public String notify_url;

    public WePayRefund() {
        nonce_str = String.valueOf(1000000 + ThreadLocalRandom.current().nextInt(9000000));
    }
}
