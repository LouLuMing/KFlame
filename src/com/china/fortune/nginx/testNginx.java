package com.china.fortune.nginx;

import com.china.fortune.global.Log;
import com.china.fortune.http.HttpSendAndRecv;
import com.china.fortune.http.client.HttpThreadAction;
import com.china.fortune.thread.LoopThread;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.timecontrol.timeout.TimeoutAction;

public class testNginx {
    public static void testHttp(String sUrl) {
        TimeoutAction ta = new TimeoutAction();
        ta.start();
        int iLoop = 100;
        int iError = 0;
        for (int i = 0; i < iLoop; i++) {
            if (HttpSendAndRecv.doGet(sUrl) == null) {
                iError++;
            }
        }
        Log.log(sUrl + " " + iError + " " + ta.getMilliseconds()/iLoop);
    }

    public static void testHttpThread(String sUrl) {
        HttpThreadAction hta = new HttpThreadAction() {
            @Override
            public boolean doHttpRequest(LoopThread t) {
                String sRecv = HttpSendAndRecv.doGet(sUrl);
                return sRecv != null;
            }};
        hta.start(20);
        for (int i = 0; i < 10; i++) {
            ThreadUtils.sleep(1000);
            Log.log(sUrl + " " + hta.showStatus());
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
//        sUrl = "http://www.baidu.com";
        testHttpThread(sUrl);
//        sUrl = "http://121.40.112.2/account/checktoken?userId=1&token=111";
//        testHttpThread(sUrl);

//        Log.log(HttpSendAndRecv.doGet("https://m.baidu.com"));

    }
}
