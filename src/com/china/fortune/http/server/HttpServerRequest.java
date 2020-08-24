package com.china.fortune.http.server;

import com.china.fortune.common.ByteBufferUtils;
import com.china.fortune.easy.Int2Struct;
import com.china.fortune.file.FileUtils;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpFormData;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.socket.IPUtils;
import com.china.fortune.socket.SocketChannelUtils;
import com.china.fortune.socket.selectorManager.NioSocketActionType;
import com.china.fortune.string.StringUtils;
import com.china.fortune.xml.ByteParser;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpServerRequest extends HttpRequest {
    static final protected int iDefBufferSize = 4 * 1024;
    static final protected int iLimitBufferSize = 1024 * 1024;

    protected ByteBuffer bbData = ByteBuffer.allocateDirect(iDefBufferSize);
    public int iHeadLength = 0;
    public int iDataLength = 0;
    public boolean notChuncked = true;
    public byte[] bRemoteAddr = null;
    public long lActiveTicket = System.currentTimeMillis();

    public void setRemoteAddr(String sIp) {
        bRemoteAddr = IPUtils.Ip2Bytes(sIp);
    }

    public String getRmoteStringIP() {
        return IPUtils.bytes2String(bRemoteAddr);
    }

    public int getRemoteIP() {
        return IPUtils.bytes2Int(bRemoteAddr);
    }

    public boolean remoteIPEqualTo(int ip) {
        return IPUtils.bytes2Int(bRemoteAddr) == ip;
    }

    public void logByteBuffer() {
        if (bbData.limit() > 0) {
            Log.log(ByteBufferUtils.toString(bbData, 0, bbData.limit()));
//            Log.log(new String(bbData.array(), 0, bbData.position()));
        }
    }

    public void fetchAddress(SocketChannel sc) {
        InetSocketAddress isa = (InetSocketAddress) sc.socket().getRemoteSocketAddress();
        if (isa != null) {
            bRemoteAddr = isa.getAddress().getAddress();
        }
    }

    public void reset() {
        super.clear();
        bbData.clear();
        iHeadLength = 0;
        iDataLength = 0;
    }

    public void clear() {
        super.clear();
        if (bbData.capacity() > iLimitBufferSize) {
            bbData = ByteBuffer.allocateDirect(iDefBufferSize);
        } else {
            bbData.clear();
        }
        lActiveTicket = System.currentTimeMillis();
        iHeadLength = 0;
        iDataLength = 0;
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
            sBody = StringUtils.newString(bbData, iHeadLength, iHeadLength + iMax, sCharset);
        }
        return sBody;
    }


    @Override
    public String getBody() {
        if (sBody == null) {
            if (iDataLength > 0) {
                String sCharset = getCharset();
                if (sCharset == null) {
                    sCharset = ConstData.sHttpCharset;
                }
                sBody = StringUtils.newString(bbData, iHeadLength, iHeadLength + iDataLength, sCharset);
            }
        }
        return sBody;
    }

    @Override
    public int getContentLength() {
        return iDataLength;
    }

    @Override
    public byte[] getByteBody() {
        if (bBody == null) {
            if (iDataLength > 0) {
                bBody = ByteBufferUtils.toByte(bbData, iHeadLength, iHeadLength + iDataLength);
//            bBody = new byte[iDataLength];
//            if (iDataLength > 0) {
//                System.arraycopy(bbData.array(), iHeadLength, bBody, 0, iDataLength);
//            }
            }
        }
        return bBody;
    }

    private int findFormData(ByteBuffer bb, int iOff, byte[] bTag) {
        int i = ByteBufferUtils.indexOf(bb, iOff, bb.position()+1, bTag);
        if (i > 0) {
            i = ByteBufferUtils.indexOf(bb, i, bb.position()+1, fbCRLFCRLF);
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
        int i = ByteBufferUtils.indexOf(bb, iStart, bb.position()+1, sNameDot);
        if (i > 0) {
            i += sNameDot.length;
            int j = ByteBufferUtils.indexOf(bb, i, bDot);
            if (j > i) {
                return ByteBufferUtils.toString(bb, i, j);
//                return new String(bBody, i, j - i);
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
            String sTag = StringUtils.getAfter(sRType, "boundary=");
            if (StringUtils.length(sTag) > 0) {
                HashMap<String, Int2Struct> mapIndex = new HashMap<String, Int2Struct>();
                byte[] bTag = sTag.getBytes();
                int iOff = iHeadLength;
                while (true) {
                    int iStart = findFormData(bbData, iOff, bTag);
                    if (iStart > 0) {
                        String sName = findFormDataName(bbData, iOff);
                        iOff = ByteBufferUtils.indexOf(bbData, iStart, bbData.position()+1, bTag);
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
            return StringUtils.newString(bbData, i2s.i1, i2s.i2);
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
        int chunkSize = -1;
        do {
            int chunkHead = iHeadLength + iDataLength;
            if (chunkHead < bbData.position()) {
                int i = ByteBufferUtils.indexOf(bbData, chunkHead, bbData.position() + 1, HttpHeader.fbCRLF);
                if (i > 0) {
                    chunkSize = hexToInt(bbData, chunkHead, i);
                    iDataLength += chunkSize + (i - chunkHead + 4);
                } else {
                    break;
                }
            } else {
                break;
            }
//            Log.logClass("chunkSize " + chunkSize);
        } while (chunkSize > 0);
        return chunkSize;
    }

    private void largeBuffer(int iTotalLength) {
        int pos = bbData.position();
        bbData.position(0);
        ByteBuffer bb = ByteBuffer.allocateDirect(iTotalLength);
        bb.put(bbData);
        bb.position(pos);
        bbData = bb;
    }

    private boolean largeBufferAndRead(SocketChannel sc, int iMaxHttpBodyLength) {
        if (iDataLength + iHeadLength > bbData.capacity()) {
            if (iDataLength <= iMaxHttpBodyLength) {
                int iLeft = bbData.remaining();
                largeBuffer(iHeadLength + iDataLength + 16);
                if (iLeft == 0) {
                    return SocketChannelUtils.read(sc, bbData) >= 0;
                }
            } else {
                Log.logError("Http Body Length Too Large " + (iHeadLength + iDataLength) + " Limit " + iMaxHttpBodyLength);
            }
        }
        return false;
    }

    private NioSocketActionType parseNormalData() {
        if ((iHeadLength + iDataLength) <= bbData.position()) {
            lActiveTicket = Long.MAX_VALUE;
            return NioSocketActionType.NSA_READ_COMPLETED;
        } else {
            return NioSocketActionType.NSA_READ;
        }
    }

    private NioSocketActionType parseChunckData(SocketChannel sc, int iMaxHttpBodyLength) {
        int iChunkLen = findChunkLen();
        if (iChunkLen == 0) {
            lActiveTicket = Long.MAX_VALUE;
            return NioSocketActionType.NSA_READ_COMPLETED;
        } else if (iChunkLen > 0) {
            if (bbData.capacity() < iHeadLength + iDataLength) {
                if (iDataLength <= iMaxHttpBodyLength) {
                    largeBuffer(iHeadLength + iDataLength + 16);
                } else {
                    Log.logError("Http Body Length Too Large " + (iHeadLength + iDataLength) + " Limit " + iMaxHttpBodyLength);
                    lActiveTicket = Long.MAX_VALUE;
                    return NioSocketActionType.NSA_CLOSE;
                }
            }
        } else {
            int iLimitCap = iHeadLength + iDataLength + 16;
            if (bbData.position() > iLimitCap) {
                Log.logError("Http ChunckSize is Miss");
                lActiveTicket = Long.MAX_VALUE;
                return NioSocketActionType.NSA_CLOSE;
            } else if (bbData.capacity() < iLimitCap) {
//                largeBuffer(iLimitCap);
                if (largeBufferAndRead(sc, iLimitCap)) {
                    return parseChunckData(sc, iLimitCap);
                }
            }
        }
        return NioSocketActionType.NSA_READ;
    }

    private NioSocketActionType parseData(SocketChannel sc, int iMaxHttpBodyLength) {
        if ((iHeadLength + iDataLength) <= bbData.position()) {
            if (notChuncked) {
                lActiveTicket = Long.MAX_VALUE;
                return NioSocketActionType.NSA_READ_COMPLETED;
            } else {
                return parseChunckData(sc, iMaxHttpBodyLength);
            }
        }
        return NioSocketActionType.NSA_READ;
    }

    public NioSocketActionType readHttpHead(SocketChannel sc, int iMaxHttpHeadLength, int iMaxHttpBodyLength) {
        int iLastPostion = bbData.position();
        boolean rs = (SocketChannelUtils.read(sc, bbData) > 0);
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
                    if (iDataLength == 0) {
                        lActiveTicket = Long.MAX_VALUE;
                        return NioSocketActionType.NSA_READ_COMPLETED;
                    } else if (iDataLength > 0) {
                        notChuncked = true;
                        largeBufferAndRead(sc, iMaxHttpBodyLength);
                        return parseNormalData();
                    } else {
                        if (isChunked(bbData, 0, iHeadLength)) {
                            notChuncked = false;
                            return parseChunckData(sc, iMaxHttpBodyLength);
                        } else {
                            lActiveTicket = Long.MAX_VALUE;
                            return NioSocketActionType.NSA_READ_COMPLETED;
                        }
                    }
                } else if (bbData.position() > iMaxHttpHeadLength) {
                    Log.logError("Http Head Length Too Large " + bbData.position() + ":" + iMaxHttpHeadLength);
                    rs = false;
                }
            } else {
                return parseData(sc, iMaxHttpBodyLength);
            }
        }
        if (rs) {
            return NioSocketActionType.NSA_READ;
        } else {
            lActiveTicket = Long.MAX_VALUE;
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    public boolean findHttpHeadLength() {
        int i = ByteBufferUtils.indexOf(bbData, 10, HttpHeader.fbCRLFCRLF);
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

    public boolean parseHeader() {
        return parseHeader(bbData);
    }

    public boolean parseRequestAndHeader() {
        return parseRequestAndHeader(bbData);
    }

    public void setByteBuffer(byte[] bData) {
        int iLen = bData.length;
        if (bbData.capacity() < iLen) {
            bbData = ByteBuffer.allocateDirect(iLen);
        } else {
            bbData.clear();
        }
        bbData.put(bData);
        bbData.flip();
    }

    public void appendString(String sText) {
        try {
            byte[] bData = sText.getBytes("utf-8");
            bbData.put(bData);
        } catch (Exception e) {
        }
    }

    public void setResponse(int ciCode) {
        bbData.clear();
        appendString(csVersion);
        bbData.put((byte)' ');
        appendString(String.valueOf(ciCode));
        bbData.put((byte)' ');
        String sCode = HttpProp.getError(ciCode);
        appendString(sCode);
        appendString(csEnter);
    }

    public void appendHeader(String sKey, String sValue) {
        appendString(sKey);
        bbData.put((byte)':');
        bbData.put((byte)' ');
        appendString(sValue);
        appendString(csEnter);
    }

    public void appendFileHeader(String sFileName) {
        String sShortFileName = FileUtils.getSimpleName(sFileName);
        String sContentType = HttpProp.getContentTypeByFile(sShortFileName);
        if (sContentType == null) {
            sContentType = HttpProp.csDefaultContentType;
        }
        appendHeader(csContentDisposition, "filename=" + sShortFileName);
        appendHeader(csContentType, sContentType);
    }

    public void appendBody(byte[] bData) {
        int iData = 0;
        if (bData != null) {
            iData = bData.length;
        }
        appendHeader(csContentLength, String.valueOf(iData));
        bbData.put(csEnter.getBytes());
        if (bData != null) {
            int iLen = bbData.position() + iData;
            if (bbData.capacity() < iLen) {
                largeBuffer(iLen);
            }
            bbData.put(bData);
        }
        bbData.flip();
    }

    public void setByteBuffer(HttpResponse hResponse) {
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
        } else {
            bbData.clear();
        }
        if (bHeader != null) {
            bbData.put(bHeader);
        }
        if (bResBody != null) {
            bbData.put(bResBody);
        }
        bbData.flip();
    }

    public void setByteBuffer(HttpFormData hf) {
        int iLen = 0;
        ArrayList<byte[]> lsObj = hf.getBodyList();
        if (lsObj != null) {
            for (int i = 0; i < lsObj.size(); i++) {
                iLen += lsObj.get(i).length;
            }
        }
        String sHeader = hf.toString();
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
        } else {
            bbData.clear();
        }
        if (bHeader != null) {
            bbData.put(bHeader);
        }
        if (lsObj != null) {
            for (int i = 0; i < lsObj.size(); i++) {
                bbData.put(lsObj.get(i));
            }
        }
//        bbData.flip();
    }

    public void readyToWrite() {
        bbData.flip();
    }

    public int read(SocketChannel sc) {
        return SocketChannelUtils.read(sc, bbData);
    }

//    public NioSocketActionType proxyData(SelectionKey key) {
//        bbData.flip();
//        key.interestOps(SelectionKey.OP_WRITE);
//        return NioSocketActionType.NSA_NULL;
//    }

    public NioSocketActionType proxyData(SelectionKey key) {
        bbData.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        if (SocketChannelUtils.write(sc, bbData) >= 0) {
            if (bbData.remaining() == 0) {
                reset();
                key.interestOps(SelectionKey.OP_READ);
            } else {
                key.interestOps(SelectionKey.OP_WRITE);
            }
            return NioSocketActionType.NSA_NULL;
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    public NioSocketActionType write(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (SocketChannelUtils.write(sc, bbData) > 0) {
            if (bbData.remaining() == 0) {
                reset();
                key.interestOps(SelectionKey.OP_READ);
            } else {
                key.interestOps(SelectionKey.OP_WRITE);
            }
            return NioSocketActionType.NSA_NULL;
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    public NioSocketActionType writeOrNo(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        if (SocketChannelUtils.write(sc, bbData) >= 0) {
            if (bbData.remaining() == 0) {
                reset();
                key.interestOps(SelectionKey.OP_READ);
            } else {
                key.interestOps(SelectionKey.OP_WRITE);
            }
            return NioSocketActionType.NSA_NULL;
        } else {
            return NioSocketActionType.NSA_CLOSE;
        }
    }

    public int read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        int iRecv;
        try {
            iRecv = sc.read(bbData);
        } catch (Exception e) {
            iRecv = -1;
            Log.logClass(e.getMessage());
        }
        return iRecv;
    }

    public boolean findIfNoneMatch() {
        int i = ByteBufferUtils.indexOf(bbData, 0, bbData.position() + 1, HttpHeader.fbCRLF);
        if (i > 0) {
            return findIfNoneMatch(bbData, i, iHeadLength) > 0;
        } else {
            return false;
        }
    }

    public byte[] toByte() {
        int iLen = bbData.limit();
        byte[] bData = new byte[iLen];
        bbData.get(bData);
        bbData.position(0);
        return bData;
    }
}
