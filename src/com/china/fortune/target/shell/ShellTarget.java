package com.china.fortune.target.shell;

import java.util.ArrayList;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.xml.XmlNode;

public class ShellTarget implements TargetInterface {
	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		ArrayList<XmlNode> lsCmd = cfg.getChildNodeSet();
		if (lsCmd != null) {
			for (XmlNode cmd : lsCmd) {
				String sTag = cmd.getTag();
				if (sTag != null) {
					if ("echo".compareTo(sTag) == 0) {
						Log.log(cmd.getText());
					} else if ("delete".compareTo(sTag) == 0) {
						String sParam = cmd.getAttrValue("file");
						if (sParam != null) {
							FileHelper.delete(sParam);
						}
						sParam = cmd.getAttrValue("dir");
						if (sParam != null) {
							PathUtils.delete(sParam, true);
						}
					} else if ("copy".compareTo(sTag) == 0) {
						String sParam = cmd.getAttrValue("file");
						if (sParam != null) {
							
						}
						sParam = cmd.getAttrValue("dir");
						if (sParam != null) {
							//FileHelper.copy(sSrcPath, sDesPath)
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}

}
