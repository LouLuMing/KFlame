package com.china.fortune.restfulHttpServer.base;

import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.restfulHttpServer.property.InterfaceRSAKey;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.secure.RSAAction;
import com.china.fortune.socket.IPUtils;
import com.china.fortune.string.StringUtils;

public class RSACheckAction extends RestfulStringServlet {
    private String[] lsKey = { "ip", "rsa" };
    public RSACheckAction() {
        ksKey.append(lsKey);
        setUrlDecode(true);
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object dbObj, String[] lsValues) {
        String sDecrypt = RSAAction.decryptByPrivateKey(lsValues[1], InterfaceRSAKey.RSA_Data_Private);
        if (StringUtils.compareTo(sDecrypt, lsValues[0]) == 0) {
            int clientIP;
            if (lsValues[0].indexOf('.') > 0) {
                clientIP = IPUtils.Ip2Int(lsValues[0]);
            } else {
                clientIP = StringUtils.toInteger(lsValues[0]);
            }

            if (hReq.getRemoteIP() == clientIP) {
                return RunStatus.isOK;
            } else {
                ResultJson.fillData(json, 1, "IP地址不匹配", null);
            }
        } else {
            ResultJson.fillData(json, 1, "RSA验证信息错误", null);
        }
        return RunStatus.isError;
    }

    @Override
    protected void onParamMiss(JSONObject json, String sKey) {
        ResultJson.fillData(json, 1, sKey + " is null", null);
    }
}
