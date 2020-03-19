package com.china.fortune.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.china.fortune.global.Log;

public class ReadLineFileAction {
	private ReadEnterBuffer inSteam = null;

	public boolean open(File f) {
		boolean hz = false;
		if (f.exists()) {
			InputStream is = null;
			try {
				is = new FileInputStream(f);
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			if (is != null) {
				inSteam = new ReadEnterBuffer(is);
				hz = true;
			}
		}
		return hz;
	}

	public boolean open(String fullfilename) {
		boolean hz = false;
		if (fullfilename != null) {
			if (inSteam != null) {
				inSteam.close();
				inSteam = null;
			}
			File f = new File(fullfilename);
			if (f.exists()) {
				InputStream is = null;
				try {
					is = new FileInputStream(f);
				} catch (Exception e) {
					Log.logClass(e.getMessage());
				}
				if (is != null) {
					inSteam = new ReadEnterBuffer(is);
					hz = true;
				}
			} else {
				Log.logClass("Not Exists:" + fullfilename);
			}
		}
		return hz;
	}

	public void close() {
		if (inSteam != null) {
			inSteam.close();
			inSteam = null;
		}
	}

	public String readLine(String sCharset) {
		return inSteam.readLine(sCharset);
	}

	public String readLine() {
		return inSteam.readLine();
	}
}
