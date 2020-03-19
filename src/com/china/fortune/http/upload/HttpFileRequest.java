package com.china.fortune.http.upload;

import java.util.ArrayList;

import com.china.fortune.file.FileHelper;
import com.china.fortune.http.httpHead.HttpRequest;

public class HttpFileRequest extends HttpRequest {
	private String sBoundary = java.util.UUID.randomUUID().toString();
	final private String sSpanLine = "--";
	private ArrayList<byte[]> lsObj = new ArrayList<byte[]>();

	public HttpFileRequest(String sURL) {
		super("POST", sURL);
		addHeader(csAccept, "*/*");
		addHeader(csContentType, "multipart/form-data; boundary=" + sBoundary);
	}

	public ArrayList<byte[]> getBodyList() {
		return lsObj;
	}

	public void addFile(String sFile) {
		StringBuilder sb = new StringBuilder();
		byte[] pFile = FileHelper.readSmallFile(sFile);
		if (pFile != null) {
			sb.append(sSpanLine);
			sb.append(sBoundary);
			sb.append(csEnter);

			sb.append(csContentDisposition);
			sb.append(": form-data;name=\"file\"; filename=\"");
			sb.append(FileHelper.getFileNameFromFullPath(sFile));
			sb.append('"');
			sb.append(csEnter);

			sb.append(csContentType);
			sb.append(": application/");
			sb.append(FileHelper.getFileExtension(sFile));
			sb.append(csEnter);
			sb.append(csEnter);

			lsObj.add(sb.toString().getBytes());
			lsObj.add(pFile);
			lsObj.add(csEnter.getBytes());
		}
	}

	public void addBlock(String sName, byte[] bData) {
		if (bData != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(sSpanLine);
			sb.append(sBoundary);
			sb.append(csEnter);

			sb.append(csContentDisposition);
			sb.append(": form-data; name=\"");
			sb.append(sName);
			sb.append('"');
			sb.append(csEnter);

			sb.append(csEnter);

			lsObj.add(sb.toString().getBytes());
			lsObj.add(bData);
			lsObj.add(csEnter.getBytes());
		}
	}

	public void addBlock(String sName, String sText) {
		try {
			addBlock(sName, sText.getBytes("utf-8"));
		} catch (Exception e) {
		}
	}
	
	public void addFileBlock(String sFile) {
		byte[] bData = FileHelper.readSmallFile(sFile);
		StringBuilder sb = new StringBuilder();
		sb.append(sSpanLine);
		sb.append(sBoundary);
		sb.append(csEnter);

		sb.append(csContentDisposition);
		sb.append(": form-data; name=\"file\"; filename=\"blob\"");
		sb.append(csEnter);
		sb.append(csEnter);

		lsObj.add(sb.toString().getBytes());
		lsObj.add(bData);
		lsObj.add(csEnter.getBytes());
	}

	public void addEndLine() {
		lsObj.add(sSpanLine.getBytes());
		lsObj.add(sBoundary.getBytes());
		lsObj.add(sSpanLine.getBytes());
		lsObj.add(csEnter.getBytes());
		int iContentLength = 0;
		for (byte[] pBody : lsObj) {
			iContentLength += pBody.length;
		}
		addHeader(csContentLength, String.valueOf(iContentLength));
	}
}
