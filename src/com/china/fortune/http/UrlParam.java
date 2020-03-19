package com.china.fortune.http;

import java.util.ArrayList;
import java.util.HashMap;

import com.china.fortune.global.Log;
import com.china.fortune.string.StringAction;

public class UrlParam {
	static public int calSigner(String sResource) {
		int index = sResource.indexOf('?');
		String sTag = sResource;
		if (index > 0) {
			sTag = sResource.substring(0, index);
		}
		
		int iSigner = StringAction.calSigner(sTag);
		return iSigner;
	}
	
	static public String findValue(String sResource, String sKey) {
		String sValue = null;
		int iStart = sResource.indexOf('?');
		if (iStart < 0) {
			iStart = 0;
		} else {
			iStart++;
		}
		while (iStart >= 0) {
			int iEnd = sResource.indexOf('=', iStart);
			if (iEnd > 0) {
				if (sKey.length() == (iEnd - iStart)) {
					if (StringAction.compareTo(sResource, iStart, sKey)) {
						iEnd++;
						int iAnd = sResource.indexOf('&', iEnd);
						if (iAnd > 0) {
							sValue = sResource.substring(iEnd, iAnd);
						} else {
							sValue = sResource.substring(iEnd);
						}
						break;
					}
				} else {
					iEnd++;
				}
				iStart = sResource.indexOf('&', iEnd);
				if (iStart >= 0) {
					iStart++;
				}
			} else {
				break;
			}
		}
		return sValue;
	}

	static public HashMap<String, String> findValuesLowerCase(String sResource) {
		HashMap<String, String> mapParams = new HashMap<String, String>();
		int iStart = sResource.indexOf('?');
		if (iStart < 0) {
			iStart = 0;
		} else {
			iStart++;
		}
		while (iStart >= 0) {
			int iEnd = sResource.indexOf('=', iStart);
			if (iEnd > 0) {
				String sKey = sResource.substring(iStart, iEnd).toLowerCase();
				String sValue = null;
				iEnd++;
				int iAnd = sResource.indexOf('&', iEnd);
				if (iAnd > 0) {
					sValue = sResource.substring(iEnd, iAnd);
					mapParams.put(sKey, sValue);
					iStart = iAnd + 1;
				} else {
					sValue = sResource.substring(iEnd);
					mapParams.put(sKey, sValue);
					break;
				}
			} else {
				break;
			}
		}
		return mapParams;
	}

	static public HashMap<String, String> findValues(String sResource) {
		HashMap<String, String> mapParams = new HashMap<String, String>();
		int iStart = sResource.indexOf('?');
		if (iStart < 0) {
			iStart = 0;
		} else {
			iStart++;
		}
		while (iStart >= 0) {
			int iEnd = sResource.indexOf('=', iStart);
			if (iEnd > 0) {
				String sKey = sResource.substring(iStart, iEnd);
				String sValue = null;
				iEnd++;
				int iAnd = sResource.indexOf('&', iEnd);
				if (iAnd > 0) {
					sValue = sResource.substring(iEnd, iAnd);
					mapParams.put(sKey, sValue);
					iStart = iAnd + 1;
				} else {
					sValue = sResource.substring(iEnd);
					mapParams.put(sKey, sValue);
					break;
				}
			} else {
				break;
			}
		}
		return mapParams;
	}

	static public ArrayList<String> findKeys(String sResource) {
		ArrayList<String> lsKeys = new ArrayList<String>();
		int iStart = sResource.indexOf('?');
		if (iStart < 0) {
			iStart = 0;
		} else {
			iStart++;
		}
		while (iStart >= 0) {
			int iEnd = sResource.indexOf('=', iStart);
			if (iEnd > 0) {
				String sKey = sResource.substring(iStart, iEnd);
				iEnd++;
				int iAnd = sResource.indexOf('&', iEnd);
				if (iAnd > 0) {
					lsKeys.add(sKey);
					iStart = iAnd + 1;
				} else {
					lsKeys.add(sKey);
					break;
				}
			} else {
				break;
			}
		}
		return lsKeys;
	}
	
//	static public String getResource(String sResource) {
//		String sTag = sResource;
//		int index = sResource.indexOf('?');
//		if (index > 0) {
//			sTag = sResource.substring(0, index);
//		}
//		return sTag;
//	}
	
	static public String getResource(String sResource) {
		String sTag = sResource;
		int start = 0;
		if (sResource.startsWith("http")) {
			start = sResource.indexOf('/', 9);
		}
		int index = sResource.indexOf('?', start);
		if (index > 0) {
			sTag = sResource.substring(start, index);
		}
		return sTag;
	}
	
	static public String getUrlLastPart(String sResource) {
		String sTag = sResource;
		int index = sResource.lastIndexOf('/');
		if (index >= 0) {
			int end = sResource.indexOf('?');
			if (end > 0) {
				sTag = sResource.substring(index+1, end);
			} else {
				sTag = sResource.substring(index+1, sResource.length());
			}
		}
		return sTag;
	}

	static public String getUrlParent(String sResource) {
		String sTag = sResource;
		int index = sResource.lastIndexOf('/');
		if (index >= 0) {
			sTag = sResource.substring(0, index);
		}
		return sTag;
	}
	
	static public String getFirstResource(String sResource) {
		String sTag = null;
		int start = 1;
		if (sResource.startsWith("http")) {
			start = sResource.indexOf('/', 9) + 1;
		}
		int index = sResource.indexOf('/', start);
		if (index > 1) {
			sTag = sResource.substring(start, index);
		}
		return sTag;
	}

	static public String getUrlFirstPart(String sResource) {
		String sTag = null;
		int index = sResource.indexOf('/', 1);
		if (index > 1) {
			sTag = sResource.substring(0, index);
		}
		return sTag;
	}
	
	static public String together(String sUrl, String sParam) {
		if (sParam != null) {
			return sUrl + "?" + sParam;
		} else {
			return sUrl;
		}
	}
	
	public static void main(String[] args) {
		String sUrl = "http://121.40.112.2:8700/pdf/getextendpdf.pdf?userId=1&token=1764232472&iouId=20180626031605300";
		Log.log(getResource(sUrl));
		Log.log(getUrlLastPart(sUrl));
		Log.log(getUrlParent(sUrl));
//		sUrl = "/wx/MP_adfadf.txt";
		Log.log(getFirstResource(sUrl));
		Log.log(findValue(sUrl, "iouId"));
	}
}
