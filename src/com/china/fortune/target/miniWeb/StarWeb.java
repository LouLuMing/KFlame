package com.china.fortune.target.miniWeb;

import com.china.fortune.reflex.ClassToPacketName;
import com.china.fortune.restfulHttpServer.WebEnterPoint;
import com.china.fortune.restfulHttpServer.config.WebProp;

public class StarWeb {
    static public void main(String[] args) {
        WebProp wc = new WebProp();
        wc.WebPort = 8000;

        wc.MySqlIP = "121.40.112.2:8306";
        wc.MySqlUser = "root";
        wc.MySqlPassword = "Hgwl12345!";
        wc.MySqlDBName = "YJT";

        wc.ScanPath = ClassToPacketName.get(StarWeb.class, 1);

        WebEnterPoint wep = new WebEnterPoint();
//        if (wep.initDatabase(wc.MySqlIP, wc.MySqlDBName, wc.MySqlUser, wc.MySqlPassword)) {
            wep.startWeb(wc.WebPort, wc.ScanPath);
//        }
    }
}
