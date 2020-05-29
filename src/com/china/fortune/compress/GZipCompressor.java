package com.china.fortune.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.china.fortune.global.Log;

public class GZipCompressor {

	public static final int BUFFER = 1024;
	public static final String EXT = ".gz";

	private static void closeInputStream(ByteArrayInputStream is) {
		try {
			is.close();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	private static void closeOutputStream(ByteArrayOutputStream os) {
		try {
			os.close();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	public static byte[] compress(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		compress(bais, baos);
		byte[] output = baos.toByteArray();

		closeInputStream(bais);
		closeOutputStream(baos);

		return output;
	}

	public static void compress(File file) throws Exception {
		compress(file, true);
	}

	public static void compress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);

		compress(fis, fos);

		fis.close();
		fos.flush();
		fos.close();

		if (delete) {
			file.delete();
		}
	}

	public static void compress(InputStream is, OutputStream os) {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = is.read(data, 0, BUFFER)) != -1) {
				gos.write(data, 0, count);
			}
			gos.finish();
			gos.flush();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		if (gos != null) {
			try {
				gos.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}

//	public static void compress(InputStream is, OutputStream os) {
//		GZIPOutputStream gos = null;
//		try {
//			gos = new GZIPOutputStream(os);
//			int count;
//			byte data[] = new byte[BUFFER];
//			while ((count = is.read(data, 0, BUFFER)) != -1) {
//				gos.write(data, 0, count);
//			}
//			gos.finish();
//			gos.flush();
//		} catch (Exception e) {
//			Log.logClass(e.getMessage());
//		}
//		if (gos != null) {
//			try {
//				gos.close();
//			} catch (Exception e) {
//				Log.logClass(e.getMessage());
//			}
//		}
//	}

	public static void compress(String path) throws Exception {
		compress(path, true);
	}

	public static void compress(String path, boolean delete) throws Exception {
		File file = new File(path);
		compress(file, delete);
	}

	public static byte[] decompress(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		decompress(bais, baos);

		data = baos.toByteArray();
		
		closeInputStream(bais);
		closeOutputStream(baos);

		return data;
	}

	public static void decompress(File file) throws Exception {
		decompress(file, true);
	}

	public static void decompress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath().replace(EXT, ""));
		decompress(fis, fos);
		fis.close();
		fos.flush();
		fos.close();

		if (delete) {
			file.delete();
		}
	}

	public static void decompress(InputStream is, OutputStream os) {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = gis.read(data, 0, BUFFER)) != -1) {
				os.write(data, 0, count);
			}
		} catch (Exception e) {
			Log.logException(e);
		}
		try {
			if (gis != null) {
				gis.close();
			}
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public static void decompress(String path) throws Exception {
		decompress(path, true);
	}

	public static void decompress(String path, boolean delete) throws Exception {
		File file = new File(path);
		decompress(file, delete);
	}
}
