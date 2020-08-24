package com.china.fortune.statemachine;

public class LogStateMachine extends StateMachine {
	public void addState(String name, StateAction s) {
		LogStateAction lsa = new LogStateAction(s);
		lsa.setName(name);
		lsState.put(name, lsa);
	}
	
	public void addState(String name, StateMachine sm) {
		LogStateAction lsa = new LogStateAction(sm);
		lsa.setName(name);
		lsState.put(name, lsa);
	}
}
