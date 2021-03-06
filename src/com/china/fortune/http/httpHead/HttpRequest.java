package com.china.fortune.http.httpHead;

import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;
import com.china.fortune.xml.ByteParser;

import java.nio.ByteBuffer;

public class HttpRequest extends HttpHeader {
    final private String csRootResource = "/";
    private String sMethod;
    private String sResource;

    private String sServerIP;
    private int iServerPort;

    public void setServerIP(String sIP) {
        sServerIP = sIP;
    }

    public void setServerPort(int iPort) {
        iServerPort = iPort;
    }

    public HttpRequest() {
    }

    public HttpRequest(String sMethod) {
        this.sMethod = sMethod;
        this.sResource = csRootResource;
    }

    public HttpRequest(String sMethod, String sURL) {
        this.sMethod = sMethod;
        parseURL(sURL);
    }

    public boolean parseRequest(ByteBuffer bb, int iStart) {
        int iPos = ByteBufferUtils.indexOf(bb, iStart, bb.position() +1, (byte) ' ');
        if (iPos > 0) {
            sMethod = StringUtils.newString(bb, iStart, iPos);
            iStart = iPos + 1;
            iPos = ByteBufferUtils.indexOf(bb, iStart, bb.position() +1, (byte) ' ');
            if (iPos > 0) {
                sResource = StringUtils.newString(bb, iStart, iPos);
                return true;
            }
        }
        return false;
    }

    public boolean parseRequest(byte[] bData, int iStart) {
        int iPos = ByteParser.indexOf(bData, iStart, (byte) ' ');
        if (iPos > 0) {
            sMethod = StringUtils.newString(bData, iStart, iPos);
            iStart = iPos + 1;
            iPos = ByteParser.indexOf(bData, iStart, (byte) ' ');
            if (iPos > 0) {
                sResource = StringUtils.newString(bData, iStart, iPos);
                return true;
            }
        }
        return false;
    }

    public boolean parseRequest(String sRequest) {
        String[] lsText = StringUtils.split(sRequest, ' ');
        if (lsText != null && lsText.length > 2) {
            sMethod = lsText[0];
            sResource = lsText[1];
            return true;
        }
        return false;
    }

    public String toString() {
        String sHeader = sMethod + " " + sResource + " " + csVersion + csEnter;
        return sHeader + super.toString() + csEnter;
    }

    public void setHost(String sIP, int iPort) {
        sServerIP = sIP;
        iServerPort = iPort;
    }

    public void setHostAndHeader(String sIP, int iPort) {
        sServerIP = sIP;
        iServerPort = iPort;
        if (iServerPort != 80) {
            addHeader(csHost, sServerIP + ":" + iServerPort);
        } else {
            addHeader(csHost, sServerIP);
        }
    }

    final static String csHttpHead = "http://";
    final static String csHttpsHead = "https://";
    protected boolean bHttp = true;

    public boolean isbHttp() {
        return bHttp;
    }

    public void parseURL(String sURL) {
        if (sURL != null) {
            int iPort = 80;
            String sTmp = null;
            if (sURL.startsWith(csHttpHead)) {
                sTmp = sURL.substring(csHttpHead.length());
                bHttp = true;
            } else if (sURL.startsWith(csHttpsHead)) {
                iPort = 443;
                sTmp = sURL.substring(csHttpsHead.length());
                bHttp = false;
            }
            if (sTmp != null) {
                int iPos = sTmp.indexOf('/');
                if (iPos >= 0) {
                    sResource = sTmp.substring(iPos, sTmp.length());
                    sTmp = sTmp.substring(0, iPos);
                } else {
                    sResource = csRootResource;
                }
                if (sTmp != null) {
                    String sIP = StringUtils.getBefore(sTmp, ":");
                    String sPort = StringUtils.getAfter(sTmp, ":");

                    if (sPort != null) {
                        iPort = StringUtils.toInteger(sPort);
                    }
                    setHostAndHeader(sIP, iPort);
                }
            } else {
                Log.logError(sURL);
            }
        }
    }

    public String getServerIP() {
        return sServerIP;
    }

    public int getServerPort() {
        return iServerPort;
    }

    public String getResource() {
        return sResource;
    }

    public String getResourceWithoutParam() {
        String sTag = sResource;
        int index = sResource.indexOf('?', 0);
        if (index > 0) {
            sTag = sResource.substring(0, index);
        }
        return sTag;
    }

    public String getMethod() {
        return sMethod;
    }

    public boolean parseHeader(ByteBuffer bb) {
        boolean rs = false;
        int iMethod = ByteBufferUtils.indexOf(bb, 0, bb.position()+1, HttpHeader.fbCRLF);
        if (iMethod > 0) {
            while (true) {
                int iOff = iMethod + HttpHeader.fbCRLF.length;
                iMethod = ByteBufferUtils.indexOf(bb, iOff, bb.position()+1, HttpHeader.fbCRLF);
                if (iMethod > iOff) {
                    parseHeader(bb, iOff, iMethod - 1);
                } else {
                    break;
                }
            }
            rs = true;
        }
        return rs;
    }

    public boolean parseRequestAndHeader(ByteBuffer bb) {
        boolean rs = false;
        int iMethod = ByteBufferUtils.indexOf(bb, 0, bb.position()+1, HttpHeader.fbCRLF);
        if (iMethod > 0) {
            if (parseRequest(bb, 0)) {
                while (true) {
                    int iOff = iMethod + HttpHeader.fbCRLF.length;
                    iMethod = ByteBufferUtils.indexOf(bb, iOff, bb.position()+1, HttpHeader.fbCRLF);
                    if (iMethod > iOff) {
                        parseHeader(bb, iOff, iMethod - 1);
                    } else {
                        break;
                    }
                }
                rs = true;
            }
        }
        return rs;
    }

    public boolean parseRequestAndHeader(byte[] bData) {
        boolean rs = false;
        int iMethod = ByteParser.indexOf(bData, 0, HttpHeader.fbCRLF);
        if (iMethod > 0) {
            if (parseRequest(bData, 0)) {
                while (true) {
                    int iOff = iMethod + HttpHeader.fbCRLF.length;
                    iMethod = ByteParser.indexOf(bData, iOff, HttpHeader.fbCRLF);
                    if (iMethod > iOff) {
                        parseHeader(bData, iOff, iMethod - 1);
                    } else {
                        break;
                    }
                }
                rs = true;
            }
        }
        return rs;
    }

    public boolean parseHeader(byte[] bData) {
        boolean rs = false;
        int iMethod = ByteParser.indexOf(bData, 0, HttpHeader.fbCRLF);
        if (iMethod > 0) {
            while (true) {
                int iOff = iMethod + HttpHeader.fbCRLF.length;
                iMethod = ByteParser.indexOf(bData, iOff, HttpHeader.fbCRLF);
                if (iMethod > iOff) {
                    parseHeader(bData, iOff, iMethod - 1);
                } else {
                    break;
                }
            }
            rs = true;
        }
        return rs;
    }
}
