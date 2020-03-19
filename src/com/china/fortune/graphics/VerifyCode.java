package com.china.fortune.graphics;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class VerifyCode {
	// 使用到Algerian字体，系统里没有的话需要安装字体，字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
	public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

	/**
	 * 使用系统默认字符源生成验证码
	 * 
	 * @param verifySize
	 *            验证码长度
	 * @return
	 */
	public static String generateVerifyCode(int verifySize) {
		return generateVerifyCode(verifySize, VERIFY_CODES);
	}

	/**
	 * 使用指定源生成验证码
	 * 
	 * @param verifySize
	 *            验证码长度
	 * @param sources
	 *            验证码字符源
	 * @return
	 */
	public static String generateVerifyCode(int verifySize, String sources) {
		if (sources == null || sources.length() == 0) {
			sources = VERIFY_CODES;
		}
		int codesLen = sources.length();
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder verifyCode = new StringBuilder(verifySize);
		for (int i = 0; i < verifySize; i++) {
			verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
		}
		return verifyCode.toString();
	}

	/**
	 * 生成随机验证码文件,并返回验证码值
	 * 
	 * @param w
	 * @param h
	 * @param outputFile
	 * @param verifySize
	 * @return
	 * @throws IOException
	 */
	public static String outputVerifyImage(int w, int h, File outputFile, int verifySize) throws IOException {
		String verifyCode = generateVerifyCode(verifySize);
		VerifyCodeImage.outputImage(w, h, outputFile, verifyCode);
		return verifyCode;
	}

	/**
	 * 输出随机验证码图片流,并返回验证码值
	 * 
	 * @param w
	 * @param h
	 * @param os
	 * @param verifySize
	 * @return
	 * @throws IOException
	 */
	public static String outputVerifyImage(int w, int h, OutputStream os, int verifySize) throws IOException {
		String verifyCode = generateVerifyCode(verifySize);
		VerifyCodeImage.outputImage(w, h, os, verifyCode);
		return verifyCode;
	}

	public static void main(String[] args) throws IOException {
//		String sDesPath = PathHelper.getCurrentDataPath(true);
//		File dir = new File(sDesPath);
		int w = 200, h = 80;
		while (true) {
			String verifyCode = generateVerifyCode(4);
			VerifyCodeImage.outputImage(w, h, verifyCode);
//			File file = new File(dir, verifyCode + ".jpg");
//			VerifyCodeImage.outputImage(w, h, file, verifyCode);
		}
	}
}
