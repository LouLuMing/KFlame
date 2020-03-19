package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class QinNiuUpload {
    private String ACCESS_KEY = "9dip2udk3kxKNII7IX9mWes3jbxTRf9coQpkAqRn";
    private String SECRET_KEY = "hJuCjXH0Vb1e_gr6MHNYFONMVZKF7ea6YWNYuf4D";
    private String defBucketName = "sharephoto";

    private Auth auth = null;
    private UploadManager uploadManager = null;

    public QinNiuUpload(String acc, String sec, String bucket) {
        ACCESS_KEY = acc;
        SECRET_KEY = sec;
        defBucketName = bucket;

        auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        Configuration cfg= new Configuration(Zone.zone0());
        uploadManager = new UploadManager(cfg);
    }

    public boolean doAction(byte[] bData, String sKey) {
        String sQiNiuToken = auth.uploadToken(defBucketName);
        try {
            Response res = uploadManager.put(bData, sKey, sQiNiuToken);
            Log.log("QinNiuUpload " + res.toString());
        } catch (Exception e) {
            Log.logException(e);
        }
        return true;
    }
}
