package com.china.fortune.target.miniWeb.action;

import com.china.fortune.database.mySql.MySqlDbAction;
import com.china.fortune.http.server.HttpServerRequest;
import com.china.fortune.http.webservice.servlet.RestfulStringServlet;
import com.china.fortune.json.JSONObject;
import com.china.fortune.reflex.ClassDatabase;
import com.china.fortune.reflex.ClassJson;
import com.china.fortune.restfulHttpServer.ResultJson;
import com.china.fortune.restfulHttpServer.annotation.AsServlet;
import com.china.fortune.target.miniWeb.table.User;

@AsServlet(ipFrequent = true)
public class LoginAction extends RestfulStringServlet {
    private String[] lsKey = {"phone", "password"};

    public LoginAction() {
        ksKey.append(lsKey);
    }

    @Override
    protected void onParamMiss(JSONObject json, String sMissKey) {
    }

    @Override
    public RunStatus doWork(HttpServerRequest hReq, JSONObject json, Object objForThread, String[] lsValues) {
        MySqlDbAction dbObj = (MySqlDbAction) objForThread;
        User user = new User();
        user.phone = lsValues[0];
        ClassDatabase.selectWhere(dbObj,user, "phone");
        if (user != null) {
            if (user.checkPassword(lsValues[1])) {
                JSONObject data = ClassJson.toJSONObject(user);
                data.remove("password");
                ResultJson.fillData(json, 0, "ok", data);
            } else {
                ResultJson.fillData(json, 1, "用户密码错误", null);
            }
        } else {
            ResultJson.fillData(json, 1, "用户不存在", null);
        }
        return RunStatus.isOK;
    }
}
