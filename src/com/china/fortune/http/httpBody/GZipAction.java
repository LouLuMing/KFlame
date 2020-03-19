package com.china.fortune.http.httpBody;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.china.fortune.common.ByteArrayInputStream;

public class GZipAction {
	static public ByteArrayInputStream decode(ByteArrayInputStream in) {
		ByteArrayInputStream out = null;
		try {
			GZIPInputStream gis = new GZIPInputStream(in);
			out = new ByteArrayInputStream();
			int iRead = 0;
			do {
				byte[] pData = new byte[1024];
				iRead = gis.read(pData);
				if (iRead > 0) {
					out.add(pData, iRead);
				}
			} while (iRead > 0);
			gis.close();
		} catch (IOException e) {
		}
		return out;
	}
	
}
