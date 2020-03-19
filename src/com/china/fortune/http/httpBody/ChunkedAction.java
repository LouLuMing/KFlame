package com.china.fortune.http.httpBody;

import com.china.fortune.common.ByteArrayInputStream;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.string.StringAction;

public class ChunkedAction {
	static final int iMaxContentLenLimited = 64 * 1024 * 1024;
	static public ByteArrayInputStream decode(LineSocketAction lSA, String sCharset) {
		ByteArrayInputStream bas = null;
		if (lSA != null) {
			int iChunkLen = 0;
			bas = new ByteArrayInputStream();
			int iChunkCount = 0;
			do {
				String sLine = lSA.readLine(sCharset);
				iChunkLen = StringAction.hexToInt(sLine);
				if (iChunkLen > 0 && iChunkLen < HttpProp.iMaxChunkedLenLimited) {
					byte[] pData = new byte[iChunkLen];
					if (lSA.read(pData) > 0) {
						bas.add(pData);
					}
					lSA.readLine(sCharset);
					iChunkCount++;
				} else {
					break;
				}
			} while (iChunkCount < HttpProp.iMaxChunkedCount);
		}
		return bas;
	}
	
}
