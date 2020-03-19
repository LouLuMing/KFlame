package com.china.fortune.http.client;

import com.china.fortune.global.ConstData;
import com.china.fortune.http.httpBody.GetBodyAction;
import com.china.fortune.http.httpBody.HttpBodyInterface;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.socket.SocketUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.net.Socket;

public class HttpClient {
	protected SocketUtils socketUtils = new SocketUtils();

	public void setTimeOut(int iConnect, int iRecv) {
		socketUtils.setTimeOut(iConnect, iRecv);
	}

	private HttpBodyInterface ocrlsner = new GetBodyAction();

	public HttpResponse executeLoopMove(HttpRequest hrRequest) {
		HttpResponse hs = execute(hrRequest, ocrlsner);
		if (hs != null) {
			if (hs.isMoved()) {
				String sLocation = hs.getHeaderValue(HttpHeader.csLocation);
				if (sLocation != null) {
					hrRequest.parseURL(sLocation);
					hs = executeLoopMove(hrRequest);
				}
			}
		}
		return hs;
	}

	public HttpResponse execute(HttpRequest hrRequest) {
		return execute(hrRequest, ocrlsner);
	}

	public LineSocketAction createLSA(KeyManagerFactory kmf, TrustManagerFactory tmf, String sServer, int iPort) {
		LineSocketAction lSA = null;
		Socket sock = socketUtils.createSSLSocket(kmf, tmf, sServer, iPort);
		if (sock != null) {
			lSA = new LineSocketAction();
			lSA.attach(sock);
//			lSA.setSoLinger(true, ConstData.iSocketCloseWait);
		}
		return lSA;
	}

	public LineSocketAction createLSA(KeyManagerFactory kmf, TrustManagerFactory tmf, HttpRequest hrRequest) {
		return createLSA(kmf, tmf, hrRequest.getServerIP(), hrRequest.getServerPort());
	}

	public LineSocketAction createLSA(String sServer, int iPort) {
		LineSocketAction lSA = null;
		Socket sock = socketUtils.createSocket(sServer, iPort);
		if (sock != null) {
			lSA = new LineSocketAction();
			lSA.attach(sock);
		}
		return lSA;
	}

	public LineSocketAction createLSA(HttpRequest hrRequest) {
		LineSocketAction lSA = null;
		if (hrRequest.isHttp()) {
			lSA = createLSA(hrRequest.getServerIP(), hrRequest.getServerPort());
		} else {
			lSA = createLSA(null, null, hrRequest.getServerIP(), hrRequest.getServerPort());
		}
		return lSA;
	}

	public void parseHeader(HttpResponse hrResponce, LineSocketAction lSA) {
		do {
			String sLine = lSA.readLine(ConstData.sHttpCharset);
			if (sLine != null && sLine.length() > 0) {
				hrResponce.parseHeader(sLine);
			} else {
				break;
			}
		} while (true);
	}

	public HttpResponse sendDataAndRecvHead(LineSocketAction lSA, HttpRequest hrRequest) {
		HttpResponse hrResponce = null;
		String sHeader = hrRequest.toString();
		if (lSA.writeNoFlush(sHeader, ConstData.sHttpCharset)) {
			byte[] pBody = hrRequest.getByteBody();
			if (pBody == null || lSA.write(pBody)) {
				String sLine = lSA.readLine(ConstData.sHttpCharset);
				if (sLine != null) {
					hrResponce = new HttpResponse();
					if (hrResponce.parseResponse(sLine)) {
						parseHeader(hrResponce, lSA);
					} else {
						hrResponce = null;
					}
				}
			}
		}
		return hrResponce;
	}

	public HttpResponse execute(LineSocketAction lSA, HttpRequest hrRequest, HttpBodyInterface lsn) {
		HttpResponse hrResponce = null;
		if (lSA != null) {
			hrResponce = sendDataAndRecvHead(lSA, hrRequest);
			if (hrResponce != null) {
				lsn.onContentRecv(hrResponce, lSA);
			}
		}
		return hrResponce;
	}

	public HttpResponse execute(HttpRequest hrRequest, HttpBodyInterface lsn) {
		HttpResponse hrResponce = null;
		LineSocketAction lSA = createLSA(hrRequest);
		if (lSA != null) {
			hrResponce = sendDataAndRecvHead(lSA, hrRequest);
			if (hrResponce != null) {
				lsn.onContentRecv(hrResponce, lSA);
			}
			lSA.close();
		}
		return hrResponce;
	}

	public HttpResponse execute(KeyManagerFactory kmf, TrustManagerFactory tmf, HttpRequest hrRequest) {
		HttpResponse hrResponce = null;
		LineSocketAction lSA = createLSA(kmf, tmf, hrRequest);
		if (lSA != null) {
			hrResponce = sendDataAndRecvHead(lSA, hrRequest);
			if (hrResponce != null) {
				ocrlsner.onContentRecv(hrResponce, lSA);
			}
			lSA.close();
		}
		return hrResponce;
	}

	public boolean onContentRecv(HttpResponse hrResponce, LineSocketAction lSA) {
		return ocrlsner.onContentRecv(hrResponce, lSA);
	}
	// public LineSocketAction execute(HttpRequest hrRequest) {
	// LineSocketAction lSA = createLSA(hrRequest);
	// if (lSA != null) {
	// HttpResponse hrResponce = sendAndParseHead(lSA, hrRequest);
	// }
	// return lSA;
	// }
}
