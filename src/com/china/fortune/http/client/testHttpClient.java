package com.china.fortune.http.client;

import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpGet;
import com.china.fortune.http.httpHead.HttpPost;
import com.china.fortune.http.httpHead.HttpResponse;

public class testHttpClient {
    public static void main(String[] args) {
        HttpGet hr = new HttpGet("http://hi.zjrcinfo.com:8090/index.html");
        hr.addHeader("Host", "nsfocus.com");
        HttpClient hc = new HttpClient();
        Log.log(hr.toString());
        HttpResponse hRes = hc.execute(hr);
        Log.log(hRes.toString() + ":" + hRes.getBody());

    }
}
