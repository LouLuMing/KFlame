package com.china.fortune.processflow;

import com.china.fortune.global.Log;
import com.china.fortune.thread.ThreadUtils;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.xml.XmlNode;

public class TestProcessflow implements TargetInterface {
	private class logProcessAction extends ProcessAction {
		private int iNode = 0;
		public logProcessAction(int i) {
			iNode = i;
		}
		
		@Override
		protected boolean onAction() {
			Log.log("process " + iNode);
			return true;
		}
	}
	
	public boolean doAction(ProcessAction proc, XmlNode cfg) {
		Log.log("processManager start");
		
		ProcessManager obj = new ProcessManager();
		obj.addProcess("1", new logProcessAction(1));
		obj.addProcess("2", new logProcessAction(2));
		obj.addProcess("3", new logProcessAction(3));
		obj.addProcess("4", new logProcessAction(4));
		obj.addProcess("5", new logProcessAction(5));
		obj.addProcess("6", new logProcessAction(6));
		obj.addProcess("7", new logProcessAction(7));
		obj.addProcess("8", new logProcessAction(8));
		
		obj.addPath("1", "2");
		obj.addPath("1", "3");
		obj.addPath("1", "6");
		obj.addPath("2", "5");
		obj.addPath("3", "4");
		obj.addPath("4", "5");
		obj.addPath("5", "6");
		obj.addPath("7", "5");

		obj.addPath("6", "8");
		
		obj.setStartProcess("1");
		obj.setStartProcess("7");
		
//		obj.setEndProcess("6");
//		obj.setStopProcessAuto();
		
		obj.start();
		while (!obj.isFinish()) {
			ThreadUtils.sleep(50);
		}
		if (obj.isError()) {
			Log.log("processManager error");
		}
		
		Log.log("processManager end");
		
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}
	
	public static void main(String[] args) {
		TestProcessflow obj = new TestProcessflow();
		obj.doAction(null, null);
	}
}
