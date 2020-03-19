package com.china.fortune.http.upload;

import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.client.HttpClient;
import com.china.fortune.socket.LineSocketAction;

import java.util.ArrayList;

public class HttpFileUpload extends HttpClient {

    @Override
    public HttpResponse sendDataAndRecvHead(LineSocketAction lSA, HttpRequest hr) {
        HttpResponse hrResponce = null;
        if (hr instanceof HttpFileRequest) {
            HttpFileRequest hrRequest = (HttpFileRequest) hr;
            String sHeader = hrRequest.toString();
            if (lSA.writeNoFlush(sHeader, ConstData.sHttpCharset)) {
                ArrayList<byte[]> lsObj = hrRequest.getBodyList();
                if (lsObj != null) {
                    for (byte[] pBody : lsObj) {
                        lSA.writeNoFlush(pBody);
                    }
                }
                lSA.flush();
                String sLine = lSA.readLine(ConstData.sHttpCharset);
                if (sLine != null) {
                    hrResponce = new HttpResponse();
                    if (hrResponce.parseResponse(sLine)) {
                        parseHeader(hrResponce, lSA);
                    }
                }
            }
        } else {
            Log.logClassError("HttpRequest is not instanceof HttpFileRequest");
        }
        return hrResponce;
    }

}
