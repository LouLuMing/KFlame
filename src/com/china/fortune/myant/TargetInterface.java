package com.china.fortune.myant;

import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.xml.XmlNode;

public interface TargetInterface {
	boolean doAction(XmlNode cfg, ProcessAction self);
	String doCommand(String sCmd);
}
