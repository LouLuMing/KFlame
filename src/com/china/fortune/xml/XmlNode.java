package com.china.fortune.xml;

import java.util.ArrayList;

import com.china.fortune.global.ConstData;

public class XmlNode {
	private String sTag = null;
	private String sText = null;
	private ArrayList<AttrNode> attrNodes = null;
	private ArrayList<XmlNode> childNodes = null;
	private Object oAttach = null;
	
	public XmlNode() {
	}
	
	public XmlNode(String tag) {
		sTag = tag;
	}
	
	public XmlNode(String tag, String text) {
		sText = text;
		sTag = tag;
	}
	
	public void clear() {
		dettachAllObject();
		if (childNodes != null) {
			for (XmlNode me : childNodes) {
				me.clear();
			}	
			childNodes.clear();
		}
		if (attrNodes != null) {
			attrNodes.clear();
		}
	}
	
	public void dettachAllObject() {
		oAttach = null;
		if (childNodes != null) {
			for (XmlNode me : childNodes) {
				me.dettachAllObject();
			}
		}
	}
	
	public void setObject(Object obj) {
		oAttach = obj;
	}
	
	public Object getObject() {
		return oAttach;
	}

	public void setText(String text) {
		sText = text;
	}
	
	public String getText() {
		return sText;
	}
	
	public String getTag() {
		return sTag;
	}
	
	public void setTag(String tag) {
		sTag = tag;
	}
	
	public int getChildCount() {
		if (childNodes != null) {
			return childNodes.size();
		}
		return 0;
	}
	
	public XmlNode getChildNode(int index) {
		if (childNodes != null) {
			if (index >=0 && index < childNodes.size()) {
				return childNodes.get(index);
			}
		}
		return null;
	}
	
	public XmlNode getChildNode(String tag) {
		if (childNodes != null) {
			for (XmlNode child : childNodes) {
				if (tag.compareTo(child.getTag()) == 0) {
					return child;
				}
			}
		}
		return null;
	}
	
	public String getChildNodeText(String tag) {
		XmlNode child = getChildNode(tag);
		if (child != null) {
			return child.getText();
		}
		return null;
	}

	public String getChildNodeText(String tag, String sDef) {
		XmlNode child = getChildNode(tag);
		if (child != null) {
			return child.getText();
		}
		return sDef;
	}
	
	public ArrayList<XmlNode> getChildNodeSet() {
		return childNodes;
	}
	
	public ArrayList<XmlNode> getChildNodeSet(String tag) {
		ArrayList<XmlNode> lsXmlObj = null;
		if (tag != null) {
			lsXmlObj = new ArrayList<XmlNode>();
			if (tag != null && childNodes != null) {
				for (XmlNode child : childNodes) {
					if (tag.compareTo(child.getTag()) == 0) {
						lsXmlObj.add(child);
					}
				}
			}
		}
		return lsXmlObj;
	}
	
	public XmlNode getChildNode(String tag, int index) {
		if (childNodes != null) {
			int iCount = 0;
			for (XmlNode child : childNodes) {
				if (tag.equals(child.getTag())) {
					if (index == iCount) {
						return child;
					}
					iCount++;
				}
			}
		}
		return null;
	}
	
	public XmlNode getChildNode(String tag, String attr, String value) {
		if (childNodes != null) {
			if (attr == null || value == null) {
				return getChildNode(tag);
			}
			
			for (XmlNode child : childNodes) {
				if (tag.compareTo(child.getTag()) == 0) {
					if (value.compareTo(child.getAttrValue(attr)) == 0) {
						return child;
					}
				}
			}
		}
		return null;
	}
	
	public XmlNode addChildNode(String tag) {
		if (childNodes == null) {
			childNodes = new ArrayList<XmlNode>(); 
		}
			
		XmlNode me = new XmlNode(tag);
		childNodes.add(me);
		return me;
	}
	
	public XmlNode addChildNodeCDATA(String tag, String text) {
		if (childNodes == null) {
			childNodes = new ArrayList<XmlNode>(); 
		}
			
		XmlNode me = new XmlNode(tag, "![CDATA[" + text + "]]");
		childNodes.add(me);
		return me;
	}
	
	public XmlNode addChildNode(String tag, String text) {
		if (childNodes == null) {
			childNodes = new ArrayList<XmlNode>(); 
		}
			
		XmlNode me = new XmlNode(tag, text);
		childNodes.add(me);
		return me;
	}
	
	public void addChildNode(XmlNode child) {
		if (childNodes == null) {
			childNodes = new ArrayList<XmlNode>(); 
		}
			
		childNodes.add(child);
	}
	
	public void delChildNode(XmlNode child) {
		if (childNodes != null) {
			childNodes.remove(child);
			child.clear();
		}
	}
	
	public void detachChildNode(XmlNode child) {
		if (childNodes != null) {
			childNodes.remove(child);
		}
	}
	
	public int getAttrCount() {
		if (attrNodes != null) {
			return attrNodes.size();
		}
		return 0;
	}
	
	public String getAttrKey(int index) {
		if (attrNodes != null) {
			if (index >=0 && index < attrNodes.size()) {
				return attrNodes.get(index).getKey();
			}
		}
		return null;
	}
	
	public String getAttrValue(int index) {
		if (attrNodes != null) {
			if (index >=0 && index < attrNodes.size()) {
				return attrNodes.get(index).getValue();
			}
		}
		return null;
	}
	
	public String getAttrValue(String key) {
		if (attrNodes != null) {
			for (AttrNode attr : attrNodes) {
				if (key.compareTo(attr.getKey()) == 0) {
					return attr.getValue();
				}
			}
		}
		return null;		
	}
	
	public boolean setAttrValue(String key, String value) {
		boolean rs = false;
		if (attrNodes != null) {
			for (AttrNode attr : attrNodes) {
				if (key.compareTo(attr.getKey()) == 0) {
					attr.setValue(value);
					rs = true;
					break;
				}
			}
		}
		return rs;		
	}
	
	public ArrayList<AttrNode> getAttrNodeSet() {
		return attrNodes;
	}
	
	public ArrayList<AttrNode> getAttrNodeSet(String sTag) {
		XmlNode xmlObj = getXmlNode(sTag);
		if (xmlObj != null) {
			return xmlObj.attrNodes;
		}
		return null;
	}
	
	public AttrNode addAttrNode(String key, String value) {
		if (attrNodes == null) {
			attrNodes = new ArrayList<AttrNode>();
		}
		AttrNode me = new AttrNode(key, value);
		attrNodes.add(me);
		return me;
	}
	
	public AttrNode addAttrNode(AttrNode me) {
		if (attrNodes == null) {
			attrNodes = new ArrayList<AttrNode>();
		}
		attrNodes.add(me);
		return me;
	}
	
	public String createXMLNoHead() {
		StringBuffer sb = new StringBuffer();
		createXML(sb);
		return  sb.toString();
	}
	
	public String createXML() {
		return  createXML("1.0", ConstData.sFileCharset);
	}
	
	public String createXML(String cCharset) {
		return  createXML("1.0", cCharset);
	}
	
	public String createXML(String version, String cCharset) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"");
		sb.append(version);
		sb.append("\" encoding=\"");
		sb.append(cCharset);
		sb.append("\"?>");
		createXML(sb);
		return  sb.toString();
	}
	
	private void createXML(StringBuffer sb) {
		sb.append('<');
		sb.append(sTag);

		if (attrNodes != null) {
			for (int i = 0; i < attrNodes.size(); i++) {
				AttrNode attr = attrNodes.get(i);
				if (attr.getKey() != null && attr.getValue() != null) {
					sb.append(' ');
					sb.append(attr.getKey());
					sb.append("=\"");
					sb.append(attr.getValue());
					sb.append("\"");
				}
			}
		}
		sb.append('>');
		if (sText != null) {
			sb.append(sText);
		}
		if (childNodes != null) {
			for (int i = 0; i < childNodes.size(); i++) {
				XmlNode me = childNodes.get(i);
				me.createXML(sb);
			}
		}
		sb.append("</");
		sb.append(sTag);
		sb.append('>');
	}
	
	public XmlNode getXmlNode(String sTags) {
		XmlNode curObj = null;
		if (sTags != null) {
			String[] lsTag = sTags.split("\\.");
			if  (lsTag.length > 1) {
				if (lsTag[0].compareTo(sTag) == 0) {
					curObj = this;
					boolean rs = true;
					for (int i = 1; rs && i < lsTag.length; i++) {
						rs = false;
						if (curObj.childNodes != null) {
							for (XmlNode child : curObj.childNodes) {
								if (lsTag[i].compareTo(child.sTag) == 0) {
									curObj = child;
									rs = true;
									break;
								}
							}
						}
						else {
							break;
						}
					}
					if (!rs) {
						curObj = null;
					}
				}
			}
		} else {
			curObj = this;
		}
		return curObj;
	}
	
	// "root.child.child"
	public XmlNode createXmlNode(String sTags) {
		XmlNode curObj = null;
		if (sTags != null) {
			String[] lsTag = sTags.split("\\.");
			if  (lsTag.length > 1) {
				if (lsTag[0].compareTo(sTag) == 0) {
					curObj = this;
					for (int i = 1; i < lsTag.length; i++) {
						boolean rs = false;
						if (curObj.childNodes != null) {
							for (XmlNode child : curObj.childNodes) {
								if (lsTag[i].compareTo(child.sTag) == 0) {
									curObj = child;
									rs = true;
									break;
								}
							}
						}
						if (!rs) {
							curObj = curObj.addChildNode(lsTag[i], null);
						}
					}
				}
			}
		} else {
			curObj = this;
		}
		return curObj;
	}
	
	public String getText(String sTags) {
		XmlNode obj = getXmlNode(sTags);
		if (obj != null) {
			return obj.sText;
		}
		return null;
	}
	
	public boolean setText(String sTags, String sText) {
		XmlNode obj = getXmlNode(sTags);
		if (obj == null) {
			obj = createXmlNode(sTags);
		}
		if (obj != null) {
			obj.sText = sText;
			return true;
		}
		return false;
	}
	
	public boolean setAttrValue(String sTags, String sKey, String sValue) {
		XmlNode obj = getXmlNode(sTags);
		if (obj != null) {
			return obj.setAttrValue(sKey, sValue);
		}
		return false;
	}
	
	public String getAttrValue(String sTags, String sKey) {
		XmlNode obj = getXmlNode(sTags);
		if (obj != null) {
			return obj.getAttrValue(sKey);
		}
		return null;
	}
	
	public XmlNode clone() {
		XmlNode obj = new XmlNode(sTag, sText);
		obj.oAttach = null;
		
		if (attrNodes != null) {
			obj.attrNodes = new ArrayList<AttrNode>();
			for (int i = 0; i < attrNodes.size(); i++) {
				AttrNode attr = attrNodes.get(i);
				obj.attrNodes.add(attr.clone());
			}
		}
		
		if (childNodes != null) {
			obj.childNodes = new ArrayList<XmlNode>();
			for (int i = 0; i < childNodes.size(); i++) {
				XmlNode me = childNodes.get(i);
				obj.childNodes.add(me.clone());
			}
		}
		
		return obj;
	}
	
	public void replaceChild(XmlNode self, XmlNode other) {
		if (childNodes != null) {
			childNodes.remove(self);
			childNodes.add(other);
		}
	}
	
	public boolean haveChildNode(String sTag) {
		XmlNode child = getChildNode(sTag);
		return child != null ? true : false;
	}
	
	static public String toCDATA(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append("<![CDATA[");
		sb.append(s);
		sb.append("]]>");
		return sb.toString();
	}
}
