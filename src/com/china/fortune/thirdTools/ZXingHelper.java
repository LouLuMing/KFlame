package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Hashtable;

public class ZXingHelper {
	public void createQRCode(String sContent, int iWitdh, String sFile) {
		int width = iWitdh;
		int height = iWitdh;
		String format = "png";
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MAX_SIZE, 350);
		hints.put(EncodeHintType.MIN_SIZE, 100);
		hints.put(EncodeHintType.MARGIN, 1);
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(sContent, BarcodeFormat.QR_CODE, width, height, hints);
			File outputFile = new File(sFile);
			MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
		} catch (Exception e) {
			Log.logException(e);
		}
	}
	
	public byte[] createQRCode(String sContent, int iWitdh) {
		int width = iWitdh;
		int height = iWitdh;
		String format = "png";
		byte[] bBody = null;
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MAX_SIZE, 350);
		hints.put(EncodeHintType.MIN_SIZE, 100);
		hints.put(EncodeHintType.MARGIN, 1);
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(sContent, BarcodeFormat.QR_CODE, width, height, hints);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, format, bout);
			bBody = bout.toByteArray();
			bout.close();
		} catch (Exception e) {
			Log.logException(e);
		}
		return bBody;
	}
}
