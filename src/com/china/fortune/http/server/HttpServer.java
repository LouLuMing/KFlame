package com.china.fortune.http.server;

import java.net.Socket;

import com.china.fortune.global.ConstData;
import com.china.fortune.http.httpBody.GetBodyAction;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.socket.shortConnection.ShortConnectionServer;

public abstract class HttpServer extends ShortConnectionServer {
	protected abstract void service(HttpRequest hReq, HttpResponse hRes);

	
	@Override
	protected void onRead(Socket sc) {
		LineSocketAction lSA = new LineSocketAction();
		lSA.attach(sc);
		boolean bKeepAlive;
		do {
			bKeepAlive = false;
			String sLine = lSA.readLine(ConstData.sHttpCharset);
			if (sLine != null) {
				HttpRequest hReq = new HttpRequest();
				if (hReq.parseRequest(sLine)) {
					do {
						sLine = lSA.readLine(ConstData.sHttpCharset);
						if (sLine != null && sLine.length() > 0) {
							hReq.parseHeader(sLine);
						} else {
							break;
						}
					} while (true);

					if (sLine != null && sLine.length() == 0) {
						GetBodyAction gca = new GetBodyAction();
						if (gca.onContentRecv(hReq, lSA)) {
							HttpResponse hRes = new HttpResponse();

							service(hReq, hRes);

							byte[] pBody = hRes.getByteBody();
							if (pBody == null) {
								hRes.addHeader(HttpHeader.csContentLength, "0");
							}

							String sHeader = hRes.toString();
							if (lSA.write(sHeader, ConstData.sHttpCharset)) {
								bKeepAlive = true;
								if (pBody != null) {
									bKeepAlive = lSA.write(pBody);
								}
								
								if (bKeepAlive) {
									bKeepAlive = hReq.checkHeaderValue("Connection", "Keep-Alive");
								}
							}
						}
					}
				}
			}
		} while (bKeepAlive);

		lSA.dettach();
	}
}
