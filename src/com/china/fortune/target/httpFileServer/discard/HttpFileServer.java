package com.china.fortune.target.httpFileServer.discard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import com.china.fortune.common.DateAction;
import com.china.fortune.common.PrimaryKey;
import com.china.fortune.file.FileHelper;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.UrlParam;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.socket.shortConnection.ShortConnectionServerAttach;

public class HttpFileServer extends ShortConnectionServerAttach {
	protected String sRootPath = PathUtils.getCurrentDataPath(false);
	protected String sDomain = null;

	final protected int ciBufferSize = 1024 * 1024;
	final static String csDefaultContentType = "application/octet-stream";
	static HashMap<String, String> mapContentType = new HashMap<String, String>(
			128);

	protected HashMap<String, String> mapUrlAndDir = new HashMap<String, String>();

	static {
		mapContentType.put("css", "text/css");
		mapContentType.put("html", "text/html");
		mapContentType.put("js", "application/x-javascript");
		mapContentType.put("png", "image/png");
		mapContentType.put("gif", "image/gif");
		mapContentType.put("ico", "image/x-icon");
		mapContentType.put("jpg", "image/jpeg");
		mapContentType.put("txt", "text/plain");
		mapContentType.put("json", "application/json");
	}

	public void addUrlMapDir(String sUrl, String sDir) {
		mapUrlAndDir.put(sUrl, sDir);
	}

	protected PrimaryKey fileNameObj = new PrimaryKey();

	protected enum HttpRequestType {
		GET, POST, ERROR
	};

	protected HttpRequestType checkHttpRequest(HttpRequest hReq) {
		String sMethod = hReq.getMethod();
		if (sMethod.equals("POST")) {
			return HttpRequestType.POST;
		} else if (sMethod.equals("GET")) {
			return HttpRequestType.GET;
		}
		return HttpRequestType.ERROR;
	}

	public void setRootPath(String sPath) {
		Log.logClass(sPath);
		sRootPath = PathUtils.delSeparator(sPath);
	}

	public void setDomain(String s) {
		sDomain = s;
	}

	protected void writeHttpRequest(LineSocketAction lSA, HttpResponse hRes) {
		byte[] pBody = hRes.getByteBody();
		if (pBody == null) {
			hRes.addHeader(HttpHeader.csContentLength, "0");
		}
		String sHeader = hRes.toString();
		if (lSA.write(sHeader, ConstData.sHttpCharset)) {
			if (pBody != null) {
				lSA.write(pBody);
			}
		}
	}

	@Override
	protected void onRead(Socket sc, Object objForThread) {
		LineSocketAction lSA = new LineSocketAction();
		lSA.attach(sc);

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
					HttpRequestType hrt = checkHttpRequest(hReq);
					switch (hrt) {
					case GET:
						readAndSendFile(lSA, hReq, objForThread);
						break;
					case POST:
						recvAndWriteFile(lSA, hReq, objForThread);
						break;
					default:
						errorReturn(lSA, hReq, objForThread);
						break;
					}
				}
			}
		}
		lSA.dettach();
	}

	private void errorReturn(LineSocketAction lSA, HttpRequest hReq,
			Object objForThread) {
		HttpResponse hRes = new HttpResponse();
		hRes.setResponse(403);
		writeHttpRequest(lSA, hRes);
	}

	protected void readAndSendFile(LineSocketAction lSA, HttpRequest hReq,
			Object objForThread) {
		String sResource = hReq.getResource();
		String sName = null;
		int iEnd = sResource.indexOf('?');
		if (iEnd > 0) {
			sName = sResource.substring(0, iEnd);
		} else {
			sName = sResource;
		}
		HttpResponse hRes = new HttpResponse();

		String sFile = sRootPath + sName;
		File fileObj = new File(sFile);
		if (fileObj.exists() && fileObj.isFile()) {
			long iFileLen = fileObj.length();
			hRes.addHeader(HttpHeader.csContentLength, String.valueOf(iFileLen));
			String sContentType = csDefaultContentType;
			String sFileExtension = FileHelper.getFileExtension(sResource);
			if (sFileExtension != null) {
				String sType = mapContentType.get(sFileExtension);
				if (sType != null) {
					sContentType = sType;
				}
			}
			hRes.addHeader(HttpHeader.csContentType, sContentType);

			if (lSA.write(hRes.toString(), ConstData.sHttpCharset)) {
				long iLeftLen = iFileLen;
				if (iLeftLen > 0) {
					try {
						byte[] pData = (byte[]) objForThread;
						InputStream is = new FileInputStream(fileObj);
						while (iLeftLen > 0) {
							int iRecv = is.read(pData);
							if (iRecv > 0) {
								iLeftLen -= iRecv;
								if (!lSA.write(pData, iRecv)) {
									break;
								}
							}
						}
						is.close();
					} catch (Exception e) {
						Log.logClass(e.getMessage());
					}
				}
			}
		} else {
			hRes.setResponse(404);
			writeHttpRequest(lSA, hRes);
		}
	}

	protected String createRelativePath(HttpRequest hReq) {
		return DateAction.getDateTime("yyyyMMdd");
	}

	protected String createFileName(HttpRequest hReq) {
		return fileNameObj.createUnicode();
	}

	protected void recvAndWriteFile(LineSocketAction lSA, HttpRequest hReq,
			Object objForThread) {
		HttpResponse hRes = new HttpResponse();

		String sTag = UrlParam.getResource(hReq.getResource());
		String sPrev = mapUrlAndDir.get(sTag);
		if (sPrev != null) {
			String sRelativePath = createRelativePath(hReq);
			String sFileName = createFileName(hReq);

			StringBuilder sbFullPath = new StringBuilder();
			sbFullPath.append(sRootPath);
			sbFullPath.append(File.separator);
			sbFullPath.append(sPrev);
			sbFullPath.append(File.separator);
			if (sRelativePath != null) {
				sbFullPath.append(sRelativePath);
				sbFullPath.append(File.separator);
			}

			long iLeftLen = hReq.getContentLength();
			if (iLeftLen > 0) {
				if (PathUtils.create(sbFullPath.toString())) {
					sbFullPath.append(sFileName);
					File fileObj = new File(sbFullPath.toString());
					fileObj.deleteOnExit();
					byte[] pData = (byte[]) objForThread;
					try {
						fileObj.createNewFile();
						OutputStream is = new FileOutputStream(fileObj);
						int iOneSize = ciBufferSize;
						while (iLeftLen > 0) {
							if (iLeftLen < iOneSize) {
								iOneSize = (int) iLeftLen;
							}

							int iRecv = lSA.read(pData, iOneSize);
							if (iRecv > 0) {
								iLeftLen -= iRecv;
								is.write(pData, 0, iRecv);
							} else {
								break;
							}
						}
						is.close();
					} catch (Exception e) {
						Log.logClass(e.getMessage());
					}
				}

				if (iLeftLen == 0) {
					StringBuilder sb = new StringBuilder();
					if (sDomain != null) {
						sb.append(sDomain);
					}
					sb.append('/');
					sb.append(sPrev);
					sb.append('/');
					if (sRelativePath != null) {
						sb.append(sRelativePath);
						sb.append('/');
					}
					sb.append(sFileName);
					hRes.setBody(sb.toString(), "text/plain", "utf-8");
				} else {
					hRes.setResponse(500);
				}
			}
		} else {
			hRes.setResponse(404);
		}
		writeHttpRequest(lSA, hRes);
	}

	@Override
	protected Object createObjectInThread() {
		return new byte[ciBufferSize];
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}
}
