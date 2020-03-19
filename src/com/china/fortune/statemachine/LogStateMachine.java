package com.china.fortune.statemachine;

public class LogStateMachine extends StateMachine {
	public void addState(String name, StateAction s) {
		LogStateAction lsa = new LogStateAction(s);
		lsa.setName(name);
		lsState.put(name, lsa);
	}
	
	public void addState(String name, final StateMachine sm) {
		StateAction sa = new StateAction(){
			@Override
			protected boolean onAction(Object owner) {
				return sm.doAction(owner);
			}
		};
		LogStateAction lsa = new LogStateAction(sa);
		lsa.setName(name);
		lsState.put(name, lsa);
	}
}
