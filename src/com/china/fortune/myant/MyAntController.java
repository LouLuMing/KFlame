package com.china.fortune.myant;

import java.net.Socket;
import java.util.ArrayList;

import com.china.fortune.file.ReadLinesInteface;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.os.common.OsDepend;
import com.china.fortune.os.shell.RunShell;
import com.china.fortune.reflex.ClassUtils;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.socket.shortConnection.ShortConnectionServer;
import com.china.fortune.string.StringUtils;
import com.china.fortune.xml.XmlNode;

public class MyAntController extends ShortConnectionServer {
	private MyAntEngine myAntEngine = new MyAntEngine();
	final String enterString = "\r\n";
	private String sLastCmd = null;

	private boolean sendMsg(LineSocketAction lSA, String sMsg) {
		return lSA.write(sMsg + enterString, ConstData.sHttpCharset);
	}
	
	final private String csLogTypeCmd = "logtype";
	final private String csSelectCmd = "select";
	final private String csShowCmd = "show";
	final private String csStopCmd = "waitToStop";
	final private String csExitCmd = "exit";
	final private String csHelpCmd = "help";
	final private String csExecuteCmd = "run";
	final private String csShowClassLogCmd = "showclass";
	final private String csHideClassLogCmd = "hideclass";
	final private String csShutDownCmd = "shutdown";
	
	@Override
	protected void onRead(Socket sc) {
		final LineSocketAction lSA = new LineSocketAction();
		lSA.attach(sc);
		TargetInterface actionObj = null;
		String sLine = lSA.readLine(ConstData.sHttpCharset);
		String sAllowedKey = "Gato:Gundam0083";
		if (sLine != null && sLine.compareTo(sAllowedKey) == 0) {
			sendMsg(lSA, "Welcome to MyAnt System");
			do {
				sLine = lSA.readLine(ConstData.sHttpCharset);
				if (sLine != null) {
					sLine = sLine.trim();
					String sCmd = sLine;
					String sParam = null;
					int iIndex = sLine.indexOf(' ');
					if (iIndex > 0) {
						sCmd = sLine.substring(0, iIndex);
						sParam = sLine.substring(iIndex + 1, sLine.length());
					}
					if (sCmd.compareTo(csLogTypeCmd) == 0) {
						if (sParam != null) {
							setLogType(sParam);
							sendMsg(lSA, csLogTypeCmd + " " + sParam + ":OK");
						}
					} else if (sCmd.compareTo(csShowClassLogCmd) == 0) {
						if (sParam != null) {
							Class<?> cls = ClassUtils.getClass(sParam);
							if (cls != null) {
								Log.showClasss(cls);
								sendMsg(lSA, csShowClassLogCmd + " " + sParam + ":OK");
							} else {
								sendMsg(lSA, csShowClassLogCmd + " " + sParam + ":Not Found");
							}
						}
					} else if (sCmd.compareTo(csHideClassLogCmd) == 0) {
						if (sParam != null) {
							Class<?> cls = ClassUtils.getClass(sParam);
							if (cls != null) {
								Log.hideClasss(cls);
								sendMsg(lSA, csHideClassLogCmd + " " + sParam + ":OK");
							} else {
								sendMsg(lSA, csHideClassLogCmd + " " + sParam + ":Not Found");
							}
						}
					} else if (sCmd.compareTo(csSelectCmd) == 0) {
						if (sParam != null) {
							String sTarget = sParam;
							actionObj = myAntEngine.selectTarget(sTarget);
							if (actionObj != null) {
								sendMsg(lSA, csSelectCmd + " " + sTarget + ":OK");
							} else {
								sendMsg(lSA, csSelectCmd + " " + sTarget + ":Not Found");
							}
						}
					} else if (sCmd.compareTo(csStopCmd) == 0) {
						if (sParam != null) {
							String sTarget = sParam;
							if (sTarget.compareTo("all") == 0) {
								myAntEngine.stopAllTargets();
							} else {
								if (myAntEngine.stopTarget(sTarget)) {
									sendMsg(lSA, csStopCmd + " " + sTarget + ":OK");
								} else {
									sendMsg(lSA, csStopCmd + " " + sTarget  + ":Error");
								}
							}
						}
					} else if (sCmd.compareTo(csShowCmd) == 0) {
						ArrayList<String> lsNames = myAntEngine
								.getAllTargetName();
						for (String sName : lsNames) {
							sendMsg(lSA, sName);
						}
					} else if (sCmd.compareTo(csExecuteCmd) == 0) {
						if (sParam != null) {
							ReadLinesInteface rli = new ReadLinesInteface() {
								@Override
								public boolean onRead(String sLine) {
									return sendMsg(lSA, sLine);
								}
							};
							if (OsDepend.isWin()) {
								RunShell.winRun(sParam, rli);
							} else {
								RunShell.linuxShell(sParam, rli);
							}
						}
					} else if (sCmd.compareTo(csHelpCmd) == 0) {
						sendMsg(lSA, csLogTypeCmd + " null|file|console");
						sendMsg(lSA, csSelectCmd + " target");
						sendMsg(lSA, csShowCmd);
						sendMsg(lSA, csStopCmd + " all|target");
						sendMsg(lSA, csShowClassLogCmd + " com.china.fortune.myant.MyAnt");
						sendMsg(lSA, csHideClassLogCmd + " com.china.fortune.myant.MyAnt");
						sendMsg(lSA, csExitCmd);
					} else if (sCmd.compareTo(csExitCmd) == 0) {
						sendMsg(lSA, "Bye");
						break;
					} else if (sCmd.compareTo(csShutDownCmd) == 0) {
						sendMsg(lSA, csShutDownCmd + " System Start");
						myAntEngine.stop();
						sendMsg(lSA, csShutDownCmd + " System End");
						stopSelf();
						break;
					} else if (actionObj != null) {
						if (sLine.length() > 0) {
							sLastCmd = sLine;
						}
						if (sLastCmd.length() > 0) {
							String sRecv = actionObj.doCommand(sLastCmd);
							if (sRecv != null) {
								sendMsg(lSA, sRecv);
							}
						}
					}
				} else {
					break;
				}
			} while (true);
		}
		lSA.dettach();
	}

	private void setLogType(String sLogType) {
		int iLogType = Log.parseLoginType(sLogType);
		Log.init(iLogType);
	}

	public void startAndBlock(String sMyAntXml) {
		XmlNode cfgXmlObj = MyAntXml.parse(sMyAntXml);
		if (cfgXmlObj != null) {
			setLogType(cfgXmlObj.getAttrValue("logtype"));
			Log.setLog("log", "myLog");
			Log.log(sMyAntXml);
			if (myAntEngine.start(cfgXmlObj)) {
				int iPort = StringUtils.toInteger(cfgXmlObj
						.getAttrValue("iPort"));
				if (iPort > 0) {
					setTimeout(60 * 1000);
					super.startAndBlock(iPort);
				} else {
					while (!myAntEngine.isFinish()) {
						ThreadUtils.sleep(1000);
					}
					myAntEngine.stop();
				}
			}
		} else {
			Log.logError(sMyAntXml + " parse error");
		}
	}

}
