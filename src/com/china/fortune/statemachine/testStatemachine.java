package com.china.fortune.statemachine;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.xml.XmlNode;

public class testStatemachine implements TargetInterface {
	public boolean doAction(XmlNode cfg , ProcessAction proc) {
		Log.log("stateMachine start");
		StateMachine obj = new StateMachine();
		
		obj.addState("1", new StateAction() {
			protected boolean onAction(Object owner) {
				Log.log("state 1");
				return true;
			}
		});
		
		obj.addState("2", new StateAction(){
			@Override
			protected boolean onAction(Object owner) {
				Log.log("state 2");
				return true;
			}
		});
		
		obj.addState("3", new StateAction(){
			@Override
			protected boolean onAction(Object owner) {
				Log.log("state 3");
				return true;
			}
		});
		
		obj.addPath("1", "2", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				Log.log("path 1 2");
				return true;
			}

			@Override
			public boolean onCondition(Object owner)  {
				Log.log("condition 1 2");
				return true;
			}
		});
		
		obj.addPath("2", "3", new PathInterface() {
			@Override
			public boolean onAction(Object owner) {
				Log.log("path 2 3");
				return true;
			}

			@Override
			public boolean onCondition(Object owner)  {
				Log.log("condition 2 3");
				return true;
			}
		});
		
		obj.setStart("1");
		obj.setStop("3");
		
		obj.doAction(obj);
		obj.clear();
		
		Log.log("stateMachine end");
		return true;
	}

	@Override
	public String doCommand(String sCmd) {
		return null;
	}
	
	static public void main(String[] args) {
		testStatemachine ts = new testStatemachine();
		ts.doAction(null, null);
	}
}
