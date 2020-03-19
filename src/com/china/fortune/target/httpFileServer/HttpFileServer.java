package com.china.fortune.target.httpFileServer;

import com.china.fortune.file.FileHelper;
import com.china.fortune.global.ConstData;
import com.china.fortune.global.Log;
import com.china.fortune.http.UrlParam;
import com.china.fortune.http.httpHead.HttpHeader;
import com.china.fortune.http.httpHead.HttpRequest;
import com.china.fortune.http.httpHead.HttpResponse;
import com.china.fortune.http.property.HttpProp;
import com.china.fortune.os.file.PathUtils;
import com.china.fortune.socket.LineSocketAction;
import com.china.fortune.socket.shortConnection.ShortConnectionServerAttach;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class HttpFileServer extends ShortConnectionServerAttach {
	protected String sRootPath = PathUtils.getCurrentDataPath(false);
	protected String sDomain = null;

	final protected int ciBufferSize = 1024 * 1024;
	final static String csDefaultContentType = "application/octet-stream";

	protected HashMap<String, HttpFileAction> mapHttpPost = new HashMap<String, HttpFileAction>();
	protected HashMap<String, HttpFileAction> mapHttpGet = new HashMap<String, HttpFileAction>();

	public void setRootPath(String sPath) {
		Log.logClass(sPath);
		if (!".".equals(sPath)) {
			sRootPath = PathUtils.delSeparator(sPath);
		}
	}

	public String getRootPath(boolean bSeparatorEndian) {
		if (bSeparatorEndian) {
			return sRootPath + File.separator;
		} else {
			return sRootPath;
		}
	}

	public void setDomain(String s) {
		sDomain = s;
	}

	public void addHttpFileAction(HttpFileAction obj) {
		String sPost = obj.getPostResource();
		if (sPost != null) {
			mapHttpPost.put(sPost, obj);
		}
		String sGet = obj.getGetResource();
		if (sGet != null) {
			mapHttpGet.put(sGet, obj);
		}
	}

	protected void writeHttpRequest(LineSocketAction lSA, HttpResponse hRes) {
		String sHeader = hRes.toString();
		if (lSA.write(sHeader, ConstData.sHttpCharset)) {
            byte[] pBody = hRes.getByteBody();
			if (pBody != null) {
				lSA.write(pBody);
			}
		}
	}

	@Override
	protected void onRead(Socket sc, Object objForThread) {
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
					bKeepAlive = hReq.checkHeaderValue("Connection", "Keep-Alive");
					
					if (sLine != null && sLine.length() == 0) {
						String sResource = hReq.getResource();
						Log.logClass(sResource);
						if ("GET".equals(hReq.getMethod())) {
							String sTag = UrlParam.getFirstResource(sResource);
							HttpFileAction hrwi = null;
							if (sTag != null) {
								hrwi = mapHttpGet.get(sTag);
							} else {
								hrwi = mapHttpGet.get(".");
							}
							if (hrwi != null && hrwi.checkHttpGet(hReq)) {
								httpGetFile(lSA, hReq, objForThread, hrwi);
							}

						} else if ("POST".equals(hReq.getMethod())) {
							String sTag = UrlParam.getResource(sResource);
							if (sTag != null) {
								HttpFileAction hrwi = mapHttpPost.get(sTag);
								if (hrwi != null && hrwi.checkHttpPost(hReq)) {
									httpPostFile(lSA, hReq, objForThread, hrwi);
								}
							}
							// } else if ("DELETE".equals(hReq.getMethod())) {
							// deleteFile(hReq.getResource());
							// okReturn(lSA, hReq, objForThread);
						} else {
							httpReturn(lSA, 403);
						}
					}
				}
			}
		} while (bKeepAlive);
		lSA.dettach();
	}

	private void httpReturn(LineSocketAction lSA, int iCode) {
		HttpResponse hRes = new HttpResponse();
		hRes.setResponse(iCode);
		writeHttpRequest(lSA, hRes);
	}

	private void httpReturn(LineSocketAction lSA, HttpResponse hRes, int iCode) {
		hRes.setResponse(iCode);
		writeHttpRequest(lSA, hRes);
	}

	public void deleteFileByUrl(String sUrl) {
		if (sUrl.startsWith(sDomain)) {
			String sResource = sUrl.substring(sDomain.length());
			deleteFile(sResource);
		}
	}

	private void deleteFile(String sResource) {
		String sName = UrlParam.getResource(sResource);
		String sFile = sRootPath + sName;
		File fileObj = new File(sFile);
		fileObj.deleteOnExit();
	}

	protected void httpGetFile(LineSocketAction lSA, HttpRequest hReq, Object objForThread, HttpFileAction hrwi) {
		String sName = hrwi.getFileName(hReq);
		HttpResponse hRes = new HttpResponse();

		String sFile = sRootPath + sName;
		File fileObj = new File(sFile);
		if (fileObj.exists() && fileObj.isFile()) {
			long iFileLen = fileObj.length();
			hRes.addHeader(HttpHeader.csContentLength, String.valueOf(iFileLen));
			String sContentType = csDefaultContentType;
			String sFileExtension = FileHelper.getFileExtension(sName);
			if (sFileExtension != null) {
				String sType = HttpProp.getContentType(sFileExtension);
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
			Log.logClass("Not Found:" + sFile);
			hRes.setResponse(404);
			writeHttpRequest(lSA, hRes);
		}
	}

	protected void httpPostFile(LineSocketAction lSA, HttpRequest hReq, Object objForThread, HttpFileAction hrwi) {
		HttpResponse hRes = new HttpResponse();
		String sPrev = hrwi.getGetResource();
		if (sPrev != null) {
			String sRelativePath = hrwi.createRelativePath(hReq);
			String sFileName = hrwi.createFileName(hReq);

			StringBuilder sbFullPath = new StringBuilder();
			sbFullPath.append(sRootPath);
			sbFullPath.append(File.separator);
			if (!sPrev.equals(".")) {
				sbFullPath.append(sPrev);
				sbFullPath.append(File.separator);
			}
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
					if (!sPrev.equals(".")) {
						sb.append(sPrev);
						sb.append('/');
					}
					if (sRelativePath != null) {
						sb.append(sRelativePath);
						sb.append('/');
					}
					sb.append(sFileName);
					hRes.setBody(sb.toString(), "text/plain", "utf-8");
				} else {
					hRes.setResponse(500);
                    hRes.setBody(null);
				}
			}
			writeHttpRequest(lSA, hRes);
		} else {
			httpReturn(lSA, hRes, 404);
		}
	}

	@Override
	protected Object createObjectInThread() {
		return new byte[ciBufferSize];
	}

	@Override
	protected void destroyObjectInThread(Object objForThread) {
	}
}
