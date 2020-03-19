package com.china.fortune.secure;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;

public class DES {
	static public byte [] encrypt(byte[] bData, byte[] bKey, byte iv[]) {
		byte [] encryptedData = null;
		try {
			IvParameterSpec zeroIv =  new IvParameterSpec(iv); 
			SecretKeySpec key = new SecretKeySpec(bKey, "DES");     
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");     
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);     
			encryptedData = cipher.doFinal(bData);
		} catch (Exception e) {
			Log.log(e.getMessage());
		}
		return encryptedData;
	}
	
	static public byte [] encrypt(byte[] bData, byte[] DES_KEY) {
		byte [] encryptedData = null;
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(DES_KEY);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key, sr);
			encryptedData = cipher.doFinal(bData);
		} catch (Exception e) {
		}
		return encryptedData;
	}
	
	public static String encryptBase64(String sData, byte[] DES_KEY) {
		String encryptedData = null;
		byte[] bResult = encrypt(sData.getBytes(), DES_KEY);
		if (bResult != null) {
			encryptedData = Base64.getEncoder().encodeToString(bResult);
		}
		return encryptedData;
	}
	
	public static String encryptHex(String sData, byte[] DES_KEY) {
		String sBase64 = null;
		byte [] encryptedData = encrypt(sData.getBytes(), DES_KEY);
		if (encryptedData != null) {
			sBase64 = ByteAction.toHexStringLower(encryptedData);
		}
		return sBase64;
	}
	
	public static byte[] decrypt(byte[] bData, byte[] DES_KEY) {
		byte [] decryptedData = null;
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec deskey = new DESKeySpec(DES_KEY);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(deskey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key, sr);
			decryptedData = cipher.doFinal(bData);
		} catch (Exception e) {
		}
		return decryptedData;
	}
	
	public static String decryptBase64(String cryptData, byte[] DES_KEY) {
		String encryptedData = null;
		byte[] bData = Base64.getDecoder().decode(cryptData);
		byte[] bResult = decrypt(bData, DES_KEY);
		if (bResult != null) {
			encryptedData = new String(bResult);
		}
		return encryptedData;
	}
}
