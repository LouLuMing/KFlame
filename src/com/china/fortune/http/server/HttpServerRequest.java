package com.china.fortune.http.server;

import com.china.fortune.common.ByteAction;
import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.easy.Int2Struct;
import com.china.fortune.file.FileHelper;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.socket.IPHelper;
import com.china.fortune.socket.SocketChannelHelper;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.ByteParser;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class HttpServerRequest extends HttpRequest {
    static final protected int iDefDataLength = 4 * 1024;
    static final protected int iLimitDataLength = 1024 * 1024;

    protected ByteBuffer bbData = ByteBuffer.allocateDirect(iDefDataLength);
    public int iHeadLength = 0;
    public int iDataLength = 0;
    public boolean notChuncked = true;
    public byte[] bRemoteAddr = null;
    public long lActiveTicket = System.currentTimeMillis();

    public void setRemoteAddr(String sIp) {
        bRemoteAddr = IPHelper.Ip2Bytes(sIp);
    }

    public String getRmoteStringIP() {
        return IPHelper.bytes2String(bRemoteAddr);
    }

    public int getRemoteIP() {
        return IPHelper.bytes2Int(bRemoteAddr);
    }

    public boolean remoteIPEqualTo(int ip) {
        return IPHelper.bytes2Int(bRemoteAddr) == ip;
    }

    public void logByteBuffer() {
        if (bbData.position() > 0) {
            Log.log(ByteBufferUtils.toHexString(bbData, 0, bbData.position()));
//            Log.log(new String(bbData.array(), 0, bbData.position()));
        }
    }

    public void reset() {
        super.clear();
        bbData.clear();
        iHeadLength = 0;
        iDataLength = 0;
        notChuncked = true;
    }

    public void clear() {
        super.clear();
        if (bbData.capacity() > iLimitDataLength) {
            bbData = ByteBuffer.allocateDirect(iDefDataLength);
        } else {
            bbData.clear();
        }
        lActiveTicket = System.currentTimeMillis();
        iHeadLength = 0;
        iDataLength = 0;
        notChuncked = true;
    }

    public String getBody(int iMax) {
        String sBody = null;
        if (iDataLength > 0) {
            String sCharset = getCharset();
            if (sCharset == null) {
                sCharset = ConstData.sHttpCharset;
            }
            if (iMax > iDataLength) {
                iMax = iDataLength;
            }
            sBody = StringAction.newString(bbData, iHeadLength, iMax, sCharset);
        }
        return sBody;
    }

    @Override
    public String getBody() {
        String sBody = null;
        if (iDataLength > 0) {
            String sCharset = getCharset();
            if (sCharset == null) {
                sCharset = ConstData.sHttpCharset;
            }
            sBody = StringAction.newString(bbData, iHeadLength, iDataLength, sCharset);
        }
        return sBody;
    }

    @Override
    public int getContentLength() {
        return iDataLength;
    }

    @Override
    public byte[] getByteBody() {
        byte[] bBody = null;
        if (iDataLength > 0) {
            bBody = ByteBufferUtils.toByte(bbData, iHeadLength, iHeadLength + iDataLength);
//            bBody = new byte[iDataLength];
//            if (iDataLength > 0) {
//                System.arraycopy(bbData.array(), iHeadLength, bBody, 0, iDataLength);
//            }
        }
        return bBody;
    }

    private int findFormData(ByteBuffer bb, int iOff, byte[] bTag) {
        int i = ByteBufferUtils.indexOf(bb, iOff, bTag);
        if (i > 0) {
            i = ByteBufferUtils.indexOf(bb, i, fbCRLFCRLF);
            if (i > 0) {
                return i + 4;
            }
        }
        return -1;
    }

    private int findFormData(byte[] bBody, int iOff, byte[] bTag) {
        int i = ByteParser.indexOf(bBody, iOff, bTag);
        if (i > 0) {
            i = ByteParser.indexOf(bBody, i, fbCRLFCRLF);
            if (i > 0) {
                return i + 4;
            }
        }
        return -1;
    }

    static final private byte[] sNameDot = "name=\"".getBytes();
    static final private byte bDot = '"';

    private String findFormDataName(ByteBuffer bb, int iStart) {
        int i = ByteBufferUtils.indexOf(bb, iStart, sNameDot);
        if (i > 0) {
            i += sNameDot.length;
            int j = ByteBufferUtils.indexOf(bb, i, bDot);
            if (j > i) {
                return new String(bBody, i, j - i);
            }
        }
        return null;
    }

    private String findFormDataName(byte[] bBody, int iStart) {
        int i = ByteParser.indexOf(bBody, iStart, sNameDot);
        if (i > 0) {
            i += sNameDot.length;
            int j = ByteParser.indexOf(bBody, i, bDot);
            if (j > i) {
                return new String(bBody, i, j - i);
            }
        }
        return null;
    }

    public HashMap<String, Int2Struct> parseFormData() {
        String sRType = getHeaderValue(csContentType);
        if (sRType != null) {
            String sTag = StringAction.getAfter(sRType, "boundary=");
            if (StringAction.length(sTag) > 0) {
                HashMap<String, Int2Struct> mapIndex = new HashMap<String, Int2Struct>();
                byte[] bTag = sTag.getBytes();
                int iOff = iHeadLength;
                while (true) {
                    int iStart = findFormData(bbData, iOff, bTag);
                    if (iStart > 0) {
                        String sName = findFormDataName(bbData, iOff);
                        iOff = ByteBufferUtils.indexOf(bbData, iStart, bTag);
                        if (iOff > 0) {
                            int iEnd = ByteBufferUtils.lastIndexOf(bbData, iOff, fbCRLF);
                            if (iEnd >= iStart) {
                                mapIndex.put(sName, new Int2Struct(iStart, iEnd));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                return mapIndex;
            }
        }
        return null;
    }

    public String formDataToString(HashMap<String, Int2Struct> formData, String key) {
        Int2Struct i2s = formData.get(key);
        if (i2s != null && i2s.i2 > 0) {
            return StringAction.newString(bbData, i2s.i1, i2s.i2);
        } else {
            return null;
        }
    }

    public byte[] formDataToBytes(HashMap<String, Int2Struct> formData, String key) {
        Int2Struct i2s = formData.get(key);
        if (i2s != null && i2s.i2 > 0) {
//            byte[] bBody = new byte[i2s.i2];
//            System.arraycopy(bbData.array(), i2s.i1, bBody, 0, bBody.length);

            byte[] bBody = ByteBufferUtils.toByte(bbData, i2s.i1, i2s.i2);
            return bBody;
        } else {
            return null;
        }
    }

    private int findChunkLen() {
        int iChuckHead = iHeadLength + iDataLength;
        int i = ByteBufferUtils.indexOf(bbData, iChuckHead, HttpHeader.fbCRLF);
        if (i > 0) {
            int iChunkLen = hexToInt(bbData, iChuckHead, bbData.position());
            if (iChunkLen > 0) {
                iDataLength += iChunkLen + (i - iChuckHead + 2);
            } else {
                iDataLength += 5;
//                iDataLength = bbData.position() - iHeadLength;
            }
            return iChunkLen;
        }
        return -1;
    }

    private boolean largeBuffer() {
        int left = bbData.remaining();
        int iTotalLength = iHeadLength + iDataLength + 16;
        if (iTotalLength > bbData.capacity()) {
            int pos = bbData.position();
            bbData.position(0);
            ByteBuffer bb = ByteBuffer.allocateDirect(iTotalLength);
            bb.put(bbData);
            bb.position(pos);
//            bb.put(bbData.array(), 0, bbData.position());
            bbData = bb;
            return left == 0;
        } else {
            return false;
        }
    }

    private boolean largeBufferAndRead(SocketChannel sc, int iMaxHttpBodyLength) {
        boolean rs = true;
        if (iDataLength <= iMaxHttpBodyLength) {
            if (largeBuffer()) {
                rs = SocketChannelHelper.read(sc, bbData) >= 0;
            }
        } else {
            Log.logError("Http Body Length Too Large " + iDataLength + ":" + iMaxHttpBodyLength);
            rs = false;
        }
        return rs;
    }

    public NioSocketActionType readHttpHead(SocketChannel sc, int iMaxHttpHeadLength, int iMaxHttpBodyLength) {
        int iLastPostion = bbData.position();
        boolean rs = (SocketChannelHelper.read(sc, bbData) > 0);
        if (rs) {
            if (iHeadLength == 0) {
                iLastPostion -= 3;
                if (iLastPostion < 0) {
                    iLastPostion = 0;
                }
                int i = ByteBufferUtils.indexOf(bbData, iLastPostion, bbData.position() + 1, HttpHeader.fbCRLFCRLF);
                if (i > 0) {
                    iHeadLength = i + HttpHeader.fbCRLFCRLF.length;
                    iDataLength = getContentLength(bbData, 0, i);
                    if (iDataLength > 0) {
                        rs = largeBufferAndRead(sc, iMaxHttpBodyLength);
                    } else {
                        if (isChunked(bbData, 0, iHeadLength)) {
                            notChuncked = false;
                            findChunkLen();
                            rs = largeBufferAndRead(sc, iMaxHttpBodyLength);
                        }
                    }
                } else if (bbData.position() > iMaxHttpHeadLength) {
                    Log.logError("Http Head Length Too Large " + bbData.position() + ":" + iMaxHttpHeadLength);
                    rs = false;
                }
            }
            if (rs && iHeadLength > 0) {
                if ((iHeadLength + iDataLength) <= bbData.position()) {
                    if (notChuncked) {
                        lActiveTicket = Long.MAX_VALUE;
                        return NioSocketActionType.OP_READ_COMPLETED;
                    } else {
                        int iChunkLen = findChunkLen();
                        if (iChunkLen == 0) {
                            lActiveTicket = Long.MAX_VALUE;
                            return NioSocketActionType.OP_READ_COMPLETED;
                        } else if (iChunkLen > 0) {
                            rs = largeBufferAndRead(sc, iMaxHttpBodyLength);
                        }
                    }
//                } else if (bbData.remaining() == 0) {
//                    Log.logError("Http Head Body Too Large " + bbData.position() + ":" + iMaxHttpHeadLength);
//                    lActiveTicket = Long.MAX_VALUE;
//                    return NioSocketActionType.OP_CLOSE;
                }
            }
        }
        if (rs) {
            return NioSocketActionType.OP_READ;
        } else {
            lActiveTicket = Long.MAX_VALUE;
            return NioSocketActionType.OP_CLOSE;
        }
    }

    public boolean findHttpHeadLength(int iStart) {
        int i = ByteBufferUtils.indexOf(bbData, iStart, bbData.position() + 1, HttpHeader.fbCRLFCRLF);
        if (i > 0) {
            iHeadLength = i + HttpHeader.fbCRLFCRLF.length;
            iDataLength = getContentLength(bbData, 0, i);
            return true;
        }
        return false;
    }

    public boolean parseRequest() {
        return parseRequest(bbData, 0);
    }

    public boolean parseRequestAndHeader() {
        return parseRequestAndHeader(bbData);
    }

    public void toByteBuffer(HttpResponse hResponse) {
        int iLen = 0;
        byte[] bResBody = hResponse.getByteBody();
        if (bResBody != null) {
            iLen = bResBody.length;
        }
        String sHeader = hResponse.toString();
        byte[] bHeader = null;
        try {
            bHeader = sHeader.getBytes(ConstData.sHttpCharset);
        } catch (Exception e) {
        }
        if (bHeader != null) {
            iLen += bHeader.length;
        }

        if (bbData.capacity() < iLen) {
            bbData = ByteBuffer.allocateDirect(iLen);
        }
        bbData.clear();
        if (bHeader != null) {
            bbData.put(bHeader);
        }
        if (bResBody != null) {
            bbData.put(bResBody);
        }
        bbData.flip();
    }

    public void readyToWrite() {
        bbData.flip();
    }


    public int read(SocketChannel sc) {
        return SocketChannelHelper.read(sc, bbData);
    }

    public NioSocketActionType write(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (SocketChannelHelper.write(sc, bbData) >= 0) {
            if (bbData.remaining() == 0) {
                reset();
                return NioSocketActionType.OP_READ;
            } else {
                return NioSocketActionType.OP_WRITE;
            }
        } else {
            return NioSocketActionType.OP_CLOSE;
        }
    }

    public int writeBlock(SocketChannel to) {
        bbData.flip();
        return SocketChannelHelper.blockWrite(to, bbData, 3);
    }

}
