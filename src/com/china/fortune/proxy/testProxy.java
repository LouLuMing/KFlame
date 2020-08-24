package com.china.fortune.proxy;

import com.china.fortune.global.Log;
import com.china.fortune.http.HttpUtils;
import com.china.fortune.http.client.HttpThreadAction;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;
//java -cp myAnt.jar com.china.fortune.proxy.testNginx
public class testProxy {
    public static void testHttp(String sUrl) {
        TimeoutAction ta = new TimeoutAction();
        ta.start();
        Log.log(sUrl);
        int iLoop = 1000;
        int iError = 0;
        for (int i = 0; i < iLoop; i++) {
            if (HttpUtils.get(sUrl) == null) {
                iError++;
            }
        }
        Log.log(iError + " " + ta.getMilliseconds()/iLoop);
    }

    public static void testHttpThread(String sUrl) {
        HttpThreadAction hta = new HttpThreadAction() {
            @Override
            public boolean doHttpRequest(LoopThread t) {
                String sRecv = HttpUtils.get(sUrl);
//                Log.logClass(sRecv);
                return sRecv != null && sRecv.length() > 50;
            }};
//        Log.log(sUrl);
        hta.start(20);
        for (int i = 0; i < 1000000; i++) {
            ThreadUtils.sleep(1000);
            Log.log(hta.showStatus());
        }
        hta.waitToStop();

    }

    public static void main(String[] args) {
        String sUrl = null;
//        sUrl = "http://115.159.71.91:30087/account/isregister?phone=18258448718";
//        testHttpThread(sUrl);
//        sUrl = "http://121.40.112.2:8900/account/checktoken?userId=1&token=111";
//        testHttpThread(sUrl);
//        sUrl = "http://121.40.112.2:8700/account/checktoken?userId=1&token=111";
//        testHttpThread(sUrl);
        sUrl = "http://121.40.112.2:8989/account/checktoken?userId=1&token=111";
        sUrl = "http://20.21.1.133:30082/api/a/getBeforeVisitorPage?access_token=d303d009b92d473e9b2f0a62df56e899&deviceCode=RKDEV202004081056&pageNo=0&pageSize=1000";
        sUrl = "http://20.21.1.133:8989/proxy/echo";
        sUrl = "http://20.21.1.133:8989/showhttp";
//        String sRecv = HttpUtils.get(sUrl);
//        sUrl = "http://www.baidu.com";
//        testHttpThread(sUrl);
//        sUrl = "http://121.40.112.2/account/checktoken?userId=1&token=111";
        testHttpThread(sUrl);

//        Log.log(HttpSendAndRecv.doGet("https://m.baidu.com"));

    }
}
