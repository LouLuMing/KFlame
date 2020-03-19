package com.china.fortune.os.xml;

import java.util.Stack;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.china.fortune.xml.XmlNode;


public class XmlParserHandler extends DefaultHandler {
	XmlNode mHead = null;
	Stack<XmlNode> stackObj = new Stack<XmlNode>();
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (stackObj.size() > 0) {
			XmlNode me = stackObj.peek();
			me.setText(new String(ch,start,length));
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		stackObj.clear();
		if (mHead != null) {
			mHead.clear();
		}
		mHead = null;
	}
	
	@Override
	public void endDocument() throws SAXException {
		stackObj.clear();
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (stackObj.size() > 0) {
			stackObj.pop();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			org.xml.sax.Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		XmlNode me = new XmlNode();
		me.setTag(qName);
		int iAttr = attributes.getLength();
		for(int i = 0; i < iAttr; i++) {   
        	me.addAttrNode(attributes.getQName(i), attributes.getValue(i));
        }
		
		if (stackObj.size() > 0) {
			XmlNode father = stackObj.peek();
			father.addChildNode(me);
		}
		else {
			mHead = me;
		}
		stackObj.push(me);
	}
	
	public XmlNode getRoot() {
		XmlNode xmlObj = mHead;
		mHead = null;
		return xmlObj;
	}
}
