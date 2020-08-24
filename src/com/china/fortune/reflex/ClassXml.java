package com.china.fortune.reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;
import com.china.fortune.xml.XmlNode;

public class ClassXml {
    static public void setXml(XmlNode xml, Object o, Field f) {
        try {
            Class<?> cType = f.getType();
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                if (cType == String.class) {
                    xml.addChildNode(f.getName(), (String) f.get(o));
                } else if (cType == Integer.class || cType == int.class) {
                    xml.addChildNode(f.getName(), String.valueOf(f.getInt(o)));
                } else if (cType == Long.class || cType == long.class) {
                    xml.addChildNode(f.getName(), String.valueOf(f.getLong(o)));
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static public void setField(Object o, Field f, XmlNode xml) {
        try {
            Class<?> cType = f.getType();
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                String sData = xml.getChildNodeText(f.getName());
                if (StringUtils.length(sData) > 0) {
                    if (cType == String.class) {
                        f.set(o, sData);
                    } else if (cType == Integer.class || cType == int.class) {
                        f.setInt(o, StringUtils.toInteger(sData));
                    } else if (cType == Long.class || cType == long.class) {
                        f.setLong(o, StringUtils.toLong(sData));
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static public void toObject(XmlNode xml, Object o) {
        Class<?> c = o.getClass();
        try {
            Field[] lsFields = c.getFields();
            for (Field f : lsFields) {
                setField(o, f, xml);
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }

    static public void toXml(XmlNode xml, Object o) {
        Class<?> c = o.getClass();
        try {
            Field[] lsFields = c.getFields();
            for (Field f : lsFields) {
                setXml(xml, o, f);
            }
        } catch (Exception e) {
            Log.logException(e);
        }
    }
}
