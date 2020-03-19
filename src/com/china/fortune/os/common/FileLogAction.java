package com.china.fortune.os.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

import com.china.fortune.common.DateAction;
import com.china.fortune.file.FileHelper;
import com.china.fortune.os.file.PathUtils;

public class FileLogAction {
	static private final String LOG_EXTENSION = ".log";
	static private final String LOG_PATH = "log" + File.separator;
	private String sLogFile = "myLog";
	private String sLogDir = LOG_PATH;
	private OutputStream fileWriter = null;

	private AtomicLong lLastDays = new AtomicLong(-1);
	final private int ciExtendTextlength = 16;

	public void clearHistory(int days) {
		StringBuilder sb = new StringBuilder();
		sb.append(sLogDir);
		sb.append(sLogFile);
		sb.append(DateAction.getDateAddDay(-days));
		sb.append(LOG_EXTENSION);
		FileHelper.delete(sb.toString());
	}
	
	public FileLogAction(String sDir, String sFile) {
		setLog(sDir, sFile);
	}

	public FileLogAction() {
	}

	public void closeFile() {
		if (fileWriter != null) {
			try {
				fileWriter.close();
			} catch (Exception e) {
			}
			fileWriter = null;
		}
	}

	private boolean openFile() {
		boolean rs = false;
		closeFile();
		if (PathUtils.create(sLogDir)) {
			try {
				String logFile = sLogFile + DateAction.getDate() + LOG_EXTENSION;
				File f = new File(sLogDir + logFile);
				if (!f.exists()) {
					f.createNewFile();
				}
				fileWriter = new FileOutputStream(f, true);
				rs = true;
			} catch (Exception e) {
			}
		}
		return rs;
	}

	public void setLog(String sDir, String sFile) {
		if (sFile != null) {
			sLogFile = sFile;
		}
		if (sDir != null) {
			sLogDir = PathUtils.addSeparator(sDir);
		}
	}

	private void decimalToString(ByteBuffer bb, int iData) {
		if (iData < 10) {
			bb.put((byte) '0');
			bb.put((byte) (iData + '0'));
		} else {
			bb.put((byte) (iData / 10 + '0'));
			bb.put((byte) (iData % 10 + '0'));
		}
	}

	private void writeFile(ByteBuffer bb) {
		if (fileWriter != null) {
			try {
				fileWriter.write(bb.array(), 0, bb.position());
			} catch (Exception e) {
				lLastDays.set(0);
			}
		}
	}

	public void putLong(ByteBuffer bb, long iData) {
		for (int i = 0; i < 8; i++) {
			bb.put((byte) ((iData & 0xff) + '0'));
			iData >>= 8;
		}
	}

	protected int addTime(ByteBuffer bb) {
		long iMil = System.currentTimeMillis();
		long iSecond = iMil / 1000;
		long iMin = iSecond / 60;
		long iHour = (iMin / 60) + 8;

		int iNowS = (int) (iSecond % 60);
		int iNowM = (int) (iMin % 60);
		int iNowH = (int) (iHour % 24);
		int iNowD = (int) (iHour / 24);

		decimalToString(bb, iNowH);
		bb.put((byte) ':');
		decimalToString(bb, iNowM);
		bb.put((byte) ':');
		decimalToString(bb, iNowS);

		bb.put((byte) ' ');

//		putLong(bb, Thread.currentThread().getId());
//
//		bb.put((byte) ' ');
		return iNowD;
	}

	protected void writeData(int iNowD, ByteBuffer bb) {
		bb.put((byte) '\n');

		if (iNowD > lLastDays.get() || fileWriter == null) {
			synchronized (this) {
				if (iNowD > lLastDays.get() || fileWriter == null) {
					if (openFile()) {
						lLastDays.set(iNowD);
					}
				}
				writeFile(bb);
			}
		} else {
			writeFile(bb);
		}
	}

	private byte[] bDots = new byte[] { '.', '.', '.' };

	protected void addByte(ByteBuffer bb, byte[] bData, int iOff, int iLen) {
		int iLeft = bb.remaining() - ciExtendTextlength;
		if (iLen > iLeft) {
			if (iLeft > 0) {
				iLen = iLeft;
				bb.put(bData, iOff, iLen);
				bb.put(bDots);
			}
		} else {
			bb.put(bData, iOff, iLen);
		}
	}

	public void log(byte[] bData) {
		if (bData != null) {
			ByteBuffer bb = ByteBuffer.allocate(bData.length + ciExtendTextlength);
			int iNowD = addTime(bb);
			bb.put(bData);
			writeData(iNowD, bb);
		}
	}

	public void log(byte[] bData, int iData) {
		if (bData != null) {
			ByteBuffer bb = ByteBuffer.allocate(iData + ciExtendTextlength);
			int iNowD = addTime(bb);
			bb.put(bData, 0, iData);
			writeData(iNowD, bb);
		}
	}

	public void log(byte[] bTag, String sText) {
		if (bTag != null && sText != null) {
			try {
				byte[] bData = sText.getBytes("utf-8");
				ByteBuffer bb = ByteBuffer.allocate(bTag.length + bData.length + ciExtendTextlength);
				int iNowD = addTime(bb);
				bb.put(bTag);
				bb.put((byte) ':');
				bb.put(bData);
				writeData(iNowD, bb);
			} catch (Exception e) {
			}
		}
	}

	public void log(String sText) {
		if (sText != null) {
			try {
				byte[] bData = sText.getBytes("utf-8");
				log(bData);
			} catch (Exception e) {
			}
		}
	}

	public void logNoDate(String sText) {
		if (sText != null) {
			try {
				byte[] bData = sText.getBytes("utf-8");
				ByteBuffer bb = ByteBuffer.allocate(bData.length + ciExtendTextlength);
				writeData(DateAction.getNowDays(), bb);
			} catch (Exception e) {
			}
		}
	}
}
