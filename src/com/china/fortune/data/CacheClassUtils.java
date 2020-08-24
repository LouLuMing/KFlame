package com.china.fortune.data;

import com.china.fortune.easy.String2Struct;
import com.china.fortune.file.FileUtils;
import com.china.fortune.json.JSONObject;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.reflex.ClassJson;
import com.china.fortune.reflex.ClassUtils;

import java.io.File;

public class CacheClassUtils {
    static private String sPath = PathUtils.getCurrentDataPath(true) + "cache" + File.separator;

    static {
        PathUtils.create(sPath);
    }

    static public void saveClass(String sFile, Object o) {
        JSONObject json = ClassJson.toJSONObject(o);
        FileUtils.writeSmallFile(sFile, json.toString(), "utf-8");
    }

    static public boolean loadClass(String sFile, Object o) {
        String sJson = FileUtils.readSmallFile(sFile, "utf-8");
        if (sJson != null) {
            JSONObject json = new JSONObject(sJson);
            ClassJson.toObject(json, o);
            return true;
        }
        return false;
    }

    static public void saveClass(Object o, String sTag) {
        Class<?> cls = o.getClass();
        saveClass(sPath + cls.getSimpleName() + "." + sTag, o);
    }

    static public void saveClass(Object o) {
        Class<?> cls = o.getClass();
        saveClass(sPath + cls.getSimpleName(), o);
    }

    static public boolean loadClass(Object o, String sTag) {
        Class<?> cls = o.getClass();
        return loadClass(sPath + cls.getName() + "." + sTag, o);
    }

    static public boolean loadClass(Object o) {
        Class<?> cls = o.getClass();
        return loadClass(sPath + cls.getName(), o);
    }

    public static void main(String[] args) {
        String2Struct s2s = new String2Struct();
        s2s.s2 ="123123123";
        s2s.s1 = "asfasfdasf";
        saveClass(s2s);

        s2s = new String2Struct();
        loadClass(s2s);

        ClassUtils.showAllFields(s2s);
    }
}
