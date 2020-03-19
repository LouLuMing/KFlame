package com.china.fortune.reflex;

public class ClassToPacketName {
    static public String get(Class<?> cls, int before) {
        String sPacketName = cls.getName();
        for (int i = 0; i < before; i++) {
            int index = sPacketName.lastIndexOf('.');
            if (index > 0) {
                sPacketName = sPacketName.substring(0, index);
            }
        }
        return sPacketName;
    }
}
