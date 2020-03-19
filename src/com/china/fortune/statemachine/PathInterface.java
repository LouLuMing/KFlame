package com.china.fortune.statemachine;

public interface PathInterface  {
	boolean onAction(Object owner);
	boolean onCondition(Object owner);
}
