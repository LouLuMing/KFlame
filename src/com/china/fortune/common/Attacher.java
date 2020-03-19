package com.china.fortune.common;

public class Attacher<E> {
	private E attachObj;
	public void attach(E o) {
		attachObj = o;
	}
	
	public E attachment() {
		return attachObj;
	}
}
