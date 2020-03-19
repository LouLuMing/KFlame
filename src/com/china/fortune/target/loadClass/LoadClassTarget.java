package com.china.fortune.target.loadClass;

import java.util.ArrayList;

import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.reflex.ClassLoaderUtils;
import com.china.fortune.xml.XmlNode;

public class LoadClassTarget implements TargetInterface {
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		ArrayList<XmlNode> lsJar = cfg.getChildNodeSet("jar");
		if (lsJar != null) {
			for (XmlNode jar : lsJar) {
				String jarPath = jar.getAttrValue("file");
				if (jarPath != null) {
					ClassLoaderUtils loader = new ClassLoaderUtils();
					if (loader.loadJarFile(jarPath)) {
						ArrayList<XmlNode> lsClass = jar.getChildNodeSet("class");
						if (lsClass != null) {
							for (XmlNode name : lsClass) {
								String className = name.getText();
								loader.loadClass(className);
							}
						}
					}
					loader.close();
				}
			}
		}
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		// TODO Auto-generated method stub
		return null;
	}
}
