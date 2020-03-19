package com.china.fortune.http.httpBody;

import com.china.fortune.common.ByteArrayInputStream;
import com.china.fortune.compress.GZipCompressor;
import com.china.fortune.global.ConstData;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.socket.LineSocketAction;

public class GetBodyAction implements HttpBodyInterface {
	private void decompress(HttpHeader hh, byte[] pEntry) {
		if (hh.checkHeaderValue(HttpHeader.csContentEncoding, "gzip")) {
			hh.setBodyNoHeader(GZipCompressor.decompress(pEntry));
		} else {
			hh.setBodyNoHeader(pEntry);
		}
	}
	
	@Override
	public boolean onContentRecv(HttpHeader hh, LineSocketAction lSA) {
		boolean rs = false;
		if (hh.checkHeaderValue(HttpHeader.csTransferEncoding, "chunked")) {
			ByteArrayInputStream bas = ChunkedAction.decode(lSA, ConstData.sHttpCharset);
			decompress(hh, bas.getByte());
			bas.close();
			rs = true;
		} else {
			int iContentLength = hh.getContentLength();
			if (iContentLength > 0 && iContentLength < HttpProp.iMaxContentLenLimited) {
				byte[] pEntry = new byte[iContentLength];
				int iRecv = lSA.read(pEntry);
				if (iRecv == iContentLength) {
					decompress(hh, pEntry);
					rs = true;
				}
			} else if (iContentLength == 0) {
				rs = true;
			}
		}
		return rs;
	}
}
