package com.china.fortune.thirdTools;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.china.fortune.common.DateAction;
import com.china.fortune.global.Log;
import com.china.fortune.json.JSONObject;

public class AliWapPay {
	private String CHARSET = "utf-8";
	private String sReturnUrl = null;
	private String sNotifyUrl = null;
	
	private AlipayClient alipayClient = null; 
	public AliWapPay(String APP_ID, String APP_PRIVATE_KEY, String ALIPAY_PUBLIC_KEY, String sRUrl, String sNUrl) {
		alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID,
				APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
		sReturnUrl = sRUrl;
		sNotifyUrl = sNUrl;
	}
	
	public String toPayUrl(String out_trade_no, String total_amount, String subject) {
		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(sReturnUrl);
		alipayRequest.setNotifyUrl(sNotifyUrl);// 在公共参数中设置回跳和通知地址
		JSONObject json = new JSONObject();
		json.put("out_trade_no", out_trade_no);
		json.put("total_amount", total_amount);
		json.put("subject", subject);
		json.put("product_code", "QUICK_WAP_PAY");
		alipayRequest.setBizContent(json.toString());// 填充业务参数
		String sPayUrl = null;
		try {
			sPayUrl = alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单
		} catch (AlipayApiException e) {
			Log.logError(e.getErrMsg());
		}
		return sPayUrl;
	}

	public static void main(String[] args) {
		String sAppId = "2017082308339281";
		String sPublic = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhHcrlm1AAw9HT9y1SCyxQrQFXeZlywpqUsguKPZhosuRcL8qc/ro41lkGd5XW9m5+3MenVeZjzRCB/L8TjkQqyAbzP2s3HuZUNtScfW64AoaL3y/S6vKMkdZhw1tPBznajeF26MxptCqYy5nGsfbAlpMePFxn7HAlIdGzm2dM+7nbKtId8GXGNUHUwlaVxmZwVbopD+TQK7cShYZHm2dy+hIv1rEH662+jxVrlUeB/73TsVNQs2aHqzcSbMdxL6BZaBLPUWeoM9aKtzPY1LGroz01pcPk5PD/Fn+NKv7dgaPSGaWTabiP5mfkrrNw4jD00cY0NFN4/qM5NR7ArFbAwIDAQAB";
		String sPrivate = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEdyuWbUADD0dP3LVILLFCtAVd5mXLCmpSyC4o9mGiy5Fwvypz+ujjWWQZ3ldb2bn7cx6dV5mPNEIH8vxOORCrIBvM/azce5lQ21Jx9brgChovfL9Lq8oyR1mHDW08HOdqN4XbozGm0KpjLmcax9sCWkx48XGfscCUh0bObZ0z7udsq0h3wZcY1QdTCVpXGZnBVuikP5NArtxKFhkebZ3L6Ei/WsQfrrb6PFWuVR4H/vdOxU1CzZoerNxJsx3EvoFloEs9RZ6gz1oq3M9jUsaujPTWlw+Tk8P8Wf40q/t2Bo9IZpZNpuI/mZ+Sus3DiMPTRxjQ0U3j+ozk1HsCsVsDAgMBAAECggEAFMKUezPOB2y6kWKWqqQOd9S8zpWQOHz+YtDqoxLSBHCA77mWDq0xGIhm7sLwz/1Ub3sAPkp/T3kyLArBzgSVfyw+Dydmkvtv8MMerauESR5sMisL0/EtdCnIdyj8iKJDBluDSAPQgUNMoNSh5gmSYlECkSXN1+hEtUJqL2nwbDX1f++PXMuz9QYfz13fTThifyqFAFOTJBNOUBKz2N2gKEJTEJdQIl0BGf+G4VS8VFOS4NyTJQy2DW4fFIwQZz1qwzZpsfnrgqvzDJDju1WZKc37ixpzkjMt3WgjjNHeHKNp+DL8rCT0up+qinAOKeIbIzapDCcC6oX8MS7LCjnTEQKBgQD5KKHGkB21/G1Wc6UV1BFqU74IlOH7LnmJ6DAYC0HfzXCMAynQHGLhkOtGPUcmD2CcaozbL0gAkCzVZJ+SGyLfDJMX4FhN9uOgZ7Z/dR1IWPEaUjZ/vix5tL9neE1WmZvSqxZRd7JefgVQTiYBOrZmp01jDHN0S6Lc2Pqg2rbmOwKBgQCIGkl7N1jjNiAJd5a/gx1UDa/mvl8T7g56zPLxKKtBbIwkcsEMfzFTx12//Fet0bBYcCK2FGw5Xop7hmjJ6p2aZ/ocWRLDucLn/AdsXSPPQR4AOc2XBUPLxeV89wG6G+lc/RmDSxwZFu+hCvDa8T+1GPzrNb80G5KdzB6J5g5p2QKBgQCPgLl8VbDnChYnPsAMTHIjXUwMQUPOmbvik9pvXrx4HCkAWyoP9Ol2pTL853o9D4pDTJMIArljqaZI2YWWbw6sjN+DVvFFJjeAQXu4+Uj+TEvBLyy4cl3HUzUScRWKFTrMIUHfdiNDjS2eTQRSnPBFO4K0040DpDSPImFnl9DT1QKBgCBpWixsrs9VGWYHw6+R0s8sf0JM/alqXKpj2ksR606mQe2AIncOZznTVYEd3+d18y/sbGe+D1POLt5h0Nyc8bFOp6JOmAhiWYcTXtUwjRGTKUjWex5G07FYTGFpFVEzymwpngTFQ0i6TJCmYVtSbmB7NefxoboQAudvL6qZTb9pAoGATSd6qmUC4JaGm9tlSd8Mvb37yT44XIAiQvJb32fKq1FV6ytmIfJ32m5ptCEH92z7vzO8MqmkkEaihSxYVZoOIVqHjSDwRW2vNkxqc4CQ3/2F094Ge+KQsbKqV+ckDsLveyHkH4joE0m4t6426ma1dYP9oGFq+XtBnwkOvv9FOdE=";
		String sNotifyUrl = "http://121.40.112.2:8500/showhttp";
		AliWapPay awp = new AliWapPay(sAppId, sPrivate, sPublic, null, sNotifyUrl);
		Log.log(awp.toPayUrl("ali" + DateAction.getDateTime("yyyyMMddHHmmss"), "0.01", "支付宝测试"));
		
	}
}
