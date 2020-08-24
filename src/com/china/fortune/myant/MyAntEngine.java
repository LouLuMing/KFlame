package com.china.fortune.myant;

import java.util.ArrayList;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.processflow.ProcessManager;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.sync.LockHashMap;
import com.china.fortune.xml.XmlNode;

public class MyAntEngine {
	private LockHashMap<String, TargetInterface> mapRunTargets = new LockHashMap<String, TargetInterface>();

	public TargetInterface selectTarget(String sTarget) {
		return mapRunTargets.lockGet(sTarget);
	}

	public ArrayList<String> getAllTargetName() {
		mapRunTargets.lock();
		ArrayList<String> lsNames = new ArrayList<String>();
		for (String sTarg : mapRunTargets.keySet()) {
			lsNames.add(sTarg);
		}
		mapRunTargets.unlock();
		return lsNames;
	}

	public boolean stopTarget(String sTarget) {
		return psMng.setStatus(sTarget, ProcessAction.iStateCancel);
	}

	public void stopAllTargets() {
		mapRunTargets.lock();
		for (String sTarget : mapRunTargets.keySet()) {
			psMng.setStatus(sTarget, ProcessAction.iStateCancel);
		}
		mapRunTargets.unlock();
	}

	private class TargetProcessAction extends ProcessAction {
		private XmlNode targetCfg = null;

		public TargetProcessAction(XmlNode xmlObj) {
			targetCfg = xmlObj;
		}

		@Override
		protected boolean onAction() {
			boolean rs = false;
			if (targetCfg != null) {
				String sName = targetCfg.getAttrValue("name");
				if (sName != null) {
					String className = targetCfg.getAttrValue("class");
					Log.logClass(sName + " " + className +" Start");
					if (className != null) {
						TargetInterface actionObj = null;
						try {
							actionObj = (TargetInterface) ClassUtils.create(className);
						} catch (Exception e) {
							Log.logException(e);
						}
						if (actionObj != null) {
							mapRunTargets.lockPut(sName, actionObj);
							try {
								rs = actionObj.doAction(this, targetCfg);
							} catch (Exception e) {
								Log.logException(e);
							} catch (Error e) {
								Log.logException(e);
							}
							mapRunTargets.lockRemove(sName);
						}
					} else {
						Log.logClassError("Miss " + className);
					}
					Log.logClass(sName + " End");
				}
			}
			return rs;
		}
	}

	public boolean isFinish() {
		return psMng.isFinish();
	}

	public void startAndBlock(XmlNode xmlObj) {
		if (start(xmlObj)) {
			while (psMng.isFinish()) {
				ThreadUtils.sleep(1000);
			}
			stop();
		}
	}

	private XmlNode cfgXmlObj = null;

	public boolean start(XmlNode xmlObj) {
		Log.logClass("Start");
		cfgXmlObj = xmlObj;
		return createTarget(cfgXmlObj);
	}

	public void stop() {
		psMng.stop();
		psMng.clear();
		mapRunTargets.clear();
		if (cfgXmlObj != null) {
			cfgXmlObj.clear();
		}
		Log.log("MyAnt End");
	}

	private ProcessManager psMng = new ProcessManager();

	private boolean createTarget(XmlNode xmlObj) {
		boolean rs = false;
		ArrayList<XmlNode> lsXmlTarg = xmlObj.getChildNodeSet("target");
		if (lsXmlTarg != null) {
			for (XmlNode targ : lsXmlTarg) {
				String strName = targ.getAttrValue("name");
				if (strName != null) {
					psMng.addProcess(strName, new TargetProcessAction(targ));
				}
			}
			for (XmlNode targ : lsXmlTarg) {
				String sName = targ.getAttrValue("name");
				if (sName != null) {
					String sDepends = targ.getAttrValue("depends");
					if (sDepends != null) {
						String[] lsDepends = sDepends.split(",");
						for (String sDepend : lsDepends) {
							String sDependTarg = sDepend.trim();
							if (sDependTarg.length() > 0) {
								if (MyAntXml.findTarget(lsXmlTarg, sDependTarg) != null) {
									psMng.addPath(sDependTarg, sName);
								} else {
									Log.logClass("depends:" + sDependTarg);
								}
							}
						}
					}
				}
			}

			String sStarts = xmlObj.getAttrValue("start");
			if (sStarts != null) {
				String[] lsStarts = sStarts.split(",");
				for (String sStart : lsStarts) {
					String sStartTarg = sStart.trim();
					if (sStartTarg.length() > 0) {
						psMng.setStartProcess(sStartTarg);
					}
				}
			}

			String sStops = xmlObj.getAttrValue("waitToStop");
			if (sStops != null) {
				String[] lsStops = sStops.split(",");
				for (String sStop : lsStops) {
					String sStopTarg = sStop.trim();
					if (sStopTarg.length() > 0) {
						if (MyAntXml.findTarget(lsXmlTarg, sStopTarg) != null) {
							psMng.setStopProcess(sStopTarg);
						} else {
							Log.logClass("waitToStop:" + sStopTarg);
						}
					}
				}
			}
			Log.logClass("Targerts Start");
			rs = psMng.start();
		}
		return rs;
	}
}
